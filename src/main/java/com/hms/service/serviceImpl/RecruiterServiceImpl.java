package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.dto.NotificationEvent;
import com.hms.service.dto.RecruiterCardsCountDto;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.RecruiterAssignmentEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.RecruiterAssignmentRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.FilterRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateRecruitersAssignmentRequest;
import com.hms.service.service.INotificationService;
import com.hms.service.service.IRecruiterService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RecruiterServiceImpl implements IRecruiterService {
	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private RecruiterAssignmentRepository recruiterAssignmentRepository;

	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private AssignRolesRepository assignRolesRepository;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private INotificationService notificationService;

	@Override
	public ApiResponse<?> getRecruiterCardsCounts() {

		log.info("RecruiterDashboardServiceImpl ::Inside getRecruiterCardsCounts method");

		try {

			RecruiterCardsCountDto responseDto = new RecruiterCardsCountDto();

			responseDto.setTotalJobs(createJobDetailsRepository.countBySubmitTrue());

			responseDto.setTotalAssignees(recruiterAssignmentRepository.countByIdIsNotNull());

			Object result = recruiterAssignmentRepository.getStatusCounts();

			Object[] counts = (Object[]) result;

			responseDto.setAcceptedCount(counts[0] != null ? ((Number) counts[0]).longValue() : 0L);

			responseDto.setDeclinedCount(counts[1] != null ? ((Number) counts[1]).longValue() : 0L);

			responseDto.setPendingCount(counts[2] != null ? ((Number) counts[2]).longValue() : 0L);

			log.info("RecruiterDashboardServiceImpl :: Dashboard counts fetched successfully");

			return ApiResponse.success(ResponseCode.SUCCESS, "Dashboard counts fetched successfully", responseDto);

		} catch (Exception ex) {

			log.error("Exception occurred while fetching dashboard counts", ex);

			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch dashboard counts");
		}
	}

	@Override
	public ApiResponse<?> getAllRecruiterAssignmentList(SpecificationFilterRequest request) {

		log.info("RecruiterDashboardServiceImpl ::Inside getAllRecruiterAssignmentList method");

		try {

			int page = request.getPage() != null ? request.getPage() : 0;

			int size = request.getSize() != null ? request.getSize() : 10;

			String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";

			Sort.Direction direction = "ASC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Specification<CreateJobDetailsEntity> spec = request.buildJobSpecification();

			Page<CreateJobDetailsEntity> pageResult = createJobDetailsRepository.findAll(spec, pageable);

			Map<Integer, String> departmentMap = departmentsRepository.findAll().stream()
					.collect(Collectors.toMap(DepartmentsEntity::getId, DepartmentsEntity::getDepartmentName));

			List<Map<String, Object>> content = pageResult.getContent().stream().map(job -> {

				Map<String, Object> map = new LinkedHashMap<>();

				List<RecruiterAssignmentEntity> assignments = recruiterAssignmentRepository.findByJobId(job.getId());

				long acceptedCount = assignments.stream().filter(a -> "ACCEPTED".equalsIgnoreCase(a.getStatus()))
						.count();

				long pendingCount = assignments.stream().filter(a -> "PENDING".equalsIgnoreCase(a.getStatus())).count();

				long declinedCount = assignments.stream().filter(a -> "DECLINED".equalsIgnoreCase(a.getStatus()))
						.count();

				map.put("id", job.getId());

				map.put("jobTitle", job.getJobTitle());

				map.put("jobCode", job.getJobCode());

				map.put("departmentName", departmentMap.get(job.getDepartmentId()));

				map.put("targetStartDate", job.getTargetStartDate());

				map.put("assignees", assignments.size());

				map.put("acceptedCount", acceptedCount);

				map.put("pendingCount", pendingCount);

				map.put("declinedCount", declinedCount);

				map.put("workMode", job.getWorkMode());

				map.put("employmentType", job.getEmploymentType());

				map.put("location", job.getLocation());

				return map;

			}).toList();

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("content", content);

			response.put("currentPage", pageResult.getNumber());

			response.put("totalPages", pageResult.getTotalPages());

			response.put("totalElements", pageResult.getTotalElements());

			return ApiResponse.success(ResponseCode.SUCCESS, "Job list fetched successfully", response);

		} catch (Exception e) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch job list", List.of(e.getMessage()));
		}
	}

	@Override
	public ApiResponse<?> getRecruiterAssignmentSummary(Integer jobId) {

		try {

			List<RecruiterAssignmentEntity> assignments = recruiterAssignmentRepository.findByJobId(jobId);

			long acceptedCount = assignments.stream().filter(a -> "ACCEPTED".equalsIgnoreCase(a.getStatus())).count();

			long pendingCount = assignments.stream().filter(a -> "PENDING".equalsIgnoreCase(a.getStatus())).count();

			long declinedCount = assignments.stream().filter(a -> "DECLINED".equalsIgnoreCase(a.getStatus())).count();

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("totalAssigned", assignments.size());

			response.put("acceptedCount", acceptedCount);

			response.put("pendingCount", pendingCount);

			response.put("declinedCount", declinedCount);

			return ApiResponse.success(ResponseCode.SUCCESS, "Assignment cards summary fetched successfully", response);

		} catch (Exception e) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch assignment summary",
					List.of(e.getMessage()));
		}
	}

	@Override
	public ApiResponse<?> getRecruiterAssignmentDetailsList(Integer jobId, FilterRequest request) {

		try {

			int page = request.getPage() != null ? request.getPage() : 0;

			int size = request.getSize() != null ? request.getSize() : 10;

			String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";

			Sort.Direction direction = "ASC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<RecruiterAssignmentEntity> assignmentPage = recruiterAssignmentRepository.findByJobId(jobId, pageable);

			List<Map<String, Object>> recruiterList = assignmentPage.getContent().stream().map(a -> {

				Map<String, Object> map = new LinkedHashMap<>();

				UserEntity user = userRepository.findByUserId(a.getUserId()).orElse(null);

				AssignRolesEntity assignRole = assignRolesRepository.findByUserId(a.getUserId()).orElse(null);

				String roleName = null;

				if (assignRole != null) {

					RolesEntity role = rolesRepository.findByRoleId(assignRole.getRoleId()).orElse(null);

					if (role != null) {

						roleName = role.getRoleName();
					}
				}

				map.put("id", a.getId());

				map.put("userId", a.getUserId());

				map.put("recruiterName", user != null ? user.getFirstName() : null);

				map.put("email", user != null ? user.getEmail() : null);

				map.put("role", roleName);

				map.put("assignedOn", a.getAssignedAt());

				map.put("respondedOn", a.getRespondedAt());

				map.put("status", a.getStatus());

				map.put("comments", a.getComments());

				return map;

			}).toList();

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("content", recruiterList);

			response.put("currentPage", assignmentPage.getNumber());

			response.put("totalPages", assignmentPage.getTotalPages());

			response.put("totalElements", assignmentPage.getTotalElements());

			return ApiResponse.success(ResponseCode.SUCCESS, "Recruiter assignment list fetched successfully",
					response);

		} catch (Exception e) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch recruiter assignment list",
					List.of(e.getMessage()));
		}
	}

	@Override
	public ApiResponse<?> getMyJobAssignmentsCounts() {

		String authHeader = httpServletRequest.getHeader("Authorization");

		Long userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token);

		} else {

			return ApiResponse.failure(ResponseCode.FAILURE, "Unauthorized", List.of("Missing or invalid token"));
		}

		Integer recruiterUserId = userId.intValue();

		Long totalAssignments = recruiterAssignmentRepository.countByUserId(recruiterUserId);

		List<Object[]> statusCounts = recruiterAssignmentRepository.getStatusCountsByUserId(recruiterUserId);

		Long accepted = 0L;
		Long pending = 0L;
		Long declined = 0L;

		for (Object[] row : statusCounts) {

			String status = (String) row[0];

			Long count = (Long) row[1];

			if ("Accepted".equalsIgnoreCase(status)) {

				accepted = count;

			} else if ("Pending".equalsIgnoreCase(status)) {

				pending = count;

			} else if ("Declined".equalsIgnoreCase(status)) {

				declined = count;
			}
		}

		Long totalOpenings = createJobDetailsRepository.getTotalOpeningsByUserId(recruiterUserId);

		Map<String, Object> response = new HashMap<>();

		response.put("totalAssignments", totalAssignments);

		response.put("accepted", accepted);

		response.put("pending", pending);

		response.put("declined", declined);

		response.put("totalOpenings", totalOpenings);

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	public ApiResponse<?> getMyJobAssignments(SpecificationFilterRequest request) {

		try {

			int page = request.getPage() != null ? request.getPage() : 0;

			int size = request.getSize() != null ? request.getSize() : 10;

			String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";

			String direction = request.getDirection() != null ? request.getDirection() : "DESC";

			Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

			Pageable pageable = PageRequest.of(page, size, sort);

			String authHeader = httpServletRequest.getHeader("Authorization");

			Long userId = null;

			if (authHeader != null && authHeader.startsWith("Bearer ")) {

				String token = authHeader.substring(7);

				userId = jwtService.extractUserId(token);
			}

			if (userId == null) {

				return ApiResponse.failure("User not found");
			}

			Integer recruiterId = userId.intValue();

			// ALL ASSIGNMENTS OF USER
			List<RecruiterAssignmentEntity> allAssignments = recruiterAssignmentRepository.findAllByUserId(recruiterId);

			// GET ALL SR IDS
			List<Integer> allJobIds = allAssignments.stream().map(RecruiterAssignmentEntity::getId).distinct().toList();

			if (allJobIds.isEmpty()) {

				Map<String, Object> emptyResponse = new HashMap<>();

				emptyResponse.put("counts", Map.of("all", 0, "pending", 0, "accepted", 0, "declined", 0));

				emptyResponse.put("content", List.of());

				emptyResponse.put("currentPage", 0);

				emptyResponse.put("totalPages", 0);

				emptyResponse.put("totalElements", 0);

				emptyResponse.put("size", size);

				return ApiResponse.success(ResponseCode.SUCCESS, "No Data Found", emptyResponse);
			}

			// BUILD SPECIFICATION
			Specification<CreateJobDetailsEntity> specification = request.buildMyRecruiterSpecification(allJobIds);

			// GET FILTERED JOBS (WITHOUT PAGINATION) FOR COUNTS
			List<CreateJobDetailsEntity> filteredJobs = createJobDetailsRepository.findAll(specification);

			// FILTERED SR IDS
			List<Integer> filteredJobIds = filteredJobs.stream().map(CreateJobDetailsEntity::getId).toList();

			// FILTER ASSIGNMENTS BASED ON FILTERED SR IDS
			List<RecruiterAssignmentEntity> filteredAssignments = allAssignments.stream()
					.filter(a -> filteredJobIds.contains(a.getId())).toList();

			// STATUS FILTER FOR CONTENT
			String status = request.getStatus();

			List<RecruiterAssignmentEntity> statusFilteredAssignments;

			if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {

				statusFilteredAssignments = filteredAssignments.stream()
						.filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase(status)).toList();

			} else {

				statusFilteredAssignments = filteredAssignments;
			}

			// COUNTS
			Map<String, Object> counts = new HashMap<>();

			counts.put("all", filteredAssignments.size());

			counts.put("pending",
					filteredAssignments.stream().filter(a -> "Pending".equalsIgnoreCase(a.getStatus())).count());

			counts.put("accepted",
					filteredAssignments.stream().filter(a -> "Accepted".equalsIgnoreCase(a.getStatus())).count());

			counts.put("declined",
					filteredAssignments.stream().filter(a -> "Declined".equalsIgnoreCase(a.getStatus())).count());

			// SR IDS AFTER STATUS FILTER
			List<Integer> finalJobIds = statusFilteredAssignments.stream().map(RecruiterAssignmentEntity::getId)
					.distinct().toList();

			if (finalJobIds.isEmpty()) {

				Map<String, Object> emptyResponse = new HashMap<>();

				emptyResponse.put("counts", counts);

				emptyResponse.put("content", List.of());

				emptyResponse.put("currentPage", 0);

				emptyResponse.put("totalPages", 0);

				emptyResponse.put("totalElements", 0);

				emptyResponse.put("size", size);

				return ApiResponse.success(ResponseCode.SUCCESS, "No Data Found", emptyResponse);
			}

			// FINAL SPECIFICATION WITH STATUS FILTERED SR IDS
			Specification<CreateJobDetailsEntity> finalSpecification = request
					.buildMyRecruiterSpecification(finalJobIds);

			// PAGINATED RESULT
			Page<CreateJobDetailsEntity> pageResult = createJobDetailsRepository.findAll(finalSpecification, pageable);

			// MAP SR ID -> ASSIGNMENT
			Map<Integer, RecruiterAssignmentEntity> assignmentMap = statusFilteredAssignments.stream()
					.collect(Collectors.toMap(RecruiterAssignmentEntity::getId, assignment -> assignment));

			// RESPONSE CONTENT
			List<Map<String, Object>> responseList = pageResult.getContent().stream().map(job -> {

				Map<String, Object> map = new HashMap<>();

				RecruiterAssignmentEntity assignment = assignmentMap.get(job.getId());

				map.put("id", job.getId());

//						map.put("srId", job.getSrId());

				map.put("jobTitle", job.getJobTitle());

				String departmentName = departmentsRepository.findById(job.getDepartmentId()).get().getDepartmentName();

				map.put("departmentName", departmentName);

				map.put("requestedBy", job.getCreatedBy());

				map.put("openings", job.getOpenings());

				map.put("createdAt", job.getCreatedAt());

				map.put("status", assignment != null ? assignment.getStatus() : null);

				return map;

			}).toList();

			// FINAL RESPONSE
			Map<String, Object> response = new HashMap<>();

			response.put("counts", counts);

			response.put("content", responseList);

			response.put("currentPage", pageResult.getNumber());

			response.put("totalPages", pageResult.getTotalPages());

			response.put("totalElements", pageResult.getTotalElements());

			response.put("size", pageResult.getSize());

			return ApiResponse.success(ResponseCode.SUCCESS, "Create Job details fetched successfully", response);

		} catch (Exception e) {

			e.printStackTrace();

			return ApiResponse.failure("Failed to fetch jobs");
		}
	}

	@Override
	public ApiResponse<?> updateRecruiterAssignment(UpdateRecruitersAssignmentRequest request) {
		String authHeader = httpServletRequest.getHeader("Authorization");
		// String userName = "";
		String roleName = "";
		Long userId = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			roleName = jwtService.extractRole(token);
			userId = jwtService.extractUserId(token);
			log.info("token userId"+userId);

		}
		List<RecruiterAssignmentEntity> recruiterAssignmentList =
		        recruiterAssignmentRepository.findByJobId(request.getJobId());

		if (recruiterAssignmentList.isEmpty()) {
		    return ApiResponse.failure(ResponseCode.FAILURE, "No assignment found");
		}

		RecruiterAssignmentEntity recruiterAssignmentEntity =
		        recruiterAssignmentList.get(0);

		Long assignedUserId =
		        recruiterAssignmentEntity.getUserId().longValue();

		log.info("assigned userId is " + assignedUserId);

		if (assignedUserId.equals(userId)) {

		    recruiterAssignmentEntity.setStatus(request.getStatus());
		    recruiterAssignmentEntity.setComments(request.getComments());
		    recruiterAssignmentEntity.setRespondedAt(LocalDateTime.now());

		    recruiterAssignmentRepository.save(recruiterAssignmentEntity);
		}
	    Integer departmentId = createJobDetailsRepository.findById(request.getJobId()).get().getDepartmentId();
		String departmentName = departmentsRepository.findById(departmentId).get().getDepartmentName();
			
		Map<Integer, List<String>> roleEmailMap = new HashMap<>();

		Integer roleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();
		log.info("role id is" + roleId);
		String emails = userRepository.findByUserId(userId).get().getEmail();

		roleEmailMap.put(roleId, List.of(emails));

		log.info("Role Email Map : {}", roleEmailMap);

		NotificationEvent event = new NotificationEvent();
		event.setProcessId(recruiterAssignmentEntity.getJobId().toString());
		event.setType("Recruiters");
		event.setDeptName(departmentName);
		Integer makerRoleId = recruiterAssignmentEntity.getRoleId();
		event.setMakerRoleId(makerRoleId);
		event.setMakerRoleName(recruiterAssignmentEntity.getRoleName());
		event.setMakerMessage("accepted");
		Integer usersId = assignRolesRepository.findByRoleId(makerRoleId).get(0).getUserId();

		String makerEmail = userRepository.findByUserId(usersId).get().getEmail();
		log.info("maker email is" + makerEmail);
		event.setMakerEmailAddress(makerEmail);
		event.setMakerEmailBody("accepted");
		event.setMakerNotificationTitle("assignment accepted");
		event.setCheckerNotificationTitle("assignment");
		event.setCheckerMessage("accepted");
		event.setCheckerEmailBody("accepted");
		event.setRoleEmailMap(roleEmailMap);
		event.setCheckerRoleName(roleName);
		notificationService.callNotification(event);
		log.info("the event is " + event);

		return ApiResponse.success(ResponseCode.SUCCESS, "success", "updated successfully");

	}

	@Override
	public ApiResponse<?> getAssignedRecruiterUserIds(Integer jobId) {

		log.info("RecruiterDashboardServiceImpl ::Inside getAssignedRecruiterUserIds method");

		try {
			List<RecruiterAssignmentEntity> assignments = recruiterAssignmentRepository.findByJobId(jobId);
			Set<Integer> userIds = assignments.stream().map(RecruiterAssignmentEntity::getUserId).collect(Collectors.toSet());
			Map<String, Object> response = new LinkedHashMap<>();
			response.put("jobId", jobId);
			response.put("userIds", userIds);
			return ApiResponse.success(ResponseCode.SUCCESS, "Recruiter userIds fetched successfully", response);

		} catch (Exception e) {
			log.error("Exception occurred while fetching recruiter userIds", e);
			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch recruiter userIds",List.of(e.getMessage()));
		}
	}
}

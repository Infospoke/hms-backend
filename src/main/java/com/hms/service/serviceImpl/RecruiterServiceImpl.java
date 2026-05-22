package com.hms.service.serviceImpl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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

				List<RecruiterAssignmentEntity> assignments = recruiterAssignmentRepository.findBySrId(job.getSrId());

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

	public ApiResponse<?> getRecruiterAssignmentSummary(String srId) {

		try {

			List<RecruiterAssignmentEntity> assignments = recruiterAssignmentRepository.findBySrId(srId);

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
	public ApiResponse<?> getRecruiterAssignmentDetailsList(String srId, FilterRequest request) {

		try {

			int page = request.getPage() != null ? request.getPage() : 0;

			int size = request.getSize() != null ? request.getSize() : 10;

			String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";

			Sort.Direction direction = "ASC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<RecruiterAssignmentEntity> assignmentPage = recruiterAssignmentRepository.findBySrId(srId, pageable);

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

//	@Override
//	public ApiResponse<?> getMyJobAssignments(SpecificationFilterRequest request) {
//
//		try {
//
//			int page = request.getPage() != null ? request.getPage() : 0;
//
//			int size = request.getSize() != null ? request.getSize() : 10;
//
//			String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
//
//			String direction = request.getDirection() != null ? request.getDirection() : "DESC";
//
//			Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//
//			Pageable pageable = PageRequest.of(page, size, sort);
//
//			String authHeader = httpServletRequest.getHeader("Authorization");
//
//			Long userId = null;
//
//			if (authHeader != null && authHeader.startsWith("Bearer ")) {
//
//				String token = authHeader.substring(7);
//
//				userId = jwtService.extractUserId(token);
//			}
//
//			if (userId == null) {
//
//				return ApiResponse.failure("User not found");
//			}
//
//			Integer recruiterId = userId.intValue();
//
//			List<RecruiterAssignmentEntity> allAssignments = recruiterAssignmentRepository.findAllByUserId(recruiterId);
//
//			List<RecruiterAssignmentEntity> filteredAssignments;
//
//			String status = request.getStatus();
//
//			if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
//
//				filteredAssignments = allAssignments.stream()
//						.filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase(status)).toList();
//
//			} else {
//
//				filteredAssignments = allAssignments;
//			}
//
//			Map<String, Object> counts = new HashMap<>();
//
//			counts.put("all", allAssignments.size());
//
//			counts.put("pending",
//					allAssignments.stream().filter(a -> "Pending".equalsIgnoreCase(a.getStatus())).count());
//
//			counts.put("accepted",
//					allAssignments.stream().filter(a -> "Accepted".equalsIgnoreCase(a.getStatus())).count());
//
//			counts.put("declined",
//					allAssignments.stream().filter(a -> "Declined".equalsIgnoreCase(a.getStatus())).count());
//
//			List<String> srIds = filteredAssignments.stream().map(RecruiterAssignmentEntity::getSrId).distinct()
//					.toList();
//
//			if (srIds.isEmpty()) {
//
//				Map<String, Object> emptyResponse = new HashMap<>();
//
//				emptyResponse.put("counts", counts);
//
//				emptyResponse.put("content", List.of());
//
//				emptyResponse.put("currentPage", 0);
//
//				emptyResponse.put("totalPages", 0);
//
//				emptyResponse.put("totalElements", 0);
//
//				emptyResponse.put("size", size);
//
//				return ApiResponse.success(ResponseCode.SUCCESS, "No Data Found", emptyResponse);
//			}
//
//			Specification<CreateJobDetailsEntity> specification = request.buildMyRecruiterSpecification(srIds);
//
//			Page<CreateJobDetailsEntity> pageResult = createJobDetailsRepository.findAll(specification, pageable);
//
//			Map<String, RecruiterAssignmentEntity> assignmentMap = filteredAssignments.stream()
//					.collect(Collectors.toMap(RecruiterAssignmentEntity::getSrId, assignment -> assignment));
//
//			List<Map<String, Object>> responseList = pageResult.getContent().stream().map(job -> {
//
//				Map<String, Object> map = new HashMap<>();
//
//				RecruiterAssignmentEntity assignment = assignmentMap.get(job.getSrId());
//
//				map.put("id", job.getId());
//
//				map.put("jobTitle", job.getJobTitle());
//
//				String departmentName = departmentsRepository.findById(job.getDepartmentId()).get().getDepartmentName();
//
//				map.put("departmentName", departmentName);
//				map.put("requestedBy", job.getCreatedBy());
//
//				map.put("openings", job.getOpenings());
//
//				map.put("createdAt", job.getCreatedAt());
//
//				map.put("status", assignment != null ? assignment.getStatus() : null);
//
//				return map;
//
//			}).toList();
//
//			Map<String, Object> response = new HashMap<>();
//
//			response.put("counts", counts);
//
//			response.put("content", responseList);
//
//			response.put("currentPage", pageResult.getNumber());
//
//			response.put("totalPages", pageResult.getTotalPages());
//
//			response.put("totalElements", pageResult.getTotalElements());
//
//			response.put("size", pageResult.getSize());
//
//			return ApiResponse.success(ResponseCode.SUCCESS, "Create Job details fetched successfully", response);
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			return ApiResponse.failure("Failed to fetch jobs");
//		}
//	}
	
	@Override
	public ApiResponse<?> getMyJobAssignments(SpecificationFilterRequest request) {

		try {

			int page = request.getPage() != null ? request.getPage() : 0;

			int size = request.getSize() != null ? request.getSize() : 10;

			String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";

			String direction = request.getDirection() != null ? request.getDirection() : "DESC";

			Sort sort = direction.equalsIgnoreCase("ASC")
					? Sort.by(sortBy).ascending()
					: Sort.by(sortBy).descending();

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
			List<RecruiterAssignmentEntity> allAssignments =
					recruiterAssignmentRepository.findAllByUserId(recruiterId);

			// GET ALL SR IDS
			List<String> allSrIds = allAssignments.stream()
					.map(RecruiterAssignmentEntity::getSrId)
					.distinct()
					.toList();

			if (allSrIds.isEmpty()) {

				Map<String, Object> emptyResponse = new HashMap<>();

				emptyResponse.put("counts", Map.of(
						"all", 0,
						"pending", 0,
						"accepted", 0,
						"declined", 0
				));

				emptyResponse.put("content", List.of());

				emptyResponse.put("currentPage", 0);

				emptyResponse.put("totalPages", 0);

				emptyResponse.put("totalElements", 0);

				emptyResponse.put("size", size);

				return ApiResponse.success(
						ResponseCode.SUCCESS,
						"No Data Found",
						emptyResponse
				);
			}

			// BUILD SPECIFICATION
			Specification<CreateJobDetailsEntity> specification =
					request.buildMyRecruiterSpecification(allSrIds);

			// GET FILTERED JOBS (WITHOUT PAGINATION) FOR COUNTS
			List<CreateJobDetailsEntity> filteredJobs =
					createJobDetailsRepository.findAll(specification);

			// FILTERED SR IDS
			List<String> filteredSrIds = filteredJobs.stream()
					.map(CreateJobDetailsEntity::getSrId)
					.toList();

			// FILTER ASSIGNMENTS BASED ON FILTERED SR IDS
			List<RecruiterAssignmentEntity> filteredAssignments =
					allAssignments.stream()
							.filter(a -> filteredSrIds.contains(a.getSrId()))
							.toList();

			// STATUS FILTER FOR CONTENT
			String status = request.getStatus();

			List<RecruiterAssignmentEntity> statusFilteredAssignments;

			if (status != null
					&& !status.isBlank()
					&& !"ALL".equalsIgnoreCase(status)) {

				statusFilteredAssignments = filteredAssignments.stream()
						.filter(a -> a.getStatus() != null
								&& a.getStatus().equalsIgnoreCase(status))
						.toList();

			} else {

				statusFilteredAssignments = filteredAssignments;
			}

			// COUNTS
			Map<String, Object> counts = new HashMap<>();

			counts.put("all", filteredAssignments.size());

			counts.put("pending",
					filteredAssignments.stream()
							.filter(a -> "Pending".equalsIgnoreCase(a.getStatus()))
							.count());

			counts.put("accepted",
					filteredAssignments.stream()
							.filter(a -> "Accepted".equalsIgnoreCase(a.getStatus()))
							.count());

			counts.put("declined",
					filteredAssignments.stream()
							.filter(a -> "Declined".equalsIgnoreCase(a.getStatus()))
							.count());

			// SR IDS AFTER STATUS FILTER
			List<String> finalSrIds = statusFilteredAssignments.stream()
					.map(RecruiterAssignmentEntity::getSrId)
					.distinct()
					.toList();

			if (finalSrIds.isEmpty()) {

				Map<String, Object> emptyResponse = new HashMap<>();

				emptyResponse.put("counts", counts);

				emptyResponse.put("content", List.of());

				emptyResponse.put("currentPage", 0);

				emptyResponse.put("totalPages", 0);

				emptyResponse.put("totalElements", 0);

				emptyResponse.put("size", size);

				return ApiResponse.success(
						ResponseCode.SUCCESS,
						"No Data Found",
						emptyResponse
				);
			}

			// FINAL SPECIFICATION WITH STATUS FILTERED SR IDS
			Specification<CreateJobDetailsEntity> finalSpecification =
					request.buildMyRecruiterSpecification(finalSrIds);

			// PAGINATED RESULT
			Page<CreateJobDetailsEntity> pageResult =
					createJobDetailsRepository.findAll(finalSpecification, pageable);

			// MAP SR ID -> ASSIGNMENT
			Map<String, RecruiterAssignmentEntity> assignmentMap =
					statusFilteredAssignments.stream()
							.collect(Collectors.toMap(
									RecruiterAssignmentEntity::getSrId,
									assignment -> assignment
							));

			// RESPONSE CONTENT
			List<Map<String, Object>> responseList =
					pageResult.getContent().stream().map(job -> {

						Map<String, Object> map = new HashMap<>();

						RecruiterAssignmentEntity assignment =
								assignmentMap.get(job.getSrId());

						map.put("id", job.getId());

						map.put("srId", job.getSrId());

						map.put("jobTitle", job.getJobTitle());

						String departmentName = departmentsRepository
								.findById(job.getDepartmentId())
								.get()
								.getDepartmentName();

						map.put("departmentName", departmentName);

						map.put("requestedBy", job.getCreatedBy());

						map.put("openings", job.getOpenings());

						map.put("createdAt", job.getCreatedAt());

						map.put("status",
								assignment != null
										? assignment.getStatus()
										: null);

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

			return ApiResponse.success(
					ResponseCode.SUCCESS,
					"Create Job details fetched successfully",
					response
			);

		} catch (Exception e) {

			e.printStackTrace();

			return ApiResponse.failure("Failed to fetch jobs");
		}
	}

}

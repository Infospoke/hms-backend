package com.hms.service.serviceImpl;

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
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.IRecruiterService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

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
	public ApiResponse<?> getRecruiterAssignmentDetails(Integer jobId) {
		
		log.info("RecruiterDashboardServiceImpl ::Inside getRecruiterAssignmentDetails method");

		try {

			CreateJobDetailsEntity job = createJobDetailsRepository.findById(jobId)
					.orElseThrow(() -> new RuntimeException("Job not found"));

			List<RecruiterAssignmentEntity> assignments = recruiterAssignmentRepository.findByJobId(jobId);

			long acceptedCount = assignments.stream().filter(a -> "ACCEPTED".equalsIgnoreCase(a.getStatus())).count();

			long pendingCount = assignments.stream().filter(a -> "PENDING".equalsIgnoreCase(a.getStatus())).count();

			long declinedCount = assignments.stream().filter(a -> "DECLINED".equalsIgnoreCase(a.getStatus())).count();

			List<Map<String, Object>> recruiterList = assignments.stream().map(a -> {

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

			response.put("totalAssigned", assignments.size());

			response.put("acceptedCount", acceptedCount);

			response.put("pendingCount", pendingCount);

			response.put("declinedCount", declinedCount);

			response.put("recruiters", recruiterList);

			return ApiResponse.success(ResponseCode.SUCCESS, "Job details fetched successfully", response);

		} catch (Exception e) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch job details", List.of(e.getMessage()));
		}
	}
}

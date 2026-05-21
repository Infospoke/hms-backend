package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.JobDescriptionEntity;
import com.hms.service.entity.RecruiterAssignmentEntity;
import com.hms.service.entity.RolesAndRequirementsEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.entity.SourcingChannelEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.BusinessUnitRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.JobDescriptionRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RecruiterAssignmentRepository;
import com.hms.service.repository.RolesAndRequirementsRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.SourcingChannelRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.CreateJobDetailsRequest;
import com.hms.service.request.CreateJobRequest;
import com.hms.service.request.JobCreationReviewRequest;
import com.hms.service.request.JobDescriptionRequest;
import com.hms.service.request.RecuriterAssignmentRequest;
import com.hms.service.request.SourcingChannelRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.response.CreateJobDetailsResponse;
import com.hms.service.response.RecruiterDetailsResponse;
import com.hms.service.service.ICreateJobService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreateJobServiceImpl implements ICreateJobService {

	@Autowired
	private PositionBasicsRepository positionBasicsRepository;

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

//	@Autowired
//	private CreateJobRepository createJobRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private BusinessUnitRepository businessUnitRepository;

	@Autowired
	private JobDescriptionRepository jobDescriptionRepository;

	@Autowired
	private RolesAndRequirementsRepository rolesAndRequirementsRepository;

	@Autowired
	private SourcingChannelRepository sourcingChannelRepository;

	@Autowired
	private RecruiterAssignmentRepository recruiterAssignmentRepository;

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private AssignRolesRepository assignRolesRepository;

	@Autowired
	private UserRepository userRepository;

	private String generateJobCode(String srId) {

		if (srId != null && srId.startsWith("SR-")) {
			return srId.substring(3);
		}

		return srId;
	}

	@Override
	public ApiResponse<?> getJobDetails(String srId) {

		SRPositionBasicsEntity srData = positionBasicsRepository.findBySrId(srId)
				.orElseThrow(() -> new RuntimeException("SR data not found"));

		RolesAndRequirementsEntity rolesData = rolesAndRequirementsRepository.findBySrId(srId)
				.orElseThrow(() -> new RuntimeException("Roles & Requirements data not found"));

		CreateJobDetailsResponse response = new CreateJobDetailsResponse();

		response.setSrId(srData.getSrId());
		response.setJobCode(generateJobCode(srData.getSrId()));
		response.setJobTitle(srData.getJobTitle());
		response.setBusinessUnitId(srData.getBusinessUnitId());
		response.setDepartmentId(srData.getDepartmentId());
		response.setLocation(srData.getLocation());
		response.setWorkMode(srData.getWorkMode());
		response.setEmploymentType(srData.getEmploymentType());
		response.setOpenings(srData.getOpenings());
		response.setTargetStartDate(srData.getTargetStartDate());

		if (rolesData != null) {

			response.setSkillsMustHave(rolesData.getSkillsMustHave());
			response.setNiceToHaveSkills(rolesData.getNiceToHaveSkills());
			response.setMinExperience(rolesData.getMinExperience());
			response.setMaxExperience(rolesData.getMaxExperience());

		}

		return ApiResponse.success(ResponseCode.SUCCESS, "Job Details fetched successfully", response);
	}

	@Override
	public ApiResponse<?> createJob(CreateJobRequest request) {

		// createJob details
		log.info("CreateJobServiceImpl :: Inside the createJob method");

		if (request == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Request body cannot be null"));
		}

		String authHeader = httpServletRequest.getHeader("Authorization");

		String userName = "";

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userName = jwtService.extractUsernameFromClaims(token);
		}

		if (request.getCreateJobDetailsRequest() != null) {

			CreateJobDetailsRequest req = request.getCreateJobDetailsRequest();

			ApiResponse<?> error = validateCreateJobDetailsRequest(req);

			if (error != null) {
				return error;
			}

			CreateJobDetailsEntity entity = null;

			if (req.getJobId() != null) {

				entity = createJobDetailsRepository.findById(req.getJobId()).orElse(null);
			}

			entity.setJobTitle(req.getJobTitle());

			entity.setBusinessUnitId(req.getBusinessUnitId());

			entity.setDepartmentId(req.getDepartmentId());

			entity.setLocation(req.getLocation());

			entity.setJobCode(req.getJobCode());

			entity.setOpenings(req.getOpenings());

			entity.setTargetStartDate(req.getTargetStartDate());

			entity.setWorkMode(req.getWorkMode());

			entity.setEmploymentType(req.getEmploymentType());

			entity.setSkillsMustHave(req.getSkillsMustHave());

			entity.setNiceToHaveSkills(req.getNiceToHaveSkills());

			entity.setMinExperience(req.getMinExperience());

			entity.setMaxExperience(req.getMaxExperience());

			entity.setAdditionalNotes(req.getAdditionalNotes());

			entity.setCreatedBy(userName);

			entity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

			entity = createJobDetailsRepository.save(entity);

			return ApiResponse.success(ResponseCode.SUCCESS, "success", "Job details Saved Successfully");
		}

		// job description

		if (request.getJobDescriptionRequest() != null) {

			JobDescriptionRequest req = request.getJobDescriptionRequest();

			ApiResponse<?> error = validateJobDescriptionRequest(req);

			if (error != null) {
				return error;
			}

			JobDescriptionEntity entity = null;

			if (req.getJobId() != null) {

				entity = jobDescriptionRepository.findByJobId(req.getJobId()).orElse(null);
			}

			if (entity == null) {

				entity = new JobDescriptionEntity();
			}

			entity.setJobId(req.getJobId());

			entity.setDescription(req.getDescription());

			entity = jobDescriptionRepository.save(entity);
			return ApiResponse.success(ResponseCode.SUCCESS, "success", "Job Description Saved Successfully");

		}

		// sourcing channel
		if (request.getSourcingChannelRequest() != null) {

			SourcingChannelRequest req = request.getSourcingChannelRequest();

			ApiResponse<?> error = validateSourcingChannelRequest(req);

			if (error != null) {
				return error;
			}

			SourcingChannelEntity entity = null;

			if (req.getJobId() != null) {

				entity = sourcingChannelRepository.findByJobId(req.getJobId()).orElse(null);
			}

			if (entity == null) {

				entity = new SourcingChannelEntity();
			}

			entity.setJobId(req.getJobId());

			entity.setSourcingChannelRequest(List.of(req));

			entity = sourcingChannelRepository.save(entity);

			return ApiResponse.success(ResponseCode.SUCCESS, "success", "Sourcing Channel Saved Successfully");
		}
		// recuriter assignment

		if (request.getRecuriterAssignmentRequest() != null) {

			RecuriterAssignmentRequest req = request.getRecuriterAssignmentRequest();

			ApiResponse<?> error = validateRecruiterAssignmentRequest(req);

			if (error != null) {
				return error;
			}

			for (Integer userId : req.getUserIds()) {

				RecruiterAssignmentEntity entity = new RecruiterAssignmentEntity();

				entity.setJobId(req.getJobId());

				entity.setUserId(userId);

				entity.setStatus("PENDING");

				entity.setAssignedBy(userName);

				entity.setAssignedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				recruiterAssignmentRepository.save(entity);
			}

			return ApiResponse.success(ResponseCode.SUCCESS, "success", "Recruiters Assigned Successfully");
		}

		// review request

		if (request.getJobCreationReviewRequest() != null) {

			JobCreationReviewRequest req = request.getJobCreationReviewRequest();

			ApiResponse<?> error = validateJobCreationReviewRequest(req);

			if (error != null) {
				return error;
			}

			CreateJobDetailsEntity entity = createJobDetailsRepository.findById(req.getJobId()).orElse(null);

			if (entity == null) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Job Details not found"));
			}

			entity.setSubmit(req.getSubmit());

			entity.setUpdatedBy(userName);

			entity.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

			entity = createJobDetailsRepository.save(entity);

			return ApiResponse.success(ResponseCode.SUCCESS, "success", "Job Review Updated Successfully");
		}
		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Job Created Successfully");
	}

	// Validations for createJobDetailsRequest

	public ApiResponse<?> validateCreateJobDetailsRequest(CreateJobDetailsRequest req) {

		ApiResponse<?> error;

		if (req.getJobTitle() != null) {

			error = validateObject(req.getJobTitle(), "jobTitle");

			if (error != null)
				return error;
		}

		if (req.getBusinessUnitId() != null) {

			error = validateObject(req.getBusinessUnitId(), "businessUnitId");

			if (error != null)
				return error;

			if (!businessUnitRepository.existsById(req.getBusinessUnitId())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Invalid businessUnitId"));
			}
		}

		if (req.getDepartmentId() != null) {

			error = validateObject(req.getDepartmentId(), "departmentId");

			if (error != null)
				return error;

			if (!departmentsRepository.existsById(req.getDepartmentId())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Invalid departmentId"));
			}
		}

		if (req.getLocation() != null) {

			error = validateObject(req.getLocation(), "location");

			if (error != null)
				return error;
		}

		if (req.getJobCode() != null) {

			error = validateObject(req.getJobCode(), "jobCode");

			if (error != null)
				return error;
		}

		if (req.getOpenings() != null) {

			error = validateObject(req.getOpenings(), "openings");

			if (error != null)
				return error;

			if (req.getOpenings() <= 0) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Openings must be greater than 0"));
			}
		}

		if (req.getTargetStartDate() != null) {

			error = validateObject(req.getTargetStartDate().toString(), "targetStartDate");

			if (error != null)
				return error;
		}

		if (req.getWorkMode() != null) {

			error = validateObject(req.getWorkMode(), "workMode");

			if (error != null)
				return error;
		}

		if (req.getEmploymentType() != null) {

			error = validateObject(req.getEmploymentType(), "employmentType");

			if (error != null)
				return error;
		}

		if (req.getSkillsMustHave() != null) {

			error = validateObject(req.getSkillsMustHave(), "skillsMustHave");

			if (error != null)
				return error;
		}

		if (req.getNiceToHaveSkills() != null) {

			error = validateObject(req.getNiceToHaveSkills(), "niceToHaveSkills");

			if (error != null)
				return error;
		}

		if (req.getMinExperience() != null) {

			error = validateObject(req.getMinExperience(), "minExperience");

			if (error != null)
				return error;

			if (req.getMinExperience() < 0) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure",
						List.of("minExperience cannot be negative"));
			}
		}

		if (req.getMaxExperience() != null) {

			error = validateObject(req.getMaxExperience(), "maxExperience");

			if (error != null)
				return error;

			if (req.getMaxExperience() < 0) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure",
						List.of("maxExperience cannot be negative"));
			}
		}

		if (req.getMinExperience() != null && req.getMaxExperience() != null) {

			if (req.getMinExperience() > req.getMaxExperience()) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure",
						List.of("minExperience cannot be greater than maxExperience"));
			}
		}

		return null;
	}

	// validations for jobDescriptionRequest

	public ApiResponse<?> validateJobDescriptionRequest(JobDescriptionRequest req) {

		ApiResponse<?> error;

		if (req.getJobId() != null) {

			error = validateObject(req.getJobId(), "jobId");

			if (error != null)
				return error;

			if (!createJobDetailsRepository.existsById(req.getJobId())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Invalid jobId"));
			}
		}

		if (req.getDescription() != null) {

			error = validateObject(req.getDescription(), "description");

			if (error != null)
				return error;
		}

		return null;
	}

	// validations for object

	public ApiResponse<?> validateObject(Object value, String fieldName) {

		if (value == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(fieldName + " is required"));
		}

		if (value instanceof String str) {

			if (str.trim().isEmpty()) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(fieldName + " cannot be empty"));
			}
		}

		if (value instanceof Integer number) {

			if (number <= 0) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure",
						List.of(fieldName + " must be greater than 0"));
			}
		}

		return null;
	}

	// validations for soucingChannelRequest

	public ApiResponse<?> validateSourcingChannelRequest(SourcingChannelRequest req) {

		ApiResponse<?> error;

		if (req.getJobId() != null) {

			error = validateObject(req.getJobId(), "jobId");

			if (error != null)
				return error;

			if (!createJobDetailsRepository.existsById(req.getJobId())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Invalid jobId"));
			}
		}

		if (req.getChannelName() != null) {

			error = validateObject(req.getChannelName(), "channelName");

			if (error != null)
				return error;
		}

		if (req.getPostJob() == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("postJob is required"));
		}

		if (req.getReferralAmount() != null) {

			error = validateObject(req.getReferralAmount(), "referralAmount");

			if (error != null)
				return error;
		}
		return null;

	}

	// validations for RecruiterAssignmentRequest
	public ApiResponse<?> validateRecruiterAssignmentRequest(RecuriterAssignmentRequest req) {

		ApiResponse<?> error;

		if (req.getJobId() != null) {

			error = validateObject(req.getJobId(), "jobId");

			if (error != null)
				return error;

			if (!createJobDetailsRepository.existsById(req.getJobId())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Invalid jobId"));
			}
		}

		if (req.getUserIds() != null) {

			if (req.getUserIds().isEmpty()) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("userIds cannot be empty"));
			}

			for (Integer userId : req.getUserIds()) {

				if (userId == null || userId <= 0) {

					return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Invalid userId"));
				}

				if (!userRepository.existsById(Long.valueOf(userId))) {

					return ApiResponse.failure(ResponseCode.FAILURE, "Failure",
							List.of("User not found for userId : " + userId));
				}
			}
		}

		return null;
	}

	// validationds for review request

	public ApiResponse<?> validateJobCreationReviewRequest(JobCreationReviewRequest req) {

		ApiResponse<?> error;

		if (req.getJobId() != null) {

			error = validateObject(req.getJobId(), "jobId");

			if (error != null)
				return error;

			if (!createJobDetailsRepository.existsById(req.getJobId())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Invalid jobId"));
			}
		}

		if (req.getSubmit() == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("submit is required"));
		}

		return null;
	}

	@Override
	public ApiResponse<?> getRecruiters(SpecificationFilterRequest request) {

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

		List<Integer> departmentIds = request.getIntegerListFilter("departmentIds");

		List<Integer> roleIds = request.getIntegerListFilter("roleIds");

		String search = request.getFilter("search");

		Set<Integer> finalRoleIds = new LinkedHashSet<>();

		if (departmentIds != null && !departmentIds.isEmpty() && roleIds != null && !roleIds.isEmpty()) {

			List<Integer> mappedRoleIds = rolesRepository.findByDepartmentIdIn(departmentIds).stream()
					.filter(role -> roleIds.contains(role.getId())).map(RolesEntity::getId).toList();

			finalRoleIds.addAll(mappedRoleIds);

		} else if (departmentIds != null && !departmentIds.isEmpty()) {

			List<Integer> departmentRoleIds = rolesRepository.findByDepartmentIdIn(departmentIds).stream()
					.map(RolesEntity::getId).toList();

			finalRoleIds.addAll(departmentRoleIds);

		} else if (roleIds != null && !roleIds.isEmpty()) {

			finalRoleIds.addAll(roleIds);
		}

		Page<AssignRolesEntity> assignRolesPage = assignRolesRepository
				.findAll(request.buildRecruiterSpecification(new ArrayList<>(finalRoleIds)), pageable);

		if (assignRolesPage.isEmpty()) {

			return ApiResponse.success(ResponseCode.SUCCESS, "No recruiters found", Collections.emptyList());
		}

		List<AssignRolesEntity> assignRoles = assignRolesPage.getContent();

		List<Integer> userIds = assignRoles.stream().map(AssignRolesEntity::getUserId).distinct().toList();

		List<UserEntity> users = userRepository.findByIdIn(userIds);

		Map<Integer, UserEntity> userMap = users.stream().collect(Collectors.toMap(UserEntity::getId, user -> user));

		List<RolesEntity> roles = rolesRepository.findAllById(finalRoleIds);

		Map<Integer, RolesEntity> roleMap = roles.stream().collect(Collectors.toMap(RolesEntity::getId, role -> role));

		List<Object[]> counts = recruiterAssignmentRepository.findAssignmentCounts(userIds);

		Map<Integer, Long> countMap = counts.stream()
				.collect(Collectors.toMap(row -> (Integer) row[0], row -> (Long) row[1]));

		Map<Integer, Map<Integer, List<RecruiterDetailsResponse>>> groupedMap = new LinkedHashMap<>();

		for (AssignRolesEntity assign : assignRoles) {

			UserEntity user = userMap.get(assign.getUserId());

			if (user == null) {
				continue;
			}

			RolesEntity role = roleMap.get(assign.getRoleId());

			if (role == null) {
				continue;
			}

			if (search != null && !search.isBlank()) {

				boolean matches = (user.getUsername() != null
						&& user.getUsername().toLowerCase().contains(search.toLowerCase()))

						||

						(user.getEmail() != null && user.getEmail().toLowerCase().contains(search.toLowerCase()))

						||

						(role.getRoleName() != null && role.getRoleName().toLowerCase().contains(search.toLowerCase()));

				if (!matches) {
					continue;
				}
			}

			RecruiterDetailsResponse recruiter = new RecruiterDetailsResponse();

			recruiter.setUserId(user.getId());

			recruiter.setRecruiterName(user.getUsername());

			recruiter.setEmail(user.getEmail());

			recruiter.setRoleName(role.getRoleName());

			recruiter.setTotalAssignments(countMap.getOrDefault(user.getId(), 0L));

			groupedMap.computeIfAbsent(role.getDepartmentId(), k -> new LinkedHashMap<>())
					.computeIfAbsent(role.getId(), k -> new ArrayList<>()).add(recruiter);
		}

		List<Map<String, Object>> departments = new ArrayList<>();

		for (Map.Entry<Integer, Map<Integer, List<RecruiterDetailsResponse>>> departmentEntry : groupedMap.entrySet()) {

			Map<String, Object> departmentMap = new LinkedHashMap<>();

			departmentMap.put("departmentId", departmentEntry.getKey());

			List<Map<String, Object>> rolesList = new ArrayList<>();

			for (Map.Entry<Integer, List<RecruiterDetailsResponse>> roleEntry : departmentEntry.getValue().entrySet()) {

				RolesEntity role = roleMap.get(roleEntry.getKey());

				Map<String, Object> roleData = new LinkedHashMap<>();

				roleData.put("roleId", roleEntry.getKey());

				roleData.put("roleName", role != null ? role.getRoleName() : null);

				roleData.put("users", roleEntry.getValue());

				rolesList.add(roleData);
			}

			departmentMap.put("roles", rolesList);

			departments.add(departmentMap);
		}

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("totalElements", assignRolesPage.getTotalElements());

		response.put("totalPages", assignRolesPage.getTotalPages());

		response.put("currentPage", request.getPage());

		response.put("departments", departments);

		return ApiResponse.success(ResponseCode.SUCCESS, "Recruiters fetched successfully", response);
	}
}
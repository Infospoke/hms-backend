package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.service.dto.JobCreationDetailsResponseDto;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.BusinessUnitEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
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
import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.JobDescriptionRequest;
import com.hms.service.request.SourcingChannelRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.response.AssignedRecruiterResponse;
import com.hms.service.response.CreateJobDetailsResponse;
import com.hms.service.response.JobDescriptionDetailResponse;
import com.hms.service.response.JobDescriptionResponse;
import com.hms.service.response.JobOverviewResponse;
import com.hms.service.response.MyRecruiterResponse;
import com.hms.service.response.RecruiterDetailsResponse;
import com.hms.service.response.RecruitersResponse;
import com.hms.service.response.SourcingChannelResponse;
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

	@Autowired
	private JwtService jwtService;

	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private HttpServletRequest httpServletRequest;

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

	@Autowired
	private RecruiterServiceImpl recruiterServiceImpl;

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
		DepartmentsEntity department = departmentsRepository.findById(srData.getDepartmentId()).orElse(null);
		BusinessUnitEntity businessUnit = businessUnitRepository.findById(srData.getBusinessUnitId()).orElse(null);

		CreateJobDetailsResponse response = new CreateJobDetailsResponse();

		response.setSrId(srData.getSrId());
		response.setJobCode(generateJobCode(srData.getSrId()));
		response.setJobTitle(srData.getJobTitle());
		response.setBusinessUnitId(srData.getBusinessUnitId());
		if (businessUnit != null) {
			response.setBusinessName(businessUnit.getBusinessName());
		}
		response.setDepartmentId(srData.getDepartmentId());
		if (department != null) {
			response.setDepartmentName(department.getDepartmentName());
		}
		response.setLocation(srData.getLocation());
		response.setWorkMode(srData.getWorkMode());
		response.setEmploymentType(srData.getEmploymentType());
		response.setOpenings(srData.getOpenings());
		response.setTargetStartDate(srData.getTargetStartDate());
		response.setCountry(srData.getCountry());

		if (rolesData != null) {

			response.setSkillsMustHave(rolesData.getSkillsMustHave());
			response.setNiceToHaveSkills(rolesData.getNiceToHaveSkills());
			response.setMinExperience(rolesData.getMinExperience());
			response.setMaxExperience(rolesData.getMaxExperience());
			response.setEducationRequirement(rolesData.getEducationRequirement());
			response.setCertificationsRequired(rolesData.getCertificationsRequired());
			response.setLanguages(rolesData.getLanguages());
		}

		return ApiResponse.success(ResponseCode.SUCCESS, "Job Details fetched successfully", response);
	}

	@Override
	@Transactional
	public ApiResponse<?> createJob(CreateJobRequest request) {

		// createJob details

		log.info("CreateJobServiceImpl :: Inside the createJob method");
		if (request == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Request body cannot be null"));
		}

		if (request.getSrId() != null) {

			Optional<SRPositionBasicsEntity> srOptional = positionBasicsRepository.findBySrId(request.getSrId());
			if (srOptional.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", "Invalid SRID");

			}

			String authHeader = httpServletRequest.getHeader("Authorization");

			String userName = "";
			Long roleId = null;

			if (authHeader != null && authHeader.startsWith("Bearer ")) {

				String token = authHeader.substring(7);

				userName = jwtService.extractUsernameFromClaims(token);
				roleId = jwtService.extractRoleId(token);
			}
			CreateJobDetailsEntity createJobDetailsEntity = new CreateJobDetailsEntity();
			JobDescriptionEntity descriptionEntity = new JobDescriptionEntity();
			SourcingChannelEntity channelEntity = new SourcingChannelEntity();

			if (request.getCreateJobDetailsRequest() != null) {

				CreateJobDetailsRequest req = request.getCreateJobDetailsRequest();

				ApiResponse<?> error = validateCreateJobDetailsRequest(req, request.getSrId());

				if (error != null) {
					return error;
				}

				createJobDetailsEntity.setSrId(request.getSrId());
				createJobDetailsEntity.setCreatedBy(userName);
				createJobDetailsEntity.setRoleId(roleId);

				createJobDetailsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				createJobDetailsEntity.setJobTitle(req.getJobTitle());

				createJobDetailsEntity.setBusinessUnitId(req.getBusinessUnitId());

				createJobDetailsEntity.setDepartmentId(req.getDepartmentId());

				createJobDetailsEntity.setLocation(req.getLocation());

				createJobDetailsEntity.setJobCode(req.getJobCode());

				createJobDetailsEntity.setOpenings(req.getOpenings());

				createJobDetailsEntity.setTargetStartDate(req.getTargetStartDate());

				createJobDetailsEntity.setWorkMode(req.getWorkMode());

				createJobDetailsEntity.setEmploymentType(req.getEmploymentType());

				createJobDetailsEntity.setSkillsMustHave(req.getSkillsMustHave());

				createJobDetailsEntity.setNiceToHaveSkills(req.getNiceToHaveSkills());

				createJobDetailsEntity.setMinExperience(req.getMinExperience());

				createJobDetailsEntity.setMaxExperience(req.getMaxExperience());

				createJobDetailsEntity.setAdditionalNotes(req.getAdditionalNotes());

				createJobDetailsEntity.setSubmit(request.getSubmit());

				createJobDetailsEntity.setEducationRequirement(req.getEducationRequirement());

				createJobDetailsEntity.setCountry(req.getCountry());

				createJobDetailsEntity.setIsOpen(true);

				createJobDetailsEntity.setCertificationsRequired(req.getCertificationsRequired());

				createJobDetailsEntity.setLanguages(req.getLanguages());

			}

			// job description

			if (request.getJobDescriptionRequest() != null) {

				JobDescriptionRequest req = request.getJobDescriptionRequest();

				ApiResponse<?> error = validateJobDescriptionRequest(req, request.getSrId());

				if (error != null) {
					return error;
				}

				descriptionEntity.setSrId(request.getSrId());

				descriptionEntity.setDescription(req.getDescription());

			}

			// Souring channel
			if (request.getSourcingChannelRequest() != null) {

				ApiResponse<?> error = validateSourcingChannelRequest(request.getSourcingChannelRequest(),
						request.getSrId());

				if (error != null) {
					return error;
				}

				Map<String, Boolean> map = new LinkedHashMap<>();
				map.putAll(request.getSourcingChannelRequest().getChannels());

				channelEntity.setSourcingChannelRequest(map);

				if (request.getSourcingChannelRequest().getReferral()) {
					error = validateObject(request.getSourcingChannelRequest().getReferralAmount(), "referralAmount");

					if (error != null)
						return error;
				}
				channelEntity.setReferral(true);
				channelEntity.setReferralAmount(request.getSourcingChannelRequest().getReferralAmount());
				channelEntity.setSrId(request.getSrId());

			}

			// recuriter assignment

			request.getRecuriterAssignmentRequest().setJobId(createJobDetailsEntity.getJobId());
			ApiResponse<?> recruitersList = recruiterServiceImpl
					.assignRecruiter(request.getRecuriterAssignmentRequest());

			if (recruitersList.getResponsecode().equals("01"))
				return recruitersList;

			// Interview plan
			if (request.getInterviewPlanRequest() != null) {

				InterviewPlanRequest req = request.getInterviewPlanRequest();

				ApiResponse<?> error = validateInterviewPlanRequest(req, request.getSrId());

				if (error != null) {
					return error;
				}

				createJobDetailsEntity.setPlanId(req.getPlanId());
				createJobDetailsRepository.save(createJobDetailsEntity);

				List<RecruiterAssignmentEntity> list = (List<RecruiterAssignmentEntity>) recruitersList.getData();
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setJobId(createJobDetailsEntity.getJobId());
				}
				recruiterAssignmentRepository.saveAll(list);

				SRPositionBasicsEntity basicsEntity = srOptional.get();
				basicsEntity.setJobSubmit(request.getSubmit());
				positionBasicsRepository.save(basicsEntity);

				jobDescriptionRepository.save(descriptionEntity);
				descriptionEntity.setJobId(createJobDetailsEntity.getJobId());

				sourcingChannelRepository.save(channelEntity);
				channelEntity.setJobId(createJobDetailsEntity.getJobId());

			}
		}
		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Job Created Successfully");

	}

	// Validations for createJobDetailsRequest

	public ApiResponse<?> validateCreateJobDetailsRequest(CreateJobDetailsRequest req, String srId) {

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

		if (req.getEducationRequirement() != null) {

			error = validateObject(req.getEducationRequirement(), "educationRequirement");

			if (error != null)
				return error;
		}
		if (req.getCountry() != null) {

			error = validateObject(req.getCountry(), "country");

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
		if (req.getCertificationsRequired() != null) {

			error = validateObject(req.getCertificationsRequired(), "certificationsRequired");

			if (error != null)
				return error;
		}
		if (req.getLanguages() != null) {

			error = validateObject(req.getLanguages(), "languages");

			if (error != null)
				return error;
		}

		return null;
	}

	// validations for jobDescriptionRequest

	public ApiResponse<?> validateJobDescriptionRequest(JobDescriptionRequest req, String srId) {

		ApiResponse<?> error;

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
		return null;
	}

	// validations for soucingChannelRequest
	public ApiResponse<?> validateSourcingChannelRequest(SourcingChannelRequest req, String srId) {

		if (req.getChannels().size() < 0) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("channels cannot be empty"));
		}

		return null;
	}

	// validations for interviewPlanRequest
	private ApiResponse<?> validateInterviewPlanRequest(InterviewPlanRequest req, String srId) {

		ApiResponse<?> error;

		if (req.getPlanId() != null) {

			error = validateObject(req.getPlanId(), "planId");

			if (error != null)
				return error;
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

//		List<RolesEntity> rolesFromDb =
//		        (departmentIds != null && !departmentIds.isEmpty())
//		                ? rolesRepository.findByDepartmentIdIn(departmentIds)
//		                : rolesRepository.findAll();
//
//		List<Integer> finalRoleIds = rolesFromDb.stream()
//		        .map(RolesEntity::getId)
//		        .filter(id -> roleIds == null || roleIds.isEmpty() || roleIds.contains(id))
//		        .toList();
//
//		Page<AssignRolesEntity> assignRolesPage = assignRolesRepository
//				.findAll(request.buildRecruiterSpecification(new ArrayList<>(finalRoleIds)), pageable);

		if (finalRoleIds == null || finalRoleIds.isEmpty()) {
			return ApiResponse.success(ResponseCode.SUCCESS, "No recruiters found", Collections.emptyMap());
		}
		Page<AssignRolesEntity> assignRolesPage = assignRolesRepository
				.findAll(request.buildRecruiterSpecification(new ArrayList<>(finalRoleIds)), pageable);

		if (assignRolesPage.isEmpty()) {

			return ApiResponse.success(ResponseCode.SUCCESS, "No recruiters found", Collections.emptyList());
		}

		List<AssignRolesEntity> assignRoles = assignRolesPage.getContent();

		List<Integer> userIds = assignRoles.stream().map(AssignRolesEntity::getUserId).distinct().toList();

		List<UserEntity> users = userRepository.findByUserIdIn(userIds);

		Map<Integer, UserEntity> userMap = users.stream()
				.collect(Collectors.toMap(UserEntity::getUserId, user -> user));

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

			recruiter.setUserId(user.getUserId());

			recruiter.setRecruiterName(user.getUsername());

			recruiter.setEmail(user.getEmail());

			recruiter.setRoleName(role.getRoleName());

			recruiter.setTotalAssignments(countMap.getOrDefault(user.getUserId(), 0L));

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

	@Override
	public ApiResponse<?> getJobCreationDetails(Integer jobId) {

		log.info("CreateJobServiceImpl : Inside getJobCreationDetails method");

		String authHeader = httpServletRequest.getHeader("Authorization");

		Long userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token);

		} else {

			return ApiResponse.failure(ResponseCode.FAILURE, "Unauthorized", List.of("Missing or invalid token"));
		}

		try {

			if (jobId == null) {

				return ApiResponse.failure(ResponseCode.FAILURE, List.of("Job ID cannot be null"));
			}

			Optional<CreateJobDetailsEntity> jobEntity = createJobDetailsRepository.findById(jobId);

			JobDescriptionEntity descriptionEntity = jobDescriptionRepository.findByJobId(jobId);

			SourcingChannelEntity sourcingEntity = sourcingChannelRepository.findByJobId(jobId);

			List<RecruiterAssignmentEntity> recruiterEntities = recruiterAssignmentRepository.findByJobId(jobId);

			CreateJobDetailsEntity createJobDetailsEntity = jobEntity.get();

			if (createJobDetailsEntity == null && descriptionEntity == null && sourcingEntity == null
					&& recruiterEntities.isEmpty()) {

				return ApiResponse.failure(ResponseCode.FAILURE, List.of("Invalid Job ID : " + jobId));
			}

			JobCreationDetailsResponseDto response = new JobCreationDetailsResponseDto();

			// JOB OVERVIEW

			if (jobEntity != null) {

				JobOverviewResponse jobOverviewResponse = new JobOverviewResponse();
				String businessUnit = businessUnitRepository.findById(createJobDetailsEntity.getBusinessUnitId()).get()
						.getBusinessName();
				String department = departmentsRepository.findById(createJobDetailsEntity.getDepartmentId()).get()
						.getDepartmentName();

				jobOverviewResponse.setJobTitle(createJobDetailsEntity.getJobTitle());
				jobOverviewResponse.setJobCode(createJobDetailsEntity.getJobCode());
				jobOverviewResponse.setBusinessUnit(businessUnit);
				jobOverviewResponse.setDepartment(department);
				jobOverviewResponse.setLocation(createJobDetailsEntity.getLocation());
				jobOverviewResponse.setOpenings(createJobDetailsEntity.getOpenings());
				jobOverviewResponse.setTargetStartDate(createJobDetailsEntity.getTargetStartDate());
				jobOverviewResponse.setWorkMode(createJobDetailsEntity.getWorkMode());
				jobOverviewResponse.setEmploymentType(createJobDetailsEntity.getEmploymentType());
				jobOverviewResponse.setMinExperience(createJobDetailsEntity.getMinExperience());
				jobOverviewResponse.setMaxExperience(createJobDetailsEntity.getMaxExperience());

				jobOverviewResponse.setSkillsMustHave(createJobDetailsEntity.getSkillsMustHave() != null
						? Arrays.asList(createJobDetailsEntity.getSkillsMustHave().split(","))
						: Collections.emptyList());

				jobOverviewResponse.setNiceToHaveSkills(createJobDetailsEntity.getNiceToHaveSkills() != null
						? Arrays.asList(createJobDetailsEntity.getNiceToHaveSkills().split(","))
						: Collections.emptyList());

				jobOverviewResponse.setAdditionalNotes(createJobDetailsEntity.getAdditionalNotes());

				response.setJobOverview(jobOverviewResponse);
			}

			// JOB DESCRIPTION
			if (descriptionEntity != null) {

				JobDescriptionResponse jobDescriptionResponse = new JobDescriptionResponse();

				List<JobDescriptionDetailResponse> details = descriptionEntity.getDescription().stream().map(desc -> {
					JobDescriptionDetailResponse detail = new JobDescriptionDetailResponse();
					BeanUtils.copyProperties(desc, detail);
					return detail;
				}).toList();

				jobDescriptionResponse.setDescription(details);

				response.setJobDescription(jobDescriptionResponse);
				// SOURCING STRATEGY

				if (sourcingEntity != null) {

					SourcingChannelResponse sourcingChannelResponse = new SourcingChannelResponse();

					sourcingChannelResponse.setSourcingChannels(sourcingEntity.getSourcingChannelRequest());

					sourcingChannelResponse.setReferral(sourcingEntity.getReferral());

					sourcingChannelResponse.setReferralAmount(sourcingEntity.getReferralAmount());

					response.setSourcingStrategy(sourcingChannelResponse);
				}

				// RECRUITERS

				if (!recruiterEntities.isEmpty()) {

					List<AssignedRecruiterResponse> recruiters = recruiterEntities.stream().map(entity -> {

						AssignedRecruiterResponse recruiter = new AssignedRecruiterResponse();

						recruiter.setUserName(entity.getUserName());

						recruiter.setEmail(entity.getEmail());

						recruiter.setAssignedAt(entity.getAssignedAt());

						return recruiter;

					}).toList();

					RecruitersResponse recruitersResponse = new RecruitersResponse();

					recruitersResponse.setRecruiters(recruiters);

					RecruiterAssignmentEntity loggedInRecruiter = recruiterAssignmentRepository
							.findByJobIdAndUserId(jobId, userId.intValue());

					if (loggedInRecruiter != null) {

						RecruiterAssignmentEntity entity = loggedInRecruiter;

						MyRecruiterResponse myResponse = new MyRecruiterResponse();

						myResponse.setComments(entity.getComments());

						myResponse.setStatus(entity.getStatus());

						recruitersResponse.setMyResponse(List.of(myResponse));
					}
					response.setRecruiters(recruitersResponse);
				}

				return ApiResponse.success(ResponseCode.SUCCESS, "Success", response);
			}
		}

		catch (Exception e) {

			log.error("Error fetching Job Details for jobId: {}", jobId, e);

			return ApiResponse.failure(ResponseCode.FAILURE, List.of(e.getMessage()));
		}
		return null;
	}
}

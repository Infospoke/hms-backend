package com.hms.service.serviceImpl;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.service.constants.Constants;
import com.hms.service.dto.AgencyDetailsResponseDto;
import com.hms.service.dto.CategoryResponseDto;
import com.hms.service.dto.JobCreationDetailsResponseDto;
import com.hms.service.dto.NotificationEvent;
import com.hms.service.entity.ActivityFeedEntity;
import com.hms.service.entity.AgencyDetailsEntity;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.BusinessUnitEntity;
import com.hms.service.entity.CategoryEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.JobDescriptionEntity;
import com.hms.service.entity.OfferDetailsEntity;
import com.hms.service.entity.RecruiterAssignmentEntity;
import com.hms.service.entity.RolesAndRequirementsEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.entity.SourcingChannelEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.ActivityFeedRepository;
import com.hms.service.repository.AgencyDetailsRepository;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.BusinessUnitRepository;
import com.hms.service.repository.CategoryRepostiory;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.InterviewAnalysisRepository;
import com.hms.service.repository.InterviewSessionRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.JobDescriptionRepository;
import com.hms.service.repository.OfferDetailsRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RecruiterAssignmentRepository;
import com.hms.service.repository.ResumeAnalysisRepository;
import com.hms.service.repository.RolesAndRequirementsRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.SourcingChannelRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.AgencyDetailsRequest;
import com.hms.service.request.CreateJobDetailsRequest;
import com.hms.service.request.CreateJobRequest;
import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.JobDescriptionRequest;
import com.hms.service.request.SourcingChannelRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateJobDetailsRequest;
import com.hms.service.response.ApplicantsCountResponse;
import com.hms.service.response.AssignedRecruiterResponse;
import com.hms.service.response.CreateJobDetailsResponse;
import com.hms.service.response.JobDescriptionDetailResponse;
import com.hms.service.response.JobDescriptionResponse;
import com.hms.service.response.JobDetailsResponse;
import com.hms.service.response.JobOverviewResponse;
import com.hms.service.response.MyRecruiterResponse;
import com.hms.service.response.RecruiterDetailsResponse;
import com.hms.service.response.RecruitersResponse;
import com.hms.service.response.SourcingChannelResponse;
import com.hms.service.service.ICreateJobService;
import com.hms.service.service.INotificationService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	private CategoryRepostiory categoryRepostiory;

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
	private InterviewSessionRepository interviewSessionRepository;

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

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private AgencyDetailsRepository agencyDetailsRepository;

	@Autowired
	private ResumeAnalysisRepository resumeAnalysisRepository;
	
	@Autowired
	private MinioClient minioClient;

	@Autowired
	private ActivityFeedRepository activityFeedRepository;

	@Autowired
	private INotificationService notificationService;
	
	@Autowired
	private OfferDetailsRepository  offerDetailsRepository;
	
	@Autowired
	private InterviewAnalysisRepository interviewAnalysisRepository;

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
			ActivityFeedEntity activityFeedEntity = new ActivityFeedEntity();

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

			// Recruiter assignment

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

				// Agency Details

				if (request.getAgencyDetailsRequest() != null) {

					AgencyDetailsRequest agencyReq = request.getAgencyDetailsRequest();

					ApiResponse<?> agencyError = validateAgencyDetailsRequest(agencyReq);

					if (agencyError != null) {
						return agencyError;
					}

					String agencyIds = agencyReq.getAgencyIds().stream().map(String::valueOf)
							.collect(Collectors.joining(","));

					createJobDetailsEntity.setAgencyIds(agencyIds);
				}
				createJobDetailsRepository.save(createJobDetailsEntity);

				List<RecruiterAssignmentEntity> list = (List<RecruiterAssignmentEntity>) recruitersList.getData();
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setJobId(createJobDetailsEntity.getJobId());
				}
				recruiterAssignmentRepository.saveAll(list);

				SRPositionBasicsEntity basicsEntity = srOptional.get();
				basicsEntity.setJobSubmit(request.getSubmit());
				positionBasicsRepository.save(basicsEntity);

				descriptionEntity.setJobId(createJobDetailsEntity.getJobId());
				jobDescriptionRepository.save(descriptionEntity);

				channelEntity.setJobId(createJobDetailsEntity.getJobId());
				sourcingChannelRepository.save(channelEntity);

				String jobTitle = createJobDetailsEntity.getJobTitle();
				log.info("hello");
				activityFeedEntity.setTimeStamp(LocalDateTime.now());

				activityFeedEntity.setActivity(jobTitle + " job was posted successfully");
				activityFeedRepository.save(activityFeedEntity);

				NotificationEvent makerEvent = new NotificationEvent();

				List<AssignRolesEntity> makerAssignRoles = assignRolesRepository
						.findByRoleId(createJobDetailsEntity.getRoleId().intValue());

				if (makerAssignRoles != null && !makerAssignRoles.isEmpty()) {

					AssignRolesEntity makerAssignRole = makerAssignRoles.get(0);

					String departmentName = departmentsRepository.findById(createJobDetailsEntity.getDepartmentId())
							.get().getDepartmentName();

					String makerRoleName = rolesRepository.findByRoleId(makerAssignRole.getRoleId()).get()
							.getRoleName();

					makerEvent.setProcessId(createJobDetailsEntity.getJobId().toString());

					makerEvent.setType("JOB CREATION");

					makerEvent.setDeptName(departmentName);

					makerEvent.setMakerRoleId(makerAssignRole.getRoleId());

					makerEvent.setMakerRoleName(makerRoleName);

					makerEvent.setMakerNotificationTitle("New Job Created");

					makerEvent.setMakerMessage(
							createJobDetailsEntity.getJobTitle() + " job has been created successfully.");
				}

				List<NotificationEvent> checkerEvents = new ArrayList<>();

				List<RecruiterAssignmentEntity> recruiterAssignments = recruiterAssignmentRepository
						.findByJobIdAndSrId(createJobDetailsEntity.getJobId(), createJobDetailsEntity.getSrId());

				if (recruiterAssignments != null && !recruiterAssignments.isEmpty()) {

					String departmentName = departmentsRepository.findById(createJobDetailsEntity.getDepartmentId())
							.get().getDepartmentName();

					for (RecruiterAssignmentEntity recruiter : recruiterAssignments) {

						NotificationEvent checkerEvent = new NotificationEvent();

						checkerEvent.setProcessId(createJobDetailsEntity.getJobId().toString());

						checkerEvent.setType("JOB ASSIGNMENT");

						checkerEvent.setDeptName(departmentName);

						checkerEvent.setCheckerRoleName(recruiter.getRoleName());

						checkerEvent.setCheckerNotificationTitle("New Job Assignment");

						checkerEvent.setCheckerMessage("A new job assignment has been allocated to you for "
								+ createJobDetailsEntity.getJobTitle());

						Map<Integer, List<String>> roleEmailMap = new HashMap<>();

						roleEmailMap.put(recruiter.getRoleId(), List.of(recruiter.getEmail()));

						checkerEvent.setRoleEmailMap(roleEmailMap);

						checkerEvents.add(checkerEvent);
					}
				}

				notificationService.callInterviewerAssignmentNotification(makerEvent, checkerEvents);

			}
		}
		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Job Created Successfully");

	}

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

	// Agency details validation
	private ApiResponse<?> validateAgencyDetailsRequest(AgencyDetailsRequest req) {

		ApiResponse<?> error;

		if (req.getAgencyIds() == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("agencyIds is required"));
		}

		if (req.getAgencyIds().isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("agencyIds cannot be empty"));
		}

		Set<Integer> uniqueAgencyIds = new HashSet<>(req.getAgencyIds());

		if (uniqueAgencyIds.size() != req.getAgencyIds().size()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Duplicate agencyIds are not allowed"));
		}

		// Validate every agencyId
		for (Integer agencyId : req.getAgencyIds()) {

			error = validateObject(agencyId, "agencyId");

			if (error != null) {
				return error;
			}

			if (!agencyDetailsRepository.existsById(agencyId)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Invalid agencyId : " + agencyId));
			}
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
		
		String candidateId=null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token);
			
			candidateId=jwtService.extractCandidateId(token);

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
				jobOverviewResponse.setCountry(createJobDetailsEntity.getCountry());

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
				
				if(userId!=null)
				{
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
				}
				long totalApplicants = jobApplicationRepository.countByJobId(jobId);
				long resumeCompleted = resumeAnalysisRepository.countByJobId(jobId);

				long shortlistedCount = resumeAnalysisRepository.countByJobIdAndStatusIgnoreCase(jobId,
						Constants.SHORTLISTED);
				
				long interviewCount = interviewAnalysisRepository.countByJobId(jobId);
				
				long acceptedCount=0L;
				
				List<JobApplicationEntity> applicantList =
				        jobApplicationRepository.findByJobId(jobId);

				
				if (!applicantList.isEmpty()) {

				    List<Integer> applicationIds = applicantList.stream()
				            .map(JobApplicationEntity::getId)
				            .toList();

				    List<OfferDetailsEntity> offers =
				            offerDetailsRepository.findByJobApplication_IdIn(applicationIds);

				    acceptedCount = offers.stream()
				            .filter(o -> "Accepted".equalsIgnoreCase(o.getOfferStatus()))
				            .count();
				}

				ApplicantsCountResponse applicants = new ApplicantsCountResponse();

				applicants.setApplicantCount(totalApplicants);
				applicants.setShortlisted(shortlistedCount);
				applicants.setResumeCount(resumeCompleted);
				applicants.setHiredCount(0L);
				applicants.setOfferAccepted(acceptedCount);
				applicants.setInterviewCount(interviewCount);

				response.setApplicantsCount(applicants);

				return ApiResponse.success(ResponseCode.SUCCESS, "Success", response);
			}

		}

		catch (Exception e) {

			log.error("Error fetching Job Details for jobId: {}", jobId, e);

			return ApiResponse.failure(ResponseCode.FAILURE, List.of(e.getMessage()));
		}
		return null;
	}

	@Override
	public void downloadFile(Integer appId, String type, String action, HttpServletResponse response) {
		log.info("JobsServiceImpl:Inside downloadFile method");

		JobApplicationEntity app = jobApplicationRepository.findById(appId)
				.orElseThrow(() -> new RuntimeException(Constants.APPLICATION_NOT_FOUND));

		String objectKey;

		if (Constants.RESUME.equalsIgnoreCase(type)) {
			objectKey = app.getResume();
		} else if (Constants.ADDITIONAL.equalsIgnoreCase(type)) {
			objectKey = app.getAdditionalFile();
		} else {
			throw new RuntimeException(Constants.INVALID_FILE_TYPE);
		}

		if (objectKey == null) {
			throw new RuntimeException(Constants.FILE_NOT_UPLOADED);
		}

		String fileName = Paths.get(objectKey).getFileName().toString();

		try {

			InputStream minioStream = minioClient
					.getObject(GetObjectArgs.builder().bucket(Constants.BUCKETNAME).object(objectKey).build());

			String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

			response.setContentType("application/octet-stream");
			response.setCharacterEncoding("UTF-8");

			response.setHeader("Content-Disposition",
					(Constants.VIEW.equalsIgnoreCase(action) ? "inline" : "attachment") + "; filename*=UTF-8''"
							+ encodedFileName);

			IOUtils.copy(minioStream, response.getOutputStream());
			response.flushBuffer();

			minioStream.close();

		} catch (Exception e) {
			log.info("JobsServiceImpl::exception occured in downloadFile method" + e.getMessage());
			throw new RuntimeException("Error downloading file from MinIO", e);
		}
	}

	@Override
	public ApiResponse<?> updateJobDetailsById(UpdateJobDetailsRequest request) {
		log.info("CreateJobServiceImpl : Inside updateJobDetailsById method");
		log.info("the jobId from the request is" + request.getJobId());
		CreateJobDetailsEntity createJobDetailsEntity = createJobDetailsRepository.findByJobId(request.getJobId());
		if (createJobDetailsEntity == null) {
			log.info("No job found wiith the " + request.getJobId() + "jobId");
			return ApiResponse.failure(ResponseCode.FAILURE, "job not found with " + request.getJobId());
		}
		createJobDetailsEntity.setIsOpen(request.getIsOpen());
		createJobDetailsRepository.save(createJobDetailsEntity);
		log.info("CreateJobServiceImpl : Exit from the updateJobDetailsById method");
		return ApiResponse.success(ResponseCode.SUCCESS, "success", "job details updated sucessfully");
	}

	@Override
	public ApiResponse<?> getAllJobs(SpecificationFilterRequest request) {

		log.info("CreateJobServiceImpl :: Inside the getAllJobs");

		List<CreateJobDetailsEntity> jobs = createJobDetailsRepository.findAll(request.buildJobsSpecification(),
				Sort.by(Sort.Direction.DESC, "createdAt"));

		Map<Integer, Long> applicationCountMap = jobApplicationRepository.getApplicationCountByJobId().stream()
				.collect(Collectors.toMap(row -> (Integer) row[0], row -> (Long) row[1]));

		Map<Integer, Long> completedInterviewMap = interviewSessionRepository.getCompletedInterviewCountByJobId()
				.stream().collect(Collectors.toMap(row -> (Integer) row[0], row -> (Long) row[1]));

		List<JobDetailsResponse> response = jobs.stream().map(job -> new JobDetailsResponse(

				job.getJobId(), job.getJobCode(), job.getJobTitle(), job.getMinExperience(), job.getMaxExperience(),
				job.getLocation(),

				job.getSkillsMustHave() != null
						? Arrays.stream(job.getSkillsMustHave().split(",")).map(String::trim).toList()
						: Collections.emptyList(),

				job.getWorkMode(),

				applicationCountMap.getOrDefault(job.getJobId(), 0L),

				completedInterviewMap.getOrDefault(job.getJobId(), 0L)

		)).toList();

		log.info("CreateJobServiceImpl :: Exit from the getAllJobs");

		return ApiResponse.success(ResponseCode.SUCCESS, "Success", response);
	}

	@Override
	public ApiResponse<?> getAgencyList(SpecificationFilterRequest request) {

		try {

			List<Integer> categoryIds = new ArrayList<>();

			String categoryFilter = request.getFilter("categoryIds");

			if (categoryFilter != null && !categoryFilter.isBlank()) {

				categoryIds.addAll(Arrays.stream(categoryFilter.split(",")).map(String::trim).filter(s -> !s.isBlank())
						.map(Integer::parseInt).toList());
			}

			String search = request.getFilter("search");

			if (search != null && !search.isBlank()) {

				List<CategoryEntity> matchedCategories = categoryRepostiory
						.findByCategoryNameContainingIgnoreCase(search);

				for (CategoryEntity category : matchedCategories) {

					if (!categoryIds.contains(category.getId())) {
						categoryIds.add(category.getId());
					}

				}

			}

			Pageable pageable = PageRequest.of(

					request.getPage(),

					request.getSize(),

					Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

			Specification<AgencyDetailsEntity> specification = request.buildAgencySpec(categoryIds);

			Page<AgencyDetailsEntity> page = agencyDetailsRepository.findAll(specification, pageable);

			Map<Integer, String> categoryMap = categoryRepostiory.findAll().stream()
					.collect(Collectors.toMap(CategoryEntity::getId, CategoryEntity::getCategoryName));

			List<AgencyDetailsResponseDto> response = new ArrayList<>();

			for (AgencyDetailsEntity agency : page.getContent()) {

				AgencyDetailsResponseDto dto = new AgencyDetailsResponseDto();

				dto.setId(agency.getId());

				dto.setAgencyName(agency.getAgencyName());

				dto.setEmailId(agency.getEmailId());

				List<CategoryResponseDto> categoryResponses = new ArrayList<>();

				if (agency.getCategoryIds() != null && !agency.getCategoryIds().isBlank()) {

					String[] ids = agency.getCategoryIds().split(",");

					for (String id : ids) {

						Integer categoryId = Integer.parseInt(id.trim());

						if (categoryMap.containsKey(categoryId)) {

							categoryResponses.add(

									new CategoryResponseDto(

											categoryId,

											categoryMap.get(categoryId)

									)

							);

						}

					}

				}

				dto.setCategories(categoryResponses);

				response.add(dto);

			}

			Map<String, Object> result = new HashMap<>();

			result.put("content", response);

			result.put("size", page.getSize());

			result.put("totalElements", page.getTotalElements());

			result.put("totalPages", page.getTotalPages());

			return ApiResponse.success(ResponseCode.SUCCESS, "Success", result);

		} catch (Exception e) {

			log.error("Error while fetching agency list", e);

			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Unable to fetch agency list"));

		}

	}
}

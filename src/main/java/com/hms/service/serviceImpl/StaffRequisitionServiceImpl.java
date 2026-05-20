package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.dto.NotificationEvent;
import com.hms.service.dto.StaffingRequisitionResponseDto;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.ApprovalsChildEntity;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.BudgetAndCompensationEntity;
import com.hms.service.entity.BusinessJustificationEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.FunctionalityEntity;
import com.hms.service.entity.RolesAndRequirementsEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.entity.SourcingStrategyEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.enums.FunctionalityTypes;
import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.ApprovalsChildRepository;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.BudgetAndCompensationRepository;
import com.hms.service.repository.BusinessJustificationRepository;
import com.hms.service.repository.BusinessUnitRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RolesAndRequirementsRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.SeniorityLevelRepository;
import com.hms.service.repository.SourceStrategyRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.BudgetAndCompensationRequest;
import com.hms.service.request.BusinessJustificationRequest;
import com.hms.service.request.LevelConfig;
import com.hms.service.request.PositonBascicsRequest;
import com.hms.service.request.ReviewRequest;
import com.hms.service.request.RolesAndRequirementsRequest;
import com.hms.service.request.SourcingStrategyRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.StaffingRequisitionRequest;
import com.hms.service.request.UpdateSrRequest;
import com.hms.service.response.ApprovedSrResponse;
import com.hms.service.response.BudgetAndCompensationResponse;
import com.hms.service.response.BusinessJustificationResponse;
import com.hms.service.response.BusinessValidationResponse;
import com.hms.service.response.PositonBasicsResponse;
import com.hms.service.response.RolesAndRequirementsResponse;
import com.hms.service.response.SRCountResponse;
import com.hms.service.response.SourcingStrategyResponse;
import com.hms.service.response.SrApprovalResponse;
import com.hms.service.service.INotificationService;
import com.hms.service.service.IStaffingRequisitionService;
import com.hms.service.utils.JwtService;
import com.hms.service.utils.SequenceGenerator;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StaffRequisitionServiceImpl implements IStaffingRequisitionService {

	@Autowired
	private MinioClient minioClient;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private PositionBasicsRepository positionBasicsRepository;

	@Autowired
	private BusinessJustificationRepository businessJustificationRepository;

	@Autowired
	private BudgetAndCompensationRepository budgetAndCompensationRepository;

	@Autowired
	private RolesAndRequirementsRepository rolesAndRequirementsRepository;

	@Autowired
	private SourceStrategyRepository sourceStrategyRepository;

	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private BusinessUnitRepository businessUnitRepository;

	@Autowired
	private SequenceGenerator sequenceGenerator;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ApprovalChainRepository approvalChainRepository;

	@Autowired
	private ApprovalsChildRepository approvalsChildRepository;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private INotificationService notificationService;

//	@Autowired
//	private UserServiceImpl userService;

	@Autowired
	private SeniorityLevelRepository seniorityLevelRepository;

	@Autowired
	private AssignRolesRepository assignRolesRepository;

	@Override
	public ApiResponse<?> newStaffingRequisition(StaffingRequisitionRequest request, MultipartFile file) {

		String srId = null;
		Long userId = null;
		Integer roleId=null;		

		ApiResponse<?> finalResponse = null;
		if (request.getPositonBascicsRequest() != null) {

			PositonBascicsRequest positonBasicsRequest = request.getPositonBascicsRequest();
			ApiResponse<?> error = validatePositonBasicsRequest(positonBasicsRequest);
			if (error != null)
				return error;

			SRPositionBasicsEntity srPositionBasicsEntity = null;
			if (positonBasicsRequest.getSrId() != null) {
				srPositionBasicsEntity = positionBasicsRepository.findBySrId(positonBasicsRequest.getSrId())
						.orElse(null);
			}

			if (srPositionBasicsEntity == null) {
				srPositionBasicsEntity = new SRPositionBasicsEntity();
				srPositionBasicsEntity.setSubmitted(false);
				srPositionBasicsEntity.setApproved(false);
				srPositionBasicsEntity.setCreatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
				srId = generateSrId(positonBasicsRequest.getDepartmentId());
				srPositionBasicsEntity.setSrId(srId);

				String username = getUsernameFromToken();

				userId = getUserIdFromToken();

				String roleName = getRoleNameFromToken();
				
				 roleId=rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				srPositionBasicsEntity.setCreatedBy(username);
				srPositionBasicsEntity.setUserId(userId);
				srPositionBasicsEntity.setRoleName(roleName);
				srPositionBasicsEntity.setMakerRoleId(roleId);

			}
			srPositionBasicsEntity.setJobTitle(positonBasicsRequest.getJobTitle());

			if (businessUnitRepository.existsById(positonBasicsRequest.getBusinessUnitId())) {
				srPositionBasicsEntity.setBusinessUnitId(positonBasicsRequest.getBusinessUnitId());
			} else {
				log.info("BusinessUnit Id is required");
				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(Constants.BUSINESS_UNIT_REQUIRED));
			}
			if (departmentsRepository.existsById(positonBasicsRequest.getDepartmentId())) {
				srPositionBasicsEntity.setDepartmentId(positonBasicsRequest.getDepartmentId());
			} else {
				log.info("Department Id is required");
				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(Constants.DEPARTMENT_REQUIRED));
			}

			srPositionBasicsEntity.setReportingManagerInfo(positonBasicsRequest.getReportingManagerInfo());
			srPositionBasicsEntity.setLocation(positonBasicsRequest.getLocation());
			srPositionBasicsEntity.setSeniorityLevel(positonBasicsRequest.getSeniorityLevel());
			srPositionBasicsEntity.setOpenings(positonBasicsRequest.getOpenings());
			srPositionBasicsEntity.setTargetStartDate(positonBasicsRequest.getTargetStartDate());
			srPositionBasicsEntity.setEmploymentType(positonBasicsRequest.getEmploymentType());
			srPositionBasicsEntity.setWorkMode(positonBasicsRequest.getWorkMode());
			srPositionBasicsEntity.setPriority(positonBasicsRequest.getPriority());

			srPositionBasicsEntity = positionBasicsRepository.save(srPositionBasicsEntity);
		}
		// business screen logic
		if (request.getBusinessJustificationRequest() != null) {

			BusinessJustificationRequest businessJustificationRequest = request.getBusinessJustificationRequest();

			ApiResponse<?> error = validateBusinessJustification(businessJustificationRequest, file);
			if (error != null)
				return error;

			BusinessJustificationEntity businessJustificationEntity = null;

			srId = businessJustificationRequest.getSrId();

			if (srId == null || srId.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "srId is required", List.of("srId is required"));
			}

			businessJustificationEntity = businessJustificationRepository.findBySrId(srId).orElse(null);
			if (businessJustificationEntity == null) {
				businessJustificationEntity = new BusinessJustificationEntity();

				businessJustificationEntity.setSrId(srId);
				businessJustificationEntity.setSubmitted(false);
				businessJustificationEntity.setApproved(false);
			}

			businessJustificationEntity.setRequisitionType(businessJustificationRequest.getRequisitionType());
			businessJustificationEntity.setBusinessCase(businessJustificationRequest.getBusinessCase());
			businessJustificationEntity.setImpactIfNotFilled(businessJustificationRequest.getImpactIfNotFilled());
			businessJustificationEntity.setReplacesEmployee(businessJustificationRequest.getReplacesEmployee());

			if (file != null && !file.isEmpty()) {

				String fileKey = srId + "-" + file.getOriginalFilename();

				try {
					uploadToMinio(file, fileKey);
				} catch (Exception e) {
					return ApiResponse.failure(ResponseCode.FAILURE, "File upload failed", List.of(e.getMessage()));
				}

				businessJustificationEntity.setDocument(fileKey);
			}

			businessJustificationRepository.save(businessJustificationEntity);

		}
		// budget and compensation

		if (request.getBudgetAndCompensationRequest() != null) {

			BudgetAndCompensationRequest budgetRequest = request.getBudgetAndCompensationRequest();

			ApiResponse<?> validationResponse = validateBudgetAndCompensation(budgetRequest);
			if (validationResponse != null) {
				finalResponse = validationResponse;
			}
			System.out.println("Validation Response: " + validationResponse);

			if (validationResponse != null
					&& ResponseCode.FAILURE.getCode().equals(validationResponse.getResponsecode())) {
				return validationResponse;
			}

			BusinessValidationResponse result = (BusinessValidationResponse) validationResponse.getData();

			BudgetAndCompensationEntity budgetEntity = null;

			if (budgetRequest.getSrId() != null) {
				budgetEntity = budgetAndCompensationRepository.findBySrId(budgetRequest.getSrId()).orElse(null);
			}

			if (budgetEntity == null) {
				budgetEntity = new BudgetAndCompensationEntity();

				budgetEntity.setSrId(budgetRequest.getSrId());

				budgetEntity.setSubmitted(false);
				budgetEntity.setApproved(false);
			}

			budgetEntity.setProposedTotalCompensation(budgetRequest.getProposedTotalCompensation());

			budgetEntity.setSigningBonus(budgetRequest.getSigningBonus());
			budgetEntity.setEquity(budgetRequest.getEquity());
			budgetEntity.setRelocationBudget(budgetRequest.getRelocationBudget());
			budgetEntity.setMinimumSalary(budgetRequest.getMinSalary());
			budgetEntity.setMaximumSalary(budgetRequest.getMaxSalary());
			budgetEntity.setSigningBonusAmount(budgetRequest.getSigningBonusAmount());
			budgetEntity.setEquityAmount(budgetRequest.getEquityAmount());
			budgetEntity.setRelocationBudgetAmount(budgetRequest.getRelocationBudgetAmount());

			budgetEntity.setAnnualHiringCost(budgetRequest.getAnnualHiringCost());

			if (result != null) {
				budgetEntity.setBudgetCompensationStatus(result.getStatus());
				budgetEntity.setStatus(result.getMessage());
			}

			budgetAndCompensationRepository.save(budgetEntity);
		}

		if (request.getRolesAndRequirementsRequest() != null) {

			RolesAndRequirementsRequest rolesAndRequirementsRequest = request.getRolesAndRequirementsRequest();

			ApiResponse<?> error = validateRoleRequirements(rolesAndRequirementsRequest);
			if (error != null)
				return error;

			RolesAndRequirementsEntity entity = null;

			if (rolesAndRequirementsRequest.getSrId() != null) {
				entity = rolesAndRequirementsRepository.findBySrId(rolesAndRequirementsRequest.getSrId()).orElse(null);
			}

			if (entity == null) {

				entity = new RolesAndRequirementsEntity();

				srId = rolesAndRequirementsRequest.getSrId();

				if (srId == null || srId.isEmpty()) {
					return ApiResponse.failure(ResponseCode.FAILURE, "srId is required", List.of("srId is required"));
				}

				entity.setSrId(srId);
				entity.setSubmitted(false);
				entity.setApproved(false);
			}

			entity.setSkillsMustHave(rolesAndRequirementsRequest.getSkillsMustHave() != null
					? String.join(",", rolesAndRequirementsRequest.getSkillsMustHave())
					: null);

			entity.setNiceToHaveSkills((rolesAndRequirementsRequest.getNiceToHaveSkills() != null
					&& !rolesAndRequirementsRequest.getNiceToHaveSkills().isEmpty())
							? String.join(",", rolesAndRequirementsRequest.getNiceToHaveSkills())
							: null);

			entity.setEducationRequirement(rolesAndRequirementsRequest.getEducationRequirement());
			entity.setMinExperience(rolesAndRequirementsRequest.getMinExperience());
			entity.setMaxExperience(rolesAndRequirementsRequest.getMaxExperience());

			entity.setCertificationsRequired((rolesAndRequirementsRequest.getCertificationsRequired() != null
					&& !rolesAndRequirementsRequest.getCertificationsRequired().isEmpty())
							? String.join(",", rolesAndRequirementsRequest.getCertificationsRequired())
							: null);

			entity.setMinInterviewRounds(rolesAndRequirementsRequest.getMinInterviewRounds());
			entity.setMaxInterviewRounds(rolesAndRequirementsRequest.getMaxInterviewRounds());

			entity.setAssessmentRequired(rolesAndRequirementsRequest.getAssessmentRequired());
			entity.setTravelRequirement(rolesAndRequirementsRequest.getTravelRequirement());

			entity.setLanguages((rolesAndRequirementsRequest.getLanguages() != null
					&& !rolesAndRequirementsRequest.getLanguages().isEmpty())
							? String.join(",", rolesAndRequirementsRequest.getLanguages())
							: null);

			rolesAndRequirementsRepository.save(entity);

		}

		if (request.getSourcingStrategyRequest() != null) {

			SourcingStrategyRequest sourcingStrategyRequest = request.getSourcingStrategyRequest();

			ApiResponse<?> error = validateSourcingStrategyRequest(sourcingStrategyRequest);
			if (error != null)
				return error;

			srId = sourcingStrategyRequest.getSrId();

			if (srId == null || srId.isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "srId is required", List.of("srId is required"));
			}

			SourcingStrategyEntity entity = sourceStrategyRepository.findBySrId(srId).orElse(null);

			if (entity == null) {
				entity = new SourcingStrategyEntity();

				entity.setSrId(srId);
				entity.setSubmitted(false);
				entity.setApproved(false);
			}

			entity.setInternalBoard(sourcingStrategyRequest.getInternalBoard());
			entity.setNaukri(sourcingStrategyRequest.getNaukri());
			entity.setLinkedIn(sourcingStrategyRequest.getLinkedIn());
			entity.setIndeed(sourcingStrategyRequest.getIndeed());
			entity.setCompanySite(sourcingStrategyRequest.getCompanySite());
			entity.setAgencyRpo(sourcingStrategyRequest.getAgencyRpo());
			entity.setInternalFirstPolicy(sourcingStrategyRequest.getInternalFirstPolicy());
			entity.setSourcingBudget(sourcingStrategyRequest.getSourcingBudget());
			entity.setReferralEnabled(sourcingStrategyRequest.getReferralEnabled());
			entity.setReferralAmount(sourcingStrategyRequest.getReferralAmount());
			entity.setDiversityEnabled(sourcingStrategyRequest.getDiversityEnabled());
			entity.setDiversityTags(sourcingStrategyRequest.getDiversityTags());

			sourceStrategyRepository.save(entity);

		}

		if (request.getReviewRequest() != null) {
			ReviewRequest reviewRequest = request.getReviewRequest();

			if (reviewRequest.getSrId() == null || reviewRequest.getSrId().isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "srId is required",
						List.of("srId cannot be null or empty"));
			}
			try {
				final String finalSrId = srId;

				positionBasicsRepository.findBySrId(srId).ifPresent(entity -> {

					entity.setSubmitted(true);
					entity.setApproved(false);
					entity.setSubmittedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
					positionBasicsRepository.save(entity);

				});

				businessJustificationRepository.findBySrId(srId).ifPresent(entity -> {

					entity.setSubmitted(true);
					entity.setApproved(false);
					businessJustificationRepository.save(entity);
				});

				budgetAndCompensationRepository.findBySrId(srId).ifPresent(entity -> {

					entity.setSubmitted(true);
					entity.setApproved(false);
					budgetAndCompensationRepository.save(entity);
				});
				rolesAndRequirementsRepository.findBySrId(srId).ifPresent(entity -> {

					entity.setSubmitted(true);
					entity.setApproved(false);
					rolesAndRequirementsRepository.save(entity);
				});
				sourceStrategyRepository.findBySrId(srId).ifPresent(entity -> {

					entity.setSubmitted(true);
					entity.setApproved(false);
					sourceStrategyRepository.save(entity);

				});

				processApprovalChain(finalSrId);
                Integer checkerRoleId=null;
				Map<Integer, List<String>> roleEmailMap = processApprovalChain(finalSrId);
				for (Map.Entry<Integer, List<String>> entry : roleEmailMap.entrySet()) {

				     checkerRoleId = entry.getKey();
				}
				

				Optional<SRPositionBasicsEntity> srOptional = positionBasicsRepository.findBySrId(finalSrId);

				if (srOptional.isPresent()) {

					SRPositionBasicsEntity srEntity = srOptional.get();

					NotificationEvent event = new NotificationEvent();
					userId = srEntity.getUserId();

					event.setProcessId(srEntity.getSrId());
					
					

					event.setMakerRoleName(srEntity.getRoleName());
					event.setMakerRoleId(srEntity.getMakerRoleId());
					String makerEmail = userRepository.findByUserId(userId).get().getEmail();
					log.info("maker email is" + makerEmail);
					event.setMakerEmailAddress(makerEmail);
					event.setMakerMessage(
							"Staffing Requisition has been created successfully and submitted for Level 1 approval");
					event.setMakerNotificationTitle("SR Created");
					// Department Name
					String deptName = "";

					if (srEntity.getDepartmentId() != null) {

						Optional<DepartmentsEntity> deptOpt = departmentsRepository
								.findById(srEntity.getDepartmentId());

						if (deptOpt.isPresent()) {
							deptName = deptOpt.get().getDepartmentName();
						}
					}

					event.setDeptName(deptName);
					event.setMakerEmailBody(String.format(Constants.SR_SUBMITTED_MAIL_BODY, srEntity.getCreatedBy(),
							srEntity.getSrId(), srEntity.getJobTitle(), event.getDeptName(), srEntity.getOpenings(),
							srEntity.getLocation(), srEntity.getEmploymentType(), srEntity.getPriority(),
							srEntity.getCreatedOn()));
					

					event.setCheckerNotificationTitle("SR Created ");
					
					String checkerRoleName=rolesRepository.findByRoleId(checkerRoleId).get().getRoleName();
					event.setCheckerRoleName(checkerRoleName);

					event.setCheckerMessage("A new Staffing Requisition is awaiting your review and approval.");

					event.setType("SR");

					event.setRoleEmailMap(roleEmailMap);
					log.info("role map is" + roleEmailMap);
					event.setCheckerEmailBody(String.format(Constants.SR_TO_BE_APPROVED_BY_FIRST_APPROVER_MAIL_BODY,
							"Department Head", srEntity.getSrId(), srEntity.getJobTitle(), event.getDeptName(),
							srEntity.getCreatedBy(), srEntity.getOpenings(), srEntity.getLocation(),
							srEntity.getEmploymentType(), srEntity.getPriority(), srEntity.getCreatedOn()));

					log.info("email body is" + event);

					notificationService.callNotification(event);

					log.info("The Event is : " + event);

				}

			} catch (Exception e) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Failed to submit SR", List.of(e.getMessage()));
			}
		}
		return finalResponse != null ? finalResponse
				: ApiResponse.success(ResponseCode.SUCCESS, "Staffing Requisition processed successfully", srId);

	}

	private ApiResponse<?> validateObject(String value, String fieldName) {
		if (value == null || value.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " is required",
					List.of(fieldName + " is required"));
		}

		if (fieldName.equals("businessCase") || fieldName.equals("impactIfNotFilled")) {
			if (value.length() < 100 || value.length() > 2000) {
				return ApiResponse.failure(ResponseCode.FAILURE,
						"No of Character Should be more than 100 and less than 2000",
						List.of("No of Character Should be more than 100 and less than 2000"));
			}
		}

		if (value instanceof String str && str.trim().isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " is required",
					List.of(fieldName + " is required"));
		}

		return null;
	}

	private String getUsernameFromToken() {

		String authHeader = httpServletRequest.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			String username = jwtService.extractUsernameFromClaims(token);

			if (username == null || username.isBlank()) {

				throw new RuntimeException("No username found in token");
			}

			return username;

		} else {

			throw new RuntimeException("Invalid or missing Authorization header");
		}
	}

	private Long getUserIdFromToken() {

		String authHeader = httpServletRequest.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			return jwtService.extractUserId(token);

		} else {

			throw new RuntimeException("Invalid or missing Authorization header");
		}
	}

	private String getRoleNameFromToken() {

		String authHeader = httpServletRequest.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			String roleName = jwtService.extractRole(token);

			if (roleName == null || roleName.isBlank()) {

				throw new RuntimeException("No role found in token");
			}

			return roleName;

		} else {

			throw new RuntimeException("Invalid or missing Authorization header");
		}
	}
//method to verify the approvals

	private Map<Integer, List<String>> processApprovalChain(String srId) {

		Map<Integer, List<String>> roleEmailMap = new HashMap<>();

		FunctionalityEntity functionality = functionalityRepository
				.findByFunctionalityName(FunctionalityTypes.SR_Approvals.name())
				.orElseThrow(() -> new RuntimeException("Functionality not found"));

		Integer functionalityId = functionality.getId();

		log.info("Functionality Id : {}", functionalityId);

		ApprovalChainEntity approvalChainEntity = approvalChainRepository.findByFunctionality(functionalityId);

		// CHECK CHAIN EXISTS
		if (approvalChainEntity == null) {

			throw new RuntimeException("Approval chain not configured");

		}

		// CHECK ACTIVE STATUS
//		if (!"ACTIVE".equalsIgnoreCase(approvalChainEntity.getStatus())) {
//
//			throw new RuntimeException("Approval chain is inactive");
//
//		}

		
		// REMAINING CODE WILL RUN
		// ONLY IF STATUS IS ACTIVE
		

		List<LevelConfig> levels = approvalChainEntity.getLevelConfig();

		// sort levels
		levels.sort(Comparator.comparing(LevelConfig::getLevel));

		Optional<ApprovalsChildEntity> optionalChild = approvalsChildRepository.findByProcessId(srId);

		ApprovalsChildEntity childEntity;

		if (optionalChild.isPresent()) {

			childEntity = optionalChild.get();

		} else {

			childEntity = new ApprovalsChildEntity();

			childEntity.setProcessId(srId);

			// initially first approver enabled
			childEntity.setApprover1(true);
		}

		// set roles
		for (LevelConfig lvl : levels) {

			Integer roleId = lvl.getRoleId();

			Integer department = lvl.getDepartmentId();

			childEntity.setDepartment(department);

			if (lvl.getLevel() == 1) {

				childEntity.setRole1(roleId);

			} else if (lvl.getLevel() == 2) {

				childEntity.setRole2(roleId);

			} else if (lvl.getLevel() == 3) {

				childEntity.setRole3(roleId);
			}
		}

		Optional<SRPositionBasicsEntity> srOptional = positionBasicsRepository.findBySrId(srId);

		if (srOptional.isPresent()) {

			SRPositionBasicsEntity srEntity = srOptional.get();

			childEntity.setSubmittedBy(srEntity.getUserId());
		}

		approvalsChildRepository.save(childEntity);

		// RETURN EMAILS BASED ON APPROVER FLAG

		Integer roleId = null;

		// approver1 true -> send role1 mails
		if (Boolean.TRUE.equals(childEntity.getApprover1()) && !Boolean.TRUE.equals(childEntity.getApprover2())) {

			roleId = childEntity.getRole1();

			log.info("Sending mails for Role1");

		}

		// approver2 true -> send role2 mails
		else if (Boolean.TRUE.equals(childEntity.getApprover2()) && !Boolean.TRUE.equals(childEntity.getApprover3())) {

			roleId = childEntity.getRole2();

			log.info("Sending mails for Role2");

		}

		// approver3 true -> send role3 mails
		else if (Boolean.TRUE.equals(childEntity.getApprover3())) {

			roleId = childEntity.getRole3();

			log.info("Sending mails for Role3");
		}

		// fetch emails
		if (roleId != null) {

			List<Integer> userIds = assignRolesRepository.findByRoleId(roleId).stream()
					.map(AssignRolesEntity::getUserId).toList();

			List<String> emails = userRepository.findByUserIdIn(userIds).stream().map(UserEntity::getEmail)
					.filter(Objects::nonNull).toList();

			roleEmailMap.put(roleId, emails);

			log.info("Role Email Map : {}", roleEmailMap);
		}

		return roleEmailMap;
	}

	private ApiResponse<?> validateObject(Object value, String fieldName) {
		if (value instanceof Number) {
			Number num = (Number) value;
			if (num.doubleValue() <= 0) {
				return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " must be greater than 0",
						List.of(fieldName + " must be positive"));
			}
		}

		return null;
	}

	private ApiResponse<?> validateFile(MultipartFile file, String fieldName) {

		if (file != null && !file.isEmpty()) {

			if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
				return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " must be a PDF file",
						List.of(fieldName + " only accepts PDF format"));
			}

			long maxSize = 20 * 1024 * 1024;
			if (file.getSize() > maxSize) {
				return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " must be less than 20MB",
						List.of(fieldName + " size exceeded"));
			}
		}

		return null;
	}

	private void uploadToMinio(MultipartFile offerLetter, String fileKey) throws Exception {

		log.info("staffingRequisitonServiceImpl::Inside uploadToMinio method");

		minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKET).object(fileKey)
				.stream(offerLetter.getInputStream(), offerLetter.getSize(), -1)
				.contentType(offerLetter.getContentType()).build());

		log.info("staffingRequisitionServiceImpl::Exit from uploadToMinio method");
	}

	public ApiResponse<?> validatePositonBasicsRequest(PositonBascicsRequest req) {
		ApiResponse<?> error;

		if (req.getJobTitle() != null) {
			error = validateObject(req.getJobTitle(), "jobTitle");
			if (error != null)
				return error;
		}

		if (req.getDepartmentId() != null) {
			error = validateObject(req.getDepartmentId(), "departmentId");
			if (error != null)
				return error;
		}

		if (req.getBusinessUnitId() != null) {
			error = validateObject(req.getBusinessUnitId(), "businessUnitId");
			if (error != null)
				return error;
		}

		if (req.getReportingManagerInfo() != null) {

			if (req.getReportingManagerInfo().isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "reportingManagerInfo cannot be empty",
						List.of("reportingManagerInfo cannot be empty"));
			}

			for (Integer managerId : req.getReportingManagerInfo()) {
				if (managerId == null || managerId <= 0) {
					return ApiResponse.failure(ResponseCode.FAILURE, "Invalid reportingManagerInfo",
							List.of("Each managerId must be valid"));
				}
			}
		}

		if (req.getLocation() != null) {
			error = validateObject(req.getLocation(), "location");
			if (error != null)
				return error;
		}

		if (req.getSeniorityLevel() != null) {
			error = validateObject(req.getSeniorityLevel(), "seniorityLevel");
			if (error != null)
				return error;
		}

		if (req.getOpenings() != null) {
			error = validateObject(req.getOpenings(), "openings");
			if (error != null)
				return error;
		}

		if (req.getTargetStartDate() != null) {
			error = validateObject(req.getTargetStartDate().toString(), "targetStartDate");
			if (error != null)
				return error;
		}

		if (req.getEmploymentType() != null) {
			error = validateObject(req.getEmploymentType(), "employmentType");
			if (error != null)
				return error;
		}

		if (req.getWorkMode() != null) {
			error = validateObject(req.getWorkMode(), "workMode");
			if (error != null)
				return error;
		}

		if (req.getPriority() != null) {
			error = validateObject(req.getPriority(), "priority");
			if (error != null)
				return error;
		}

		return null;
	}

	// method for businessvalidations
	private ApiResponse<?> validateBusinessJustification(BusinessJustificationRequest bj, MultipartFile file) {

		ApiResponse<?> error;

		if (bj.getRequisitionType() != null) {
			error = validateObject(bj.getRequisitionType(), "requisitionType");
			if (error != null)
				return error;
			if (!List.of("New Headcount", "Backfill", "Replacement", "Contract to Perm")
					.contains(bj.getRequisitionType())) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid requisitionType",
						List.of("Allowed values: New Headcount, Backfill, Replacement, Contract to Perm"));
			}
		}

		if (bj.getBusinessCase() != null) {
			error = validateObject(bj.getBusinessCase(), "businessCase");
			if (error != null)
				return error;
		}

		if (bj.getImpactIfNotFilled() != null) {
			error = validateObject(bj.getImpactIfNotFilled(), "impactIfNotFilled");
			if (error != null)
				return error;
		}
		if (bj.getRequisitionType() != null && ("Backfill".equalsIgnoreCase(bj.getRequisitionType())
				|| "Replacement".equalsIgnoreCase(bj.getRequisitionType()))) {

			if (bj.getReplacesEmployee() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "replacesEmployee is required",
						List.of("replacesEmployee is required"));
			}
		}

		if (file != null && !file.isEmpty()) {
			error = validateFile(file, "document");
			if (error != null)
				return error;
		}
		return null;
	}

	// method for budget and compensation validations

	private ApiResponse<?> validateBudgetAndCompensation(BudgetAndCompensationRequest req) {

		ApiResponse<?> error;

		if (req.getMinSalary() != null) {
			error = validateObject(req.getMinSalary(), "minSalary");
			if (error != null)
				return error;
		}

		if (req.getMaxSalary() != null) {
			error = validateObject(req.getMaxSalary(), "maxSalary");
			if (error != null)
				return error;
		}

		if (req.getProposedTotalCompensation() != null) {
			error = validateObject(req.getProposedTotalCompensation(), "proposedTotalCompensation");
			if (error != null)
				return error;

		}

		if (Boolean.TRUE.equals(req.getSigningBonus())) {
			if (req.getSigningBonusAmount() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "signingBonusAmount is required",
						List.of("Enter signing bonus amount"));

			}
		}

		if (Boolean.TRUE.equals(req.getEquity())) {
			if (req.getEquityAmount() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "equityAmount is required",
						List.of("Enter equity amount"));

			}
		}

		if (Boolean.TRUE.equals(req.getRelocationBudget())) {
			if (req.getRelocationBudgetAmount() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "relocationBudgetAmount is required",
						List.of("Enter relocation budget amount"));
			}
		}

		int total =

				(req.getProposedTotalCompensation() != null ? req.getProposedTotalCompensation() : 0)
						+ (req.getSigningBonusAmount() != null ? req.getSigningBonusAmount() : 0)
						+ (req.getEquityAmount() != null ? req.getEquityAmount() : 0)
						+ (req.getRelocationBudgetAmount() != null ? req.getRelocationBudgetAmount() : 0);

		if (req.getAnnualHiringCost() != null) {
			if (total != req.getAnnualHiringCost()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "AnnualHiringCost mismatch",
						List.of("Sum of all components must equal Annual Hiring Cost"));

			}
		}
		if (req.getMinSalary() != null && req.getMaxSalary() != null && req.getProposedTotalCompensation() != null) {
			Long min = req.getMinSalary();
			Long max = req.getMaxSalary();
			Long proposed = req.getAnnualHiringCost();
			if (min > max) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid salary range",

						List.of("minSalary cannot be greater than maxSalary"));
			}
			double maxWith5Percent = max + (max * 0.05);
			if (proposed >= min && proposed <= max) {
				return ApiResponse.success(ResponseCode.SUCCESS, "Success",
						new BusinessValidationResponse("GREEN", "Within Range"));
			} else if (proposed < min) {
				return ApiResponse.success(ResponseCode.SUCCESS, "Success",
						new BusinessValidationResponse("YELLOW", "Offer Risk"));
			} else if (proposed > max && proposed <= maxWith5Percent) {
				return ApiResponse.success(ResponseCode.SUCCESS, "Success",
						new BusinessValidationResponse("YELLOW", "Range Review Required"));
			} else {
				return ApiResponse.success(ResponseCode.SUCCESS, "Success",
						new BusinessValidationResponse("RED", "Out of Range"));

			}
		}
		return null;

	}

	private ApiResponse<?> validateRoleRequirements(RolesAndRequirementsRequest req) {

		ApiResponse<?> error;

		if (req.getSrId() == null || req.getSrId().isBlank()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "srId is required",
					List.of("srId cannot be null or empty"));
		}

		if (req.getSkillsMustHave() != null) {
			if (req.getSkillsMustHave().isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "skillsMustHave cannot be empty",
						List.of("Provide at least one must-have skill"));
			}
		}

		if (req.getEducationRequirement() != null) {
			error = validateObject(req.getEducationRequirement(), "educationRequirement");
			if (error != null)
				return error;
		}

		if (req.getMinExperience() != null && req.getMaxExperience() != null) {

			if (req.getMinExperience() < 0 || req.getMaxExperience() < 0) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid experience",
						List.of("Experience cannot be negative"));
			}

			if (req.getMinExperience() > req.getMaxExperience()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid experience range",
						List.of("minExperience cannot be greater than maxExperience"));
			}
		}

		if (req.getMinInterviewRounds() != null && req.getMaxInterviewRounds() != null) {

			if (req.getMinInterviewRounds() <= 0 || req.getMaxInterviewRounds() <= 0) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid interview rounds",
						List.of("Interview rounds must be greater than 0"));
			}

			if (req.getMinInterviewRounds() > req.getMaxInterviewRounds()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid interview range",
						List.of("minInterviewRounds cannot be greater than maxInterviewRounds"));
			}
		}

		return null;
	}

	private ApiResponse<?> validateSourcingStrategyRequest(SourcingStrategyRequest req) {

		ApiResponse<?> error;

		if (req == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Request is required",
					List.of("SourcingStrategyRequest cannot be null"));
		}

		if (req.getSrId() == null || req.getSrId().isBlank()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "srId is required",
					List.of("srId cannot be null or empty"));
		}

		if (req.getInternalFirstPolicy() != null) {
			error = validateObject(req.getInternalFirstPolicy(), "internalFirstPolicy");
			if (error != null)
				return error;
		}

		boolean hasJobBoard = Boolean.TRUE.equals(req.getInternalBoard()) || Boolean.TRUE.equals(req.getLinkedIn())
				|| Boolean.TRUE.equals(req.getNaukri()) || Boolean.TRUE.equals(req.getIndeed())
				|| Boolean.TRUE.equals(req.getCompanySite());

		if (!hasJobBoard) {
			return ApiResponse.failure(ResponseCode.FAILURE, "targetJobBoard is required",
					List.of("At least one job board must be selected"));
		}

		if (Boolean.TRUE.equals(req.getReferralEnabled())) {

			if (req.getReferralAmount() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "referralAmount is required",
						List.of("Provide referral amount"));
			}

			if (req.getReferralAmount() <= 0) {
				return ApiResponse.failure(ResponseCode.FAILURE, "referralAmount must be greater than 0",
						List.of("Referral amount must be positive"));
			}
		}

		if (Boolean.TRUE.equals(req.getDiversityEnabled())) {

			if (req.getDiversityTags() == null || req.getDiversityTags().isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "diversityTags is required",
						List.of("Provide diversity tags"));
			}
		}

		if (req.getSourcingBudget() != null && req.getSourcingBudget() < 0) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid sourcingBudget",
					List.of("Sourcing budget cannot be negative"));
		}

		return null;
	}

	@Override
	public ApiResponse<?> getBySrId(String srId) {

		log.info("StaffRequisitionsServiceImpl : Inside getBySrId method");

		try {

			if (srId == null || srId.isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, Constants.SR_ID_IS_REQUIRED,
						List.of(Constants.SR_ID_CANNOT_BE_NULL_OR_EMPTY));
			}

			SRPositionBasicsEntity srPositionBasicsEntity = positionBasicsRepository.findBySrId(srId).orElse(null);
			BusinessJustificationEntity businessJustificationEntity = businessJustificationRepository.findBySrId(srId)
					.orElse(null);
			BudgetAndCompensationEntity budgetAndCompensationEntity = budgetAndCompensationRepository.findBySrId(srId)
					.orElse(null);
			RolesAndRequirementsEntity rolesAndRequirementsEntity = rolesAndRequirementsRepository.findBySrId(srId)
					.orElse(null);
			SourcingStrategyEntity sourcingStrategyEntity = sourceStrategyRepository.findBySrId(srId).orElse(null);

			if (srPositionBasicsEntity == null && businessJustificationEntity == null
					&& budgetAndCompensationEntity == null && rolesAndRequirementsEntity == null
					&& sourcingStrategyEntity == null) {

				return ApiResponse.failure(ResponseCode.FAILURE, Constants.NO_DATA_FOUND,
						List.of(Constants.INVALID_SR_ID_IS + srId));
			}

			StaffingRequisitionResponseDto response = new StaffingRequisitionResponseDto();
			if (srPositionBasicsEntity != null) {

				PositonBasicsResponse positonBasicsResponse = new PositonBasicsResponse();

				positonBasicsResponse.setId(srPositionBasicsEntity.getId());
				positonBasicsResponse.setSrId(srPositionBasicsEntity.getSrId());
				positonBasicsResponse.setJobTitle(srPositionBasicsEntity.getJobTitle());

				Integer businessId = srPositionBasicsEntity.getBusinessUnitId();

				if (businessId != null) {
					businessUnitRepository.findById(businessId)
							.ifPresent(bu -> positonBasicsResponse.setBusinessUnitName(bu.getBusinessName()));
				}

				Integer deptId = srPositionBasicsEntity.getDepartmentId();

				positonBasicsResponse.setDepartmentId(deptId);

				if (deptId != null) {

					Optional<DepartmentsEntity> optionalDepartment = departmentsRepository.findById(deptId);
					if (optionalDepartment.isPresent()) {
						DepartmentsEntity department = optionalDepartment.get();
						positonBasicsResponse.setDepartmentName(department.getDepartmentName());
					}
				}

				positonBasicsResponse.setReportingManagerInfo(srPositionBasicsEntity.getReportingManagerInfo());
				positonBasicsResponse.setLocation(srPositionBasicsEntity.getLocation());
				Integer seniorityLevelId = srPositionBasicsEntity.getSeniorityLevel();

				if (seniorityLevelId != null) {
					seniorityLevelRepository.findById(seniorityLevelId).ifPresent(sl -> {
						String seniorityLevelName = sl.getSeniorityLevel();
						positonBasicsResponse.setSeniorityLevelName(seniorityLevelName);
					});
				}

				positonBasicsResponse.setOpenings(srPositionBasicsEntity.getOpenings());
				positonBasicsResponse.setTargetStartDate(srPositionBasicsEntity.getTargetStartDate());
				positonBasicsResponse.setWorkMode(srPositionBasicsEntity.getWorkMode());
				positonBasicsResponse.setEmploymentType(srPositionBasicsEntity.getEmploymentType());
				positonBasicsResponse.setPriority(srPositionBasicsEntity.getPriority());
				positonBasicsResponse.setApproved(srPositionBasicsEntity.getApproved());
				positonBasicsResponse.setCreatedOn(srPositionBasicsEntity.getCreatedOn());
				positonBasicsResponse.setSubmittedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
				positonBasicsResponse.setTargetStartDate(srPositionBasicsEntity.getTargetStartDate());
				positonBasicsResponse.setCreatedBy(srPositionBasicsEntity.getCreatedBy());
				positonBasicsResponse.setUserId(srPositionBasicsEntity.getUserId());
				positonBasicsResponse.setApprover1(srPositionBasicsEntity.getApprover1());
				positonBasicsResponse.setApprover2(srPositionBasicsEntity.getApprover2());
				positonBasicsResponse.setApprover3(srPositionBasicsEntity.getApprover3());
				positonBasicsResponse.setApprover1By(srPositionBasicsEntity.getApprover1By());
				positonBasicsResponse.setApprover2By(srPositionBasicsEntity.getApprover2By());
				positonBasicsResponse.setApprover3By(srPositionBasicsEntity.getApprover3By());
				positonBasicsResponse.setDateOfApproval1(srPositionBasicsEntity.getDateOfApproval1());
				positonBasicsResponse.setDateOfApproval2(srPositionBasicsEntity.getDateOfApproval2());
				positonBasicsResponse.setDateOfApproval3(srPositionBasicsEntity.getDateOfApproval3());
				positonBasicsResponse.setCommentsByApprover1(srPositionBasicsEntity.getCommentsByApprover1());
				positonBasicsResponse.setCommentsByApprover2(srPositionBasicsEntity.getCommentsByApprover2());
				positonBasicsResponse.setCommentsByApprover3(srPositionBasicsEntity.getCommentsByApprover3());

				Optional<ApprovalsChildEntity> optionalChildEntity = approvalsChildRepository
						.findByProcessId(srPositionBasicsEntity.getSrId());

				if (optionalChildEntity.isPresent()) {

					ApprovalsChildEntity childEntity = optionalChildEntity.get();

					List<Integer> roleIds = List.of(childEntity.getRole1(), childEntity.getRole2(),
							childEntity.getRole3());

					List<Object[]> roles = rolesRepository.findRoleNamesByIds(roleIds);

					Map<Integer, String> roleMap = roles.stream()
							.collect(Collectors.toMap(r -> (Integer) r[0], r -> (String) r[1]));

					positonBasicsResponse.setApprover1Role(roleMap.get(childEntity.getRole1()));
					positonBasicsResponse.setApprover2Role(roleMap.get(childEntity.getRole2()));
					positonBasicsResponse.setApprover3Role(roleMap.get(childEntity.getRole3()));
				}
				response.setPositonBasicsResponse(positonBasicsResponse);
			}

			if (businessJustificationEntity != null) {

				BusinessJustificationResponse businessJustificationResponse = new BusinessJustificationResponse();

				businessJustificationResponse.setId(businessJustificationEntity.getId());
				businessJustificationResponse.setSrId(businessJustificationEntity.getSrId());
				businessJustificationResponse.setRequisitionType(businessJustificationEntity.getRequisitionType());
				businessJustificationResponse.setBusinessCase(businessJustificationEntity.getBusinessCase());
				businessJustificationResponse.setImpactIfNotFilled(businessJustificationEntity.getImpactIfNotFilled());
				businessJustificationResponse.setReplacesEmployee(businessJustificationEntity.getReplacesEmployee());
				businessJustificationResponse.setDocument(businessJustificationEntity.getDocument());
				businessJustificationResponse.setSubmitted(businessJustificationEntity.getSubmitted());
				businessJustificationResponse.setApproved(businessJustificationEntity.getApproved());

				response.setBusinessJustificationResponse(businessJustificationResponse);
			}
			if (budgetAndCompensationEntity != null) {

				BudgetAndCompensationResponse budgetAndCompensationResponse = new BudgetAndCompensationResponse();

				budgetAndCompensationResponse.setId(budgetAndCompensationEntity.getId());
				budgetAndCompensationResponse.setSrId(budgetAndCompensationEntity.getSrId());
				budgetAndCompensationResponse
						.setProposedTotalCompensation(budgetAndCompensationEntity.getProposedTotalCompensation());
				budgetAndCompensationResponse.setSigningBonus(budgetAndCompensationEntity.getSigningBonus());
				budgetAndCompensationResponse.setEquity(budgetAndCompensationEntity.getEquity());
				budgetAndCompensationResponse.setRelocationBudget(budgetAndCompensationEntity.getRelocationBudget());
				budgetAndCompensationResponse
						.setSigningBonusAmount(budgetAndCompensationEntity.getSigningBonusAmount());
				budgetAndCompensationResponse.setEquityAmount(budgetAndCompensationEntity.getEquityAmount());
				budgetAndCompensationResponse
						.setRelocationBudgetAmount(budgetAndCompensationEntity.getRelocationBudgetAmount());
				budgetAndCompensationResponse.setAnnualHiringCost(budgetAndCompensationEntity.getAnnualHiringCost());
				budgetAndCompensationResponse.setSubmitted(budgetAndCompensationEntity.getSubmitted());
				budgetAndCompensationResponse.setApproved(budgetAndCompensationEntity.getApproved());
				budgetAndCompensationResponse.setMinSalary(budgetAndCompensationEntity.getMinimumSalary());
				budgetAndCompensationResponse.setMaxSalary(budgetAndCompensationEntity.getMaximumSalary());

				response.setBudgetAndCompensationResponse(budgetAndCompensationResponse);
			}
			if (rolesAndRequirementsEntity != null) {

				RolesAndRequirementsResponse rolesAndRequirementsResponse = new RolesAndRequirementsResponse();

				rolesAndRequirementsResponse.setId(rolesAndRequirementsEntity.getId());
				rolesAndRequirementsResponse.setSrId(rolesAndRequirementsEntity.getSrId());
				rolesAndRequirementsResponse.setSkillsMustHave(rolesAndRequirementsEntity.getSkillsMustHave() != null
						? Arrays.asList(rolesAndRequirementsEntity.getSkillsMustHave().split(","))
						: Collections.emptyList());

				rolesAndRequirementsResponse
						.setNiceToHaveSkills(rolesAndRequirementsEntity.getNiceToHaveSkills() != null
								? Arrays.asList(rolesAndRequirementsEntity.getNiceToHaveSkills().split(","))
								: Collections.emptyList());
				rolesAndRequirementsResponse
						.setEducationRequirement(rolesAndRequirementsEntity.getEducationRequirement());
				rolesAndRequirementsResponse.setTravelRequirement(rolesAndRequirementsEntity.getTravelRequirement());
				rolesAndRequirementsResponse.setMinExperience(rolesAndRequirementsEntity.getMinExperience());
				rolesAndRequirementsResponse.setMaxExperience(rolesAndRequirementsEntity.getMaxExperience());
				rolesAndRequirementsResponse.setMinInterviewRounds(rolesAndRequirementsEntity.getMinInterviewRounds());
				rolesAndRequirementsResponse.setMaxInterviewRounds(rolesAndRequirementsEntity.getMaxInterviewRounds());
				rolesAndRequirementsResponse
						.setCertificationsRequired(rolesAndRequirementsEntity.getCertificationsRequired() != null
								? Arrays.asList(rolesAndRequirementsEntity.getCertificationsRequired().split(","))
								: Collections.emptyList());
				rolesAndRequirementsResponse.setLanguages(rolesAndRequirementsEntity.getLanguages() != null
						? Arrays.asList(rolesAndRequirementsEntity.getLanguages().split(","))
						: Collections.emptyList());
				rolesAndRequirementsResponse.setAssessmentRequired(rolesAndRequirementsEntity.getAssessmentRequired());
				rolesAndRequirementsResponse.setSubmitted(rolesAndRequirementsEntity.getSubmitted());
				rolesAndRequirementsResponse.setApproved(rolesAndRequirementsEntity.getApproved());

				response.setRolesAndRequirementsResponse(rolesAndRequirementsResponse);
			}

			if (sourcingStrategyEntity != null) {

				SourcingStrategyResponse sourcingStrategyResponse = new SourcingStrategyResponse();

				sourcingStrategyResponse.setId(sourcingStrategyEntity.getId());
				sourcingStrategyResponse.setSrId(sourcingStrategyEntity.getSrId());
				sourcingStrategyResponse.setInternalBoard(sourcingStrategyEntity.getInternalBoard());
				sourcingStrategyResponse.setNaukri(sourcingStrategyEntity.getNaukri());
				sourcingStrategyResponse.setLinkedIn(sourcingStrategyEntity.getLinkedIn());
				sourcingStrategyResponse.setIndeed(sourcingStrategyEntity.getIndeed());
				sourcingStrategyResponse.setCompanySite(sourcingStrategyEntity.getCompanySite());
				sourcingStrategyResponse.setAgencyRpo(sourcingStrategyEntity.getAgencyRpo());
				sourcingStrategyResponse.setInternalFirstPolicy(sourcingStrategyEntity.getInternalFirstPolicy());
				sourcingStrategyResponse.setSourcingBudget(sourcingStrategyEntity.getSourcingBudget());
				sourcingStrategyResponse.setReferralEnabled(sourcingStrategyEntity.getReferralEnabled());
				sourcingStrategyResponse.setReferralAmount(sourcingStrategyEntity.getReferralAmount());
				sourcingStrategyResponse.setDiversityEnabled(sourcingStrategyEntity.getDiversityEnabled());
				sourcingStrategyResponse.setDiversityTags(sourcingStrategyEntity.getDiversityTags() != null
						? Arrays.asList(sourcingStrategyEntity.getDiversityTags().split(","))
						: Collections.emptyList());
				sourcingStrategyResponse.setSubmitted(sourcingStrategyEntity.getSubmitted());
				sourcingStrategyResponse.setApproved(sourcingStrategyEntity.getApproved());

				response.setSourcingStrategyResponse(sourcingStrategyResponse);
			}

			return ApiResponse.success(ResponseCode.SUCCESS, Constants.SR_DATA_FETCHED_SUCCESSFULLY, response);

		} catch (Exception e) {

			log.error("Error fetching SR data for srId: {}", srId, e);

			return ApiResponse.failure(ResponseCode.FAILURE, Constants.FAILED_TO_FETCH_SR_DATA,
					List.of(e.getMessage()));
		}
	}
	
	@Override
	public ApiResponse<?> getAllSrList(SpecificationFilterRequest request) {

		try {
			int page = request.getPage() != null ? request.getPage() : 0;
			
			int size = request.getSize() != null ? request.getSize() : 10;
			
			String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdOn";
			
			Sort.Direction direction = "ASC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.ASC
					: Sort.Direction.DESC;
			
			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
			
			String authHeader = httpServletRequest.getHeader("Authorization");
			
			Long userId = null;

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				
				String token = authHeader.substring(7);
				
				userId = jwtService.extractUserId(token);
				
			} else {
				
				return ApiResponse.failure(ResponseCode.FAILURE, "Unauthorized", List.of("Missing or invalid token"));
			}
			
			Specification<SRPositionBasicsEntity> spec = request.buildMyStaffingRequisitionSpecification(userId);
			
			Page<SRPositionBasicsEntity> pageResult = positionBasicsRepository.findAll(spec, pageable);

	        String status = request.getStatus();

	        request.setStatus(null);

			Specification<SRPositionBasicsEntity> countSpec = request.buildMyStaffingRequisitionSpecification(userId);

			List<SRPositionBasicsEntity> allData = positionBasicsRepository.findAll(countSpec);

	        request.setStatus(status);
			
			long allCount = allData.size();
			
			long draftCount = allData.stream().filter(sr -> Boolean.FALSE.equals(sr.getSubmitted())).count();

			long approvedCount = allData.stream()
					.filter(sr -> Boolean.TRUE.equals(sr.getSubmitted()) && Boolean.TRUE.equals(sr.getApproved()))
					.count();

			long rejectedCount = allData.stream()
					.filter(sr -> Boolean.TRUE.equals(sr.getSubmitted()) && Boolean.TRUE.equals(sr.getRejected()))
					.count();


			long pendingCount = allData
					.stream().filter(sr -> Boolean.TRUE.equals(sr.getSubmitted())
							&& !Boolean.TRUE.equals(sr.getApproved()) && !Boolean.TRUE.equals(sr.getRejected()))
					.count();
			
			Map<Integer, String> departmentMap = departmentsRepository.findAll().stream()
					.collect(Collectors.toMap(DepartmentsEntity::getId, DepartmentsEntity::getDepartmentName));

			List<Map<String, Object>> content = pageResult.getContent().stream().map(sr -> {
				Map<String, Object> map = new LinkedHashMap<>();

				map.put("id", sr.getId());
				map.put("srId", sr.getSrId());
				map.put("jobTitle", sr.getJobTitle());
				map.put("departmentName", departmentMap.get(sr.getDepartmentId()));
				map.put("requestedBy", sr.getCreatedBy());
				map.put("requestedOn", sr.getCreatedOn());
				map.put("currentStage",getCurrentStage(sr));
				map.put("pipeline", getPipeline(sr));
				

				String srStatus;

				if (Boolean.TRUE.equals(sr.getApproved())) {
					
					srStatus = "Approved";
					
				} else if (Boolean.TRUE.equals(sr.getRejected())) {
					
					srStatus = "Rejected";
					
				} else if (Boolean.FALSE.equals(sr.getSubmitted())) {

					srStatus = "Draft";

				} else {

					srStatus = "Pending";
				}
				map.put("status", status);

				return map;

			}).toList();

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("content", content);
			response.put("currentPage", pageResult.getNumber());
			response.put("totalPages", pageResult.getTotalPages());
			response.put("totalElements", pageResult.getTotalElements());
			response.put("allRequisitions", allCount);
			response.put("approvedCount", approvedCount);
			response.put("rejectedCount", rejectedCount);
			response.put("pendingCount", pendingCount);
			response.put("draftCount", draftCount);
			return ApiResponse.success(ResponseCode.SUCCESS, "SR Data fetched successfully", response);
			
			}
		 catch (Exception e) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch SR data", List.of(e.getMessage()));
		}
	}

	private String getCurrentStage(SRPositionBasicsEntity sr) {

		try {
			
			Optional<ApprovalsChildEntity> optionalChild = approvalsChildRepository.findByProcessId(sr.getSrId());

			if (optionalChild.isEmpty()) {

				return null;
			}

			ApprovalsChildEntity child = optionalChild.get();
			
			List<Integer> roleIds = new ArrayList<>();

			if (child.getRole1() != null) {

				roleIds.add(child.getRole1());
			}

			if (child.getRole2() != null) {

				roleIds.add(child.getRole2());
			}

			if (child.getRole3() != null) {

				roleIds.add(child.getRole3());
			}

			
			System.out.println(roleIds);

			List<Object[]> roles = rolesRepository.findRoleNamesByIds(roleIds);

			Map<Integer, String> roleMap = roles.stream()
					.collect(Collectors.toMap(r -> (Integer) r[0], r -> (String) r[1]));

			if (Boolean.FALSE.equals(sr.getSubmitted())) {

				return "Draft";
			}

			if (Boolean.TRUE.equals(sr.getRejected())) {


				if (Boolean.TRUE.equals(sr.getApprover3())) {

					return roleMap.get(child.getRole3());
				}

				if (Boolean.TRUE.equals(sr.getApprover2())) {

					return roleMap.get(child.getRole2());
				}

				return roleMap.get(child.getRole1());
			}
			
			if (Boolean.TRUE.equals(sr.getSubmitted())) {

				if (!Boolean.TRUE.equals(sr.getApprover1())) {

					System.out.println(child.getRole1());
					System.out.println(roleMap);
					
					return roleMap.get(child.getRole1());
				}

				if (!Boolean.TRUE.equals(sr.getApprover2())) {

					return roleMap.get(child.getRole2());
				}

				if (!Boolean.TRUE.equals(sr.getApprover3())) {

					return roleMap.get(child.getRole3());
				}

				return roleMap.get(child.getRole3());
			}

			return null;

		} catch (Exception e) {

			 e.printStackTrace();
			return null;
		}
	}
	
	private List<String> getPipeline(SRPositionBasicsEntity sr) {

		try {

			List<String> pipeline = new ArrayList<>();

			if (sr.getRoleName() != null) {

				pipeline.add(sr.getRoleName());
			}
			
			Optional<ApprovalsChildEntity> optionalChild = approvalsChildRepository.findByProcessId(sr.getSrId());

			if (optionalChild.isEmpty()) {

				return pipeline;
			}

			ApprovalsChildEntity child = optionalChild.get();

			List<Integer> roleIds = new ArrayList<>();

			if (child.getRole1() != null) {
				roleIds.add(child.getRole1());
			}

			if (child.getRole2() != null) {
				roleIds.add(child.getRole2());
			}

			if (child.getRole3() != null) {
				roleIds.add(child.getRole3());
			}

			List<Object[]> roles = rolesRepository.findRoleNamesByIds(roleIds);

			Map<Integer, String> roleMap = roles.stream()
					.collect(Collectors.toMap(r -> (Integer) r[0], r -> (String) r[1]));

			if (child.getRole1() != null) {

				pipeline.add(roleMap.get(child.getRole1()));
			}

			if (child.getRole2() != null) {

				pipeline.add(roleMap.get(child.getRole2()));
			}

			if (child.getRole3() != null) {

				pipeline.add(roleMap.get(child.getRole3()));
			}

			return pipeline;

		} catch (Exception e) {

			return Collections.emptyList();
		}
	}
	
	@Override
	public ApiResponse<?> getAllSrListCount() {

		try {
			String authHeader = httpServletRequest.getHeader("Authorization");

			Long userId = null;

			if (authHeader != null && authHeader.startsWith("Bearer ")) {

				String token = authHeader.substring(7);

				userId = jwtService.extractUserId(token);

			} else {

				return ApiResponse.failure(ResponseCode.FAILURE, "Unauthorized", List.of("Missing or invalid token"));
			}

			List<SRPositionBasicsEntity> allData = positionBasicsRepository.findByUserId(userId);

			long allCount = allData.size();

			long draftCount = allData.stream().filter(sr -> Boolean.FALSE.equals(sr.getSubmitted())).count();

			long approvedCount = allData.stream()
					.filter(sr -> Boolean.TRUE.equals(sr.getSubmitted()) && Boolean.TRUE.equals(sr.getApproved()))
					.count();

			long rejectedCount = allData.stream()
					.filter(sr -> Boolean.TRUE.equals(sr.getSubmitted()) && Boolean.TRUE.equals(sr.getRejected()))
					.count();

			long pendingCount = allData
					.stream().filter(sr -> Boolean.TRUE.equals(sr.getSubmitted())
							&& !Boolean.TRUE.equals(sr.getApproved()) && !Boolean.TRUE.equals(sr.getRejected()))
					.count();

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("allRequisitions", allCount);

			response.put("draftCount", draftCount);

			response.put("pendingCount", pendingCount);

			response.put("approvedCount", approvedCount);

			response.put("rejectedCount", rejectedCount);

			return ApiResponse.success(ResponseCode.SUCCESS, "SR list counts fetched successfully", response);

		} catch (Exception e) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch SR list counts", List.of(e.getMessage()));
		}
	}
	

	private String generateSrId(Integer businessUnitId) {

		int year = java.time.LocalDateTime.now().getYear();
		String departmentCode = null;

		if (businessUnitId != null) {
			String deptCode = departmentsRepository.findById(businessUnitId).get().getDeptCode();
			log.info("The Department code is : " + deptCode);

			if (deptCode != null && !deptCode.trim().isEmpty()) {
				departmentCode = deptCode.trim().toUpperCase();
			}
		}
		int srSeq = sequenceGenerator.generateSrSequence();
		String formattedSeq = String.format("%04d", srSeq);

		return "SR-" + year + "-" + departmentCode + "-" + formattedSeq;
	}

	private void sendMakerMail(String srId, Long userId,Integer makerRoleId, String makerMessage, String makerRoleName,
			String notificationTitle, String body, NotificationEvent event) {

		String makerEmail = userRepository.findByUserId(userId).get().getEmail();

		event.setType("SR");

		event.setProcessId(srId);
		event.setMakerMessage(makerMessage);
		event.setMakerRoleName(makerRoleName);
		event.setMakerRoleId(makerRoleId);

		event.setMakerEmailAddress(makerEmail);

		event.setMakerNotificationTitle(notificationTitle);

		event.setMakerEmailBody(body);

		notificationService.callNotification(event);
	}

	@Override
	public ApiResponse<?> srApproval(UpdateSrRequest request) {

		Optional<ApprovalsChildEntity> optional = approvalsChildRepository.findByProcessId(request.getSrId());

		NotificationEvent event = new NotificationEvent();

		if (optional.isEmpty()) {

			return ApiResponse.failure(ResponseCode.FAILURE, "No approval record found", List.of("Invalid SR"));
		}

		ApprovalsChildEntity entity = optional.get();

		// FIND CURRENT LEVEL
		

		int currentLevel;

		if (Boolean.TRUE.equals(entity.getApprover1()) && !Boolean.TRUE.equals(entity.getApprover2())) {

			currentLevel = 1;

		} else if (Boolean.TRUE.equals(entity.getApprover2()) && !Boolean.TRUE.equals(entity.getApprover3())) {

			currentLevel = 2;

		} else if (Boolean.TRUE.equals(entity.getApprover3())) {

			currentLevel = 3;

		} else {

			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid approval flow",
					List.of("No active approval level found"));
		}

		String roleName = getRoleNameFromToken();
		String username = getUsernameFromToken();

	//ROLE VALIDATION
	

		Integer expectedRole = null;

		if (currentLevel == 1) {

			expectedRole = entity.getRole1();

		} else if (currentLevel == 2) {

			expectedRole = entity.getRole2();

		} else if (currentLevel == 3) {

			expectedRole = entity.getRole3();
		}

		String expectedRoleName = rolesRepository.findByRoleId(expectedRole).get().getRoleName();

		if (!roleName.equalsIgnoreCase(expectedRoleName)) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Unauthorized",
					List.of("You are not authorized to approve this level"));
		}

	// MAKER CHECKER VALIDATION

		Long submittedBy = entity.getSubmittedBy();

		if (submittedBy != null) {

			Optional<UserEntity> submittedUserOpt = userRepository.findByUserId(submittedBy);

			if (submittedUserOpt.isPresent()) {

				UserEntity submittedUser = submittedUserOpt.get();

				if (username.equalsIgnoreCase(submittedUser.getUsername())) {

					return ApiResponse.failure(ResponseCode.FAILURE, "Access Denied",
							List.of("You created this SR, so you cannot approve it"));
				}
			}
		}

	//FETCH SR
		

		Optional<SRPositionBasicsEntity> posOpt = positionBasicsRepository.findBySrId(request.getSrId());

		if (posOpt.isEmpty()) {

			return ApiResponse.failure(ResponseCode.FAILURE, "SR not found", List.of("Invalid SR Id"));
		}

		SRPositionBasicsEntity pos = posOpt.get();

		String srId = pos.getSrId();
		Long userId = pos.getUserId();
		String makerRoleName = pos.getRoleName();
		Integer makerRoleId=pos.getMakerRoleId();

		//FIND APPROVAL LEVEL
	

		int approvalLevel = 0;

		if (!Boolean.TRUE.equals(pos.getApprover1())) {

			approvalLevel = 1;

		} else if (!Boolean.TRUE.equals(pos.getApprover2())) {

			approvalLevel = 2;

		} else if (!Boolean.TRUE.equals(pos.getApprover3())) {

			approvalLevel = 3;

		} else {

			return ApiResponse.success("All approvals already completed");
		}

		boolean approved = Boolean.TRUE.equals(request.getApproved());

		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

		// COMMON VARIABLES

		String levelName = "";
		String approverName = "";
		LocalDateTime approvedDate = null;
		Object approvalStatus = null;

		//LEVEL BASED DATA
	

		if (approvalLevel == 1) {

			levelName = "Department Head";

			pos.setApprover1By(username);
			pos.setApprover1Role(roleName);
			pos.setDateOfApproval1(now);
			pos.setCommentsByApprover1(request.getComments());

			approverName = pos.getApprover1By();
			approvedDate = pos.getDateOfApproval1();

			if (approved) {

				pos.setApprover1(true);

				approvalStatus = pos.getApprover1();

				entity.setApprover2(true);
			}

		} else if (approvalLevel == 2) {

			levelName = "HRBP";

			pos.setApprover2By(username);
			pos.setApprover2Role(roleName);
			pos.setDateOfApproval2(now);
			pos.setCommentsByApprover2(request.getComments());

			approverName = pos.getApprover2By();
			approvedDate = pos.getDateOfApproval2();

			if (approved) {

				pos.setApprover2(true);

				approvalStatus = pos.getApprover2();

				entity.setApprover3(true);
			}

		} else if (approvalLevel == 3) {

			levelName = "Finance";

			pos.setApprover3By(username);
			pos.setApprover3Role(roleName);
			pos.setDateOfApproval3(now);
			pos.setCommentsByApprover3(request.getComments());

			approverName = pos.getApprover3By();
			approvedDate = pos.getDateOfApproval3();

			if (approved) {

				pos.setApprover3(true);

				approvalStatus = pos.getApprover3();

				pos.setApproved(true);
				pos.setInProgress(true);
			}
		}

	// COMMON SAVE


		if (approved) {

			pos.setRejected(false);

		} else {

			pos.setRejected(true);
			pos.setInProgress(true);
		}

		positionBasicsRepository.save(pos);
		approvalsChildRepository.save(entity);

	//COMMON MAIL DATA
	
		Map<Integer, List<String>> roleEmailMap = processApprovalChain(request.getSrId()); 
		Integer roleId=null;
		for (Map.Entry<Integer, List<String>> entry : roleEmailMap.entrySet()) {
                   roleId = entry.getKey();
		}
		
        String checkerRoleName=rolesRepository.findByRoleId(roleId).get().getRoleName();
		Integer deptId = pos.getDepartmentId();

		String deptName = departmentsRepository.findById(deptId).get().getDepartmentName();

		event.setProcessId(srId);
		event.setDeptName(deptName);
		event.setType("SR");
		event.setCheckerRoleName(checkerRoleName);
		event.setRoleEmailMap(roleEmailMap);

		// APPROVED FLOW

		if (approved) {

			event.setCheckerNotificationTitle("Level " + approvalLevel + " Approved — " + levelName);

			event.setCheckerMessage("A Staffing Requisition is now under your approval flow for review and approval");

			event.setCheckerEmailBody(String.format(Constants.SR_TO_BE_APPROVED_MAIL_BODY, pos.getSrId(),
					pos.getJobTitle(), deptName, pos.getCreatedBy(), pos.getOpenings(), pos.getLocation(),
					pos.getEmploymentType(), pos.getPriority(), pos.getCreatedOn()));

			String makerSubject = "";
			String makerTitle = "";
			String makerMailBody = "";
			

			if (approvalLevel == 1) {

				makerSubject = "Your Staffing Requisition has been approved by Level 1 (Department Head) and is now under Level 2 approval flow";

				makerTitle = "Level 1 Approved — Department Head";

				makerMailBody = String.format(Constants.SR_APPROVED_NOTIFY, pos.getCreatedBy(), approverName,
						pos.getSrId(), pos.getJobTitle(), deptName,pos.getOpenings(), pos.getLocation(),
						pos.getEmploymentType(), pos.getPriority(), approverName, approvalStatus, approvedDate);

			} else if (approvalLevel == 2) {

				makerSubject = "Your Staffing Requisition has been approved by Level 2 (HRBP) and is now under Level 3 approval flow";

				makerTitle = "Level 2 Approved — HRBP";

				makerMailBody = String.format(Constants.SR_APPROVED_NOTIFY, pos.getCreatedBy(), approverName,
						pos.getSrId(), pos.getJobTitle(), deptName, pos.getOpenings(), pos.getLocation(),
						pos.getEmploymentType(), pos.getPriority(), approverName, approvalStatus, approvedDate);

			} else if (approvalLevel == 3) {

				makerSubject = "Your Staffing Requisition has been fully approved successfully and is now ready for Recruiter Assignment and Job Creation";

				makerTitle = "Level 3 Approved — Finance";

				makerMailBody = String.format(Constants.SR_FULLY_APPROVED_NOTIFY, pos.getCreatedBy(), pos.getSrId(),
						pos.getJobTitle(), deptName, pos.getOpenings(), pos.getLocation(), pos.getEmploymentType(),
						pos.getPriority(), approvedDate);
			}

			sendMakerMail(srId, userId,makerRoleId, makerSubject, makerRoleName, makerTitle, makerMailBody, event);

			return ApiResponse.success("Approved successfully at level " + approvalLevel);
		}

	// REJECT FLOW
	

		else {

			String rejectedMailBody = String.format(Constants.SR_REJECTED_NOTIFY, pos.getCreatedBy(), pos.getSrId(),
					pos.getJobTitle(), deptName, pos.getOpenings(), pos.getLocation(), pos.getEmploymentType(),
					pos.getPriority(), levelName, approverName, approvedDate, request.getComments());

			event.setCheckerNotificationTitle("Level " + approvalLevel + " Rejected — " + levelName);

			event.setCheckerMessage("A Staffing Requisition has been rejected in the approval flow.");

			event.setCheckerEmailBody(rejectedMailBody);

			sendMakerMail(srId, userId,makerRoleId,
					"Your Staffing Requisition has been rejected by Level " + approvalLevel + " (" + levelName + ")",
					makerRoleName,"SR Rejected", rejectedMailBody, event);

			return ApiResponse.success("Rejected successfully at level " + approvalLevel);
		}
	}

	public ApiResponse<?> getSrCounts() {

		log.info("Inside getSrCounts method");

		try {

			List<SRPositionBasicsEntity> srList = positionBasicsRepository.findAll();

			Set<String> uniqueSrIds = new HashSet<>();

			long totalSrs = 0;
			long approved = 0;
			long rejected = 0;
			long inProgress = 0;

			for (SRPositionBasicsEntity sr : srList) {
				if (!uniqueSrIds.contains(sr.getSrId())) {

					uniqueSrIds.add(sr.getSrId());
					totalSrs++;

					if (Boolean.TRUE.equals(sr.getApproved())) {

						approved++;
					} else if (Boolean.TRUE.equals(sr.getRejected())) {
						rejected++;

					} else {
						inProgress++;
					}
				}
			}
			SRCountResponse response = new SRCountResponse();

			response.setTotalSrs(totalSrs);
			response.setApproved(approved);
			response.setRejected(rejected);
			response.setInProgress(inProgress);

			return ApiResponse.success(ResponseCode.SUCCESS, "SR counts fetched successfully", response);

		} catch (Exception e) {
			log.error("Error fetching SR counts", e);
			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch SR counts", List.of(e.getMessage()));
		}
	}

	@Override
	public ApiResponse<?> assignedSrsForApprovals(SpecificationFilterRequest request) {

		log.info("ApprovalServiceImpl::Inside assignedSrsForApprovals");

		String authHeader = httpServletRequest.getHeader("Authorization");
		String roleName = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);
			roleName = jwtService.extractRole(token);
		}

		RolesEntity roleEntity = rolesRepository.findByRoleNameIgnoreCase(roleName);

		if (roleEntity == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Role not found");
		}

		Integer roleId = roleEntity.getRoleId();

		log.info("Logged in roleId : {}", roleId);

		FunctionalityEntity functionality = functionalityRepository
				.findByFunctionalityName(FunctionalityTypes.SR_Approvals.name())
				.orElseThrow(() -> new RuntimeException("Functionality not found"));

		Integer functionalityId = functionality.getId();

		log.info("Functionality Id : {}", functionalityId);

		ApprovalChainEntity approvalChainEntity = approvalChainRepository.findByFunctionality(functionalityId);

		if (approvalChainEntity == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Approval chain not found");
		}

		if (!approvalChainEntity.getStatus().equalsIgnoreCase("active")) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Approval chain is inactive");
		}

		List<LevelConfig> levels = approvalChainEntity.getLevelConfig();

		if (levels == null || levels.isEmpty()) {

			return ApiResponse.failure(ResponseCode.FAILURE, "No approval levels found");
		}

		List<Integer> levelRoleIds = levels.stream().map(LevelConfig::getRoleId).toList();

		log.info("Approval level roleIds : {}", levelRoleIds);

		if (!levelRoleIds.contains(roleId)) {

			return ApiResponse.failure(ResponseCode.FAILURE,
					"Your not authorised person and your role is not assigned for the chain approval");
		}

		List<ApprovalsChildEntity> childEntities = approvalsChildRepository.findAllByRole(roleId);

		if (childEntities == null || childEntities.isEmpty()) {

			return ApiResponse.failure(ResponseCode.FAILURE, "No SR found for this role");
		}

		List<String> srIds = childEntities.stream().map(ApprovalsChildEntity::getProcessId).toList();

		Sort sort = request.getDirection() != null && request.getDirection().equalsIgnoreCase("ASC")
				? Sort.by(request.getSortBy()).ascending()
				: Sort.by(request.getSortBy()).descending();

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Page<SRPositionBasicsEntity> srPage = positionBasicsRepository.findAll(request.toSrApprovalSpecification(srIds),
				pageable);

		List<SRPositionBasicsEntity> srEntities = srPage.getContent();

		Map<String, Object> countFilters = new HashMap<>();

		if (request.getFilters() != null) {

			countFilters.putAll(request.getFilters());

			countFilters.remove("status");
		}

		SpecificationFilterRequest countRequest = new SpecificationFilterRequest();

		countRequest.setFilters(countFilters);

		Specification<SRPositionBasicsEntity> baseSpecification = countRequest.toSrApprovalSpecification(srIds);

		long allCount = positionBasicsRepository.count(baseSpecification);

		Specification<SRPositionBasicsEntity> approvedSpec = baseSpecification
				.and((r, q, c) -> c.equal(r.get("approved"), true));

		long approvedCount = positionBasicsRepository.count(approvedSpec);

		Specification<SRPositionBasicsEntity> rejectedSpec = baseSpecification
				.and((r, q, c) -> c.equal(r.get("rejected"), true));

		long rejectedCount = positionBasicsRepository.count(rejectedSpec);

		Specification<SRPositionBasicsEntity> pendingSpec = baseSpecification
				.and((r, q, c) -> c.equal(r.get("inProgress"), false));

		long pendingCount = positionBasicsRepository.count(pendingSpec);

		Map<String, ApprovalsChildEntity> childMap = childEntities.stream()
				.collect(Collectors.toMap(ApprovalsChildEntity::getProcessId, child -> child));

		List<SrApprovalResponse> responseList = new ArrayList<>();

		for (SRPositionBasicsEntity sRPositionBasicsEntity : srEntities) {

			String srId = sRPositionBasicsEntity.getSrId();

			log.info("SR ID : {}", srId);

			ApprovalsChildEntity childEntity = childMap.get(srId);

			if (childEntity == null) {
				continue;
			}

			SrApprovalResponse srApprovalResponse = new SrApprovalResponse();

			if (Boolean.TRUE.equals(sRPositionBasicsEntity.getApproved())) {

				srApprovalResponse.setOverAllStatus("Completed");

			} else if (Boolean.TRUE.equals(sRPositionBasicsEntity.getRejected())) {

				srApprovalResponse.setOverAllStatus("Rejected");

			} else {

				srApprovalResponse.setOverAllStatus("Pending");
			}

			int currentStageRoleId = 0;

			if (!Boolean.TRUE.equals(sRPositionBasicsEntity.getApprover1())) {

				currentStageRoleId = approvalChainEntity.getLevelConfig().get(0).getRoleId();

			} else if (Boolean.TRUE.equals(sRPositionBasicsEntity.getApprover1())
					&& !Boolean.TRUE.equals(sRPositionBasicsEntity.getApprover2())) {

				currentStageRoleId = approvalChainEntity.getLevelConfig().get(1).getRoleId();

			} else if (Boolean.TRUE.equals(sRPositionBasicsEntity.getApprover1())
					&& Boolean.TRUE.equals(sRPositionBasicsEntity.getApprover2())
					&& !Boolean.TRUE.equals(sRPositionBasicsEntity.getApprover3())) {

				currentStageRoleId = approvalChainEntity.getLevelConfig().get(2).getRoleId();
			}

			if (currentStageRoleId != 0) {

				srApprovalResponse.setCurrentStage(rolesRepository.findByRoleId(currentStageRoleId)
						.map(RolesEntity::getRoleName).orElse("Unknown"));

			} else {

				srApprovalResponse.setCurrentStage("Completed");
			}

			Integer deptId = sRPositionBasicsEntity.getDepartmentId();
			String departName = departmentsRepository.findById(deptId).get().getDepartmentName();

			srApprovalResponse.setSrId(srId);

			srApprovalResponse.setJobTitle(sRPositionBasicsEntity.getJobTitle());

			srApprovalResponse.setCreatedOn(sRPositionBasicsEntity.getCreatedOn());

			srApprovalResponse.setDepartment(departName);

			responseList.add(srApprovalResponse);
		}

		Map<String, Object> response = new HashMap<>();

		response.put("content", responseList);

		response.put("totalItems", srPage.getTotalElements());

		Map<String, Object> counts = new HashMap<>();

		counts.put("all", allCount);
		counts.put("approved", approvedCount);
		counts.put("rejected", rejectedCount);
		counts.put("pending", pendingCount);

		response.put("counts", counts);
		log.info("ApprovalServiceImpl::Exit from the assignedSrsForApprovals");
		return ApiResponse.success(ResponseCode.SUCCESS, "SR List fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getAllApprovedServiceRequests(SpecificationFilterRequest request) {

		int page = request.getPage() != null ? request.getPage() : 0;

		int size = request.getSize() != null ? request.getSize() : 10;

		String sortBy = request.getSortBy() != null ? request.getSortBy() : "dateOfApproval3";

		Sort.Direction direction = "ASC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.ASC
				: Sort.Direction.DESC;

		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

		Specification<SRPositionBasicsEntity> spec = request.buildApprovedSrSpecification();

		Page<SRPositionBasicsEntity> pageResult = positionBasicsRepository.findAll(spec, pageable);

		List<ApprovedSrResponse> responseList = pageResult.getContent().stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("approvedServiceRequests", responseList);

		response.put("currentPage", pageResult.getNumber());

		response.put("totalPages", pageResult.getTotalPages());

		response.put("totalElements", pageResult.getTotalElements());

		return new ApiResponse<>(ResponseCode.SUCCESS, "Approved Service Requests fetched successfully", response);
	}

	private ApprovedSrResponse mapToResponse(SRPositionBasicsEntity entity) {

		String departmentName = null;

		if (entity.getDepartmentId() != null) {

			departmentName = departmentsRepository.findById(entity.getDepartmentId())
					.map(DepartmentsEntity::getDepartmentName).orElse(null);
		}

		return new ApprovedSrResponse(entity.getSrId(), entity.getJobTitle(), departmentName, entity.getCreatedBy(),
				entity.getDateOfApproval3());
	}

}
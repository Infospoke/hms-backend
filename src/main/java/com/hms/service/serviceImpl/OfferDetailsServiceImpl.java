package com.hms.service.serviceImpl;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.dto.ApprovalStatusDto;
import com.hms.service.dto.NotificationEvent;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.BudgetAndCompensationEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.FunctionalityEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.OfferDetailsChildEntity;
import com.hms.service.entity.OfferDetailsEntity;
import com.hms.service.entity.OfferLetterTemplateEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.BudgetAndCompensationRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.OfferDeatilsChildRepository;
import com.hms.service.repository.OfferDetailsRepository;
import com.hms.service.repository.OfferLetterTemplateRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.ApproveOfferRequest;
import com.hms.service.request.LevelConfig;
import com.hms.service.request.ReleaseOfferRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateRaiseOfferRequest;
import com.hms.service.response.OfferCommentsResponse;
import com.hms.service.response.OfferDetailsResponse;
import com.hms.service.response.PendingApprovalsResponse;
import com.hms.service.response.RaiseOfferRequestResponse;
import com.hms.service.service.INotificationService;
import com.hms.service.service.IOfferDetailsService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OfferDetailsServiceImpl implements IOfferDetailsService {
	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private OfferDetailsRepository offerDetailsRepository;

	@Autowired
	private OfferLetterTemplateRepository offerLetterTemplateRepository;

	@Autowired
	private BudgetAndCompensationRepository budgetAndCompensationRepository;

	@Autowired
	private ApprovalChainRepository approvalChainRepository;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private OfferDeatilsChildRepository offerDeatilsChildRepository;

	@Autowired
	private AssignRolesRepository assignRolesRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private INotificationService notificationService;

	@Autowired
	private MailServiceImpl mailServiceImpl;

	@Autowired
	private MinioClient minioClient;

	@Value("${minio.bucketName}")
	private String bucketName;

	@Override
	public ApiResponse<?> getReadyToRelease(SpecificationFilterRequest request) {

		log.info("OfferServiceImpl :: Inside getReadyToRelease");

		if (request.getPage() == null || request.getSize() == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Page and Size are required"));

		}

		Sort sort = Sort.by(

				"DESC".equalsIgnoreCase(request.getDirection())

						? Sort.Direction.DESC

						: Sort.Direction.ASC,

				request.getSortBy() != null

						? request.getSortBy()

						: "dateOfApproval3"

		);

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Specification<OfferDetailsEntity> spec = request.buildReadyToReleaseSpecification();

		Page<OfferDetailsEntity> page = offerDetailsRepository.findAll(spec, pageable);

		List<Map<String, Object>> offers = page.getContent().stream().map(this::convertToMap).toList();

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("offers", offers);

		response.put("currentPage", page.getNumber());

		response.put("totalPages", page.getTotalPages());

		response.put("totalElements", page.getTotalElements());

		log.info("OfferServiceImpl :: Exit getReadyToRelease");

		return ApiResponse.success(

				ResponseCode.SUCCESS,

				"Ready To Release Offer letters list fetched successfully",

				response

		);

	}

	private Map<String, Object> convertToMap(OfferDetailsEntity offer) {

		JobApplicationEntity application = offer.getJobApplication();

		CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

		DepartmentsEntity department = departmentsRepository.findById(job.getDepartmentId()).orElse(null);

		Map<String, Object> map = new LinkedHashMap<>();

		map.put("offerId", offer.getId());

		map.put("applicationId", application.getId());

		map.put("candidateName", application.getFirstName() + " " + application.getLastName());

		map.put("email", application.getEmail());

		map.put("jobTitle", job.getJobTitle());

		map.put("department", department != null ? department.getDepartmentName() : "");

		map.put("recruiterName", offer.getRecruitedBy());

		map.put("finalApprovalTime", offer.getDateOfApproval3());

		map.put("priority", calculatePriority(offer.getDateOfApproval3()));

		map.put("totalCtc", offer.getTotalCtc());

		return map;

	}

	private String calculatePriority(LocalDateTime dateOfApproval3) {

		long days = ChronoUnit.DAYS.between(dateOfApproval3.toLocalDate(), LocalDate.now());

		if (days >= 5) {
			return "High";
		}

		if (days >= 3) {
			return "Medium";
		}

		return "Low";
	}

	@Override
	public ApiResponse<?> getOfferDetailsByApplicantId(Integer applicantId) {
		log.info("OfferDetailsServiceImpl ::Inside the getOfferDetailsByApplicantId");
		try {

			Optional<JobApplicationEntity> applicantOptional = jobApplicationRepository.findById(applicantId);

			if (applicantOptional.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Applicant not found");
			}

			JobApplicationEntity applicant = applicantOptional.get();

			OfferDetailsResponse response = new OfferDetailsResponse();

			// Applicant Details
			response.setApplicantId(applicant.getId());
			response.setCandidateName(applicant.getFirstName());
			response.setEmail(applicant.getEmail());

			// Job Details
			CreateJobDetailsEntity jobDetails = createJobDetailsRepository.findByJobId(applicant.getJobId());

			response.setJobTitle(jobDetails.getJobTitle());
			response.setEmploymentType(jobDetails.getEmploymentType());
			response.setWorkLocation(jobDetails.getLocation());
			// Department
			Optional<DepartmentsEntity> departmentOptional = departmentsRepository
					.findById(jobDetails.getDepartmentId());

			if (departmentOptional.isPresent()) {
				response.setDepartment(departmentOptional.get().getDepartmentName());
			}

			// Budget & Compensation
			Optional<BudgetAndCompensationEntity> budgetAndCompensationEntity = budgetAndCompensationRepository
					.findBySrId(jobDetails.getSrId());

			if (budgetAndCompensationEntity.isPresent()) {

				BudgetAndCompensationEntity budget = budgetAndCompensationEntity.get();

				response.setBasicSalary(budget.getProposedTotalCompensation());

				response.setSigningBonus(
						Boolean.TRUE.equals(budget.getSigningBonus()) ? budget.getSigningBonusAmount() : 0);

				response.setAnnualRsuEsopValue(Boolean.TRUE.equals(budget.getEquity()) ? budget.getEquityAmount() : 0);

				response.setOtherBenefits(
						Boolean.TRUE.equals(budget.getRelocationBudget()) ? budget.getRelocationBudgetAmount() : 0);

				response.setTotalCtc(budget.getProposedTotalCompensation());

				response.setOfferedCtc(budget.getAnnualHiringCost());
				response.setMinSalary(budget.getMinimumSalary());
				response.setMaxSalary(budget.getMaximumSalary());
			}

			// Offer Details
			Optional<OfferDetailsEntity> offerOptional = offerDetailsRepository.findByJobApplicationId(applicantId);

			if (offerOptional.isPresent()) {

				OfferDetailsEntity offer = offerOptional.get();

				response.setRecruiter(offer.getRecruitedBy());
				response.setRequestedOn(
						offer.getInterviewCompletionDate() != null ? offer.getInterviewCompletionDate().toLocalDate()
								: null);

				response.setProbationPeriod(offer.getProbationPeriod());
				response.setNoticePeriod(offer.getNoticePeriod());

			}
			log.info("OfferDetailsServiceImpl ::Exit from the getOfferDetailsByApplicantId");
			return ApiResponse.success(ResponseCode.SUCCESS, "Offer details fetched successfully", response);

		} catch (Exception e) {
			log.error("OfferDetailsServiceImpl :: Error while fetching offer details", e);
			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}

	@Override
	public ApiResponse<?> getOfferComments(Integer applicantId) {
		log.info("OfferDetailsServiceImpl ::Inside the getOfferComments");
		try {

			Optional<OfferDetailsEntity> offerDetailsEntity = offerDetailsRepository
					.findByJobApplicationId(applicantId);

			Optional<OfferDetailsChildEntity> offerDetailsChildEntity = offerDeatilsChildRepository
					.findByJobApplication_Id(applicantId);

			if (offerDetailsEntity == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Offer details not found");
			}
			OfferDetailsEntity offerDetails = offerDetailsEntity.get();
			List<OfferCommentsResponse> responseList = new ArrayList<>();

			OfferDetailsChildEntity childEntity = offerDetailsChildEntity.get();

			List<Integer> roleIds = Arrays.asList(childEntity.getRole1(), childEntity.getRole2(),
					childEntity.getRole3());

			List<Object[]> roles = rolesRepository.findRoleNamesByIds(roleIds);

			Map<Integer, String> roleMap = roles.stream()
					.collect(Collectors.toMap(r -> (Integer) r[0], r -> (String) r[1]));
			// Approver 1

			OfferCommentsResponse response = new OfferCommentsResponse();
			response.setApproverSequence("1");
			response.setRole(roleMap.get(childEntity.getRole1()));
			response.setApproverName(offerDetails.getApprover1By());
			response.setApproved(offerDetails.getApprover1());
			response.setApprovedOn(offerDetails.getDateOfApproval1());
			response.setComments(offerDetails.getApprover1Comments());

			responseList.add(response);

			// Approver 2

			response = new OfferCommentsResponse();
			response.setApproverSequence("2");
			response.setRole(offerDetails.getApprover2Role());
			response.setApproverName(roleMap.get(childEntity.getRole1()));
			response.setApproved(offerDetails.getApprover2());
			response.setApprovedOn(offerDetails.getDateOfApproval2());
			response.setComments(offerDetails.getApprover2Comments());

			responseList.add(response);

			// Approver 3

			response = new OfferCommentsResponse();
			response.setApproverSequence("3");
			response.setRole(offerDetails.getApprover3Role());
			response.setApproverName(roleMap.get(childEntity.getRole1()));
			response.setApproved(offerDetails.getApprover3());
			response.setApprovedOn(offerDetails.getDateOfApproval3());
			response.setComments(offerDetails.getApprover3Comments());

			responseList.add(response);

			log.info("OfferDetailsServiceImpl ::Exit from the getOfferComments");

			return ApiResponse.success(ResponseCode.SUCCESS, "Offer comments fetched successfully", responseList);

		} catch (Exception e) {
			log.error("OfferDetailsServiceImpl :: getOfferComments", e);
			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}

	@Transactional
	@Override
	public ApiResponse<?> approveOffer(ApproveOfferRequest request) {

		NotificationEvent event = new NotificationEvent();

		Optional<OfferDetailsChildEntity> optional = offerDeatilsChildRepository
				.findByJobApplication_Id(request.getApplicantId());

		if (optional.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "No approval record found",
					List.of("Invalid Applicant Id"));
		}

		OfferDetailsChildEntity entity = optional.get();

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

		Integer expectedRole;

		switch (currentLevel) {

		case 1:
			expectedRole = entity.getRole1();
			break;

		case 2:
			expectedRole = entity.getRole2();
			break;

		case 3:
			expectedRole = entity.getRole3();
			break;

		default:
			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid approval level",
					List.of("Unable to determine approval level"));
		}

		String expectedRoleName = rolesRepository.findByRoleId(expectedRole)
				.orElseThrow(() -> new RuntimeException("Role not found")).getRoleName();

		if (!roleName.equalsIgnoreCase(expectedRoleName)) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Unauthorized",
					List.of("You are not authorized to approve this level"));
		}

		Integer submittedBy = entity.getOfferSubmittedBy();

		if (submittedBy != null) {

			Optional<UserEntity> makerOpt = userRepository.findByUserId(submittedBy);

			if (makerOpt.isPresent() && username.equalsIgnoreCase(makerOpt.get().getUsername())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Access Denied",
						List.of("You created this Offer, so you cannot approve it"));
			}
		}

		Optional<OfferDetailsEntity> posOpt = offerDetailsRepository.findByJobApplication_Id(request.getApplicantId());

		if (posOpt.isEmpty()) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Offer not found", List.of("Invalid Applicant Id"));
		}

		OfferDetailsEntity pos = posOpt.get();

		String applicantId = pos.getJobApplication().getId().toString();

		Long userId = null;
		String makerRoleName = null;
		Integer makerRoleId = null;

		Integer submitteBy = entity.getOfferSubmittedBy();

		if (submittedBy != null) {

			Optional<UserEntity> maker = userRepository.findByUserId(submittedBy);
			UserEntity userEntity = maker.get();

			if (userEntity == null) {
				throw new RuntimeException("Maker not found");
			}

			userId = userEntity.getUserId().longValue();
			Integer roleId = assignRolesRepository.findByUserId(submitteBy).get().getRoleId();

			Optional<RolesEntity> role = rolesRepository.findByRoleId(roleId);

			RolesEntity roles = role.get();
			if (roles == null) {
				throw new RuntimeException("Maker Role not found");
			}

			makerRoleName = roles.getRoleName();
		}

		int approvalLevel;

		if (!Boolean.TRUE.equals(pos.getApprover1())) {

			approvalLevel = 1;

		} else if (!Boolean.TRUE.equals(pos.getApprover2())) {

			approvalLevel = 2;

		} else if (!Boolean.TRUE.equals(pos.getApprover3())) {

			approvalLevel = 3;

		} else {

			return ApiResponse.success("All approvals already completed");
		}

		boolean approved = Boolean.TRUE.equals(request.getApprove());

//		if (approvalLevel == 3 && approved) {
//
//			if (request.getESignature() == null || request.getESignature().trim().isEmpty()) {
//
//				return ApiResponse.failure(ResponseCode.FAILURE, "Approval Failed",
//						List.of("HR Head e-signature is mandatory for Level 3 approval."));
//			}
//		}

		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

		String levelName = "";
		String approverName = "";
		LocalDateTime approvedDate = null;
		Object approvalStatus = null;

		switch (approvalLevel) {

		case 1:

			levelName = roleName;

			pos.setApprover1By(username);
			pos.setApprover1Role(roleName);
			pos.setDateOfApproval1(now);
			pos.setApprover1Comments(request.getComments());

			approverName = pos.getApprover1By();
			approvedDate = pos.getDateOfApproval1();

			if (approved) {

				pos.setApprover1(true);

				approvalStatus = pos.getApprover1();

				// Activate next approver
				entity.setApprover2(true);
			}

			break;

		case 2:

			levelName = roleName;

			pos.setApprover2By(username);
			pos.setApprover2Role(roleName);
			pos.setDateOfApproval2(now);
			pos.setApprover2Comments(request.getComments());

			approverName = pos.getApprover2By();
			approvedDate = pos.getDateOfApproval2();

			if (approved) {

				pos.setApprover2(true);

				approvalStatus = pos.getApprover2();

				// Activate next approver
				entity.setApprover3(true);
			}

			break;

		case 3:

			levelName = roleName;

			pos.setApprover3By(username);
			pos.setApprover3Role(roleName);
			pos.setDateOfApproval3(now);
			pos.setApprover3Comments(request.getComments());

			approverName = pos.getApprover3By();
			approvedDate = pos.getDateOfApproval3();

			if (approved) {

				pos.setApprover3(true);

				approvalStatus = pos.getApprover3();
			}

			break;

		default:

			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid Approval Level",
					List.of("Unable to process approval"));
		}

		if (approved) {

			pos.setReject(false);

		} else {

			pos.setReject(true);
			pos.setInProgress(true);
		}

		offerDetailsRepository.save(pos);
		offerDeatilsChildRepository.save(entity);

		Map<Integer, List<String>> roleEmailMap = processApprovalChain(request.getApplicantId());

		Integer roleId = null;

		for (Map.Entry<Integer, List<String>> entry : roleEmailMap.entrySet()) {

			roleId = entry.getKey();
		}

		String checkerRoleName = rolesRepository.findByRoleId(roleId).get().getRoleName();

		event.setProcessId(pos.getId().toString());
		event.setType("SR");
		event.setCheckerRoleName(checkerRoleName);
		event.setRoleEmailMap(roleEmailMap);

		if (approved) {

			event.setCheckerNotificationTitle("Level " + approvalLevel + " Approved — " + levelName);

			event.setCheckerMessage("A offer is now under your approval flow for review and approval");

			String makerSubject = "";
			String makerTitle = "";
			String makerMailBody = "";

			switch (approvalLevel) {

			case 1:

				makerSubject = "Your offer has been approved by Level 1 (Finance Analyst) and is now under Level 2 approval flow";

				makerTitle = "Level 1 Approved — " + roleName;

				makerMailBody = "hgertyuiuoiuy";

				break;

			case 2:

				makerSubject = "Your offer has been approved by Level 2 (Finance Head) and is now under Level 3 approval flow";

				makerTitle = "Level 2 Approved — " + roleName;

				makerMailBody = "sadfegfrdhyjgkui";

				break;

			case 3:

				makerSubject = "Your offer has been fully approved successfully and is now ready to release";

				makerTitle = "Level 3 Approved — " + roleName;

				makerMailBody = "ttretyuio";

				break;
			}

			sendMakerMail(applicantId, userId, makerRoleId, makerSubject, makerRoleName, makerTitle, makerMailBody,
					event);

			return ApiResponse.success("Approved successfully at level " + approvalLevel);
		}

		String rejectedMailBody = "gfdhkjsl;jf";

		event.setCheckerNotificationTitle("Level " + approvalLevel + " Rejected — " + levelName);

		event.setCheckerMessage("A Offer has been rejected in the approval flow.");

		event.setCheckerEmailBody(rejectedMailBody);

		sendMakerMail(applicantId, userId, makerRoleId,
				"Your Offer  has been rejected by Level " + approvalLevel + " (" + levelName + ")", makerRoleName,
				"OFFER Rejected", rejectedMailBody, event);

		return ApiResponse.success("Rejected successfully at level " + approvalLevel);
	}

	private Map<Integer, List<String>> processApprovalChain(Integer applicantId) {

		Map<Integer, List<String>> roleEmailMap = new HashMap<>();

		FunctionalityEntity functionality = functionalityRepository.findByFunctionalityName(Constants.OFFER_PLAN)
				.orElseThrow(() -> new RuntimeException("Functionality not found"));

		Integer functionalityId = functionality.getId();

		log.info("Functionality Id : {}", functionalityId);

		ApprovalChainEntity approvalChainEntity = approvalChainRepository.findByFunctionality(functionalityId);

		// CHECK CHAIN EXISTS
		if (approvalChainEntity == null) {

			throw new RuntimeException("Approval chain not configured");

		}

		List<LevelConfig> levels = approvalChainEntity.getLevelConfig();

		// sort levels
		levels.sort(Comparator.comparing(LevelConfig::getLevel));

		Optional<OfferDetailsChildEntity> optionalChild = offerDeatilsChildRepository
				.findByJobApplication_Id(applicantId);

		OfferDetailsChildEntity childEntity;

		if (optionalChild.isPresent()) {

			childEntity = optionalChild.get();

		} else {

			childEntity = new OfferDetailsChildEntity();

			// initially first approver enabled
			childEntity.setApprover1(true);
		}

		// set roles
		for (LevelConfig lvl : levels) {

			Integer roleId = lvl.getRoleId();

			if (lvl.getLevel() == 1) {

				childEntity.setRole1(roleId);

			} else if (lvl.getLevel() == 2) {

				childEntity.setRole2(roleId);

			} else if (lvl.getLevel() == 3) {

				childEntity.setRole3(roleId);
			}
		}

		Optional<OfferDetailsEntity> offerOptional = offerDetailsRepository.findByJobApplication_Id(applicantId);

		if (offerOptional.isPresent()) {

			OfferDetailsEntity offerEntity = offerOptional.get();

			childEntity.setOfferSubmittedBy(offerEntity.getSubmittedByUserId());
		}

		offerDeatilsChildRepository.save(childEntity);

		// RETURN EMAILS BASED ON APPROVER FLAG

		Integer roleId = null;

		// approver1 true send role1 mails
		if (Boolean.TRUE.equals(childEntity.getApprover1()) && !Boolean.TRUE.equals(childEntity.getApprover2())) {

			roleId = childEntity.getRole1();

			log.info("Sending mails for Role1");

		}

		// approver2 true send role2 mails
		else if (Boolean.TRUE.equals(childEntity.getApprover2()) && !Boolean.TRUE.equals(childEntity.getApprover3())) {

			roleId = childEntity.getRole2();

			log.info("Sending mails for Role2");

		}

		// approver3 true send role3 mails
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

	private Long getUserIdFromToken() {

		String authHeader = httpServletRequest.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			return jwtService.extractUserId(token);

		} else {

			throw new RuntimeException("Invalid or missing Authorization header");
		}
	}

	private void sendMakerMail(String srId, Long userId, Integer makerRoleId, String makerMessage, String makerRoleName,
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

	public ApiResponse<?> getAllRaiseOfferRequests(SpecificationFilterRequest request) {

		log.info("OfferDetailsServiceImpl :: Inside getAllRaiseOfferRequests");

		Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Specification<OfferDetailsEntity> specification = request.buildRaiseOfferRequestSpecification();

		Page<OfferDetailsEntity> offerPage = offerDetailsRepository.findAll(specification, pageable);

		List<RaiseOfferRequestResponse> responseList = new ArrayList<>();

		for (OfferDetailsEntity offer : offerPage.getContent()) {

			JobApplicationEntity application = offer.getJobApplication();

			CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

			DepartmentsEntity department = null;

			if (job != null) {

				department = departmentsRepository.findById(job.getDepartmentId()).orElse(null);
			}

			RaiseOfferRequestResponse response = new RaiseOfferRequestResponse();

			response.setOfferId(offer.getId());

			response.setApplicantId(application.getId());

			response.setCandidateName(application.getFirstName() + " " + application.getLastName());

			response.setCandidateEmail(application.getEmail());

			response.setPhoneNumber(application.getPhNo());

			if (job != null) {

				response.setJobId(job.getJobId());

				response.setJobTitle(job.getJobTitle());

			}
			if (department != null) {

				response.setDepartmentName(department.getDepartmentName());

			}
			response.setMovedToHireOn(offer.getInterviewCompletionDate());

			response.setRecruiter(offer.getRecruitedBy());

			response.setPriority(calculatedOfferRaiseRequestPriority(offer.getInterviewCompletionDate()));

			responseList.add(response);

		}

		Map<String, Object> response = new HashMap<>();

		response.put("content", responseList);

		response.put("page", offerPage.getNumber());

		response.put("size", offerPage.getSize());

		response.put("totalElements", offerPage.getTotalElements());

		response.put("totalPages", offerPage.getTotalPages());

		response.put("last", offerPage.isLast());

		log.info("OfferDetailsServiceImpl :: Exit getAllRaiseOfferRequests");

		return ApiResponse.success(ResponseCode.SUCCESS, "Raise Offer Requests fetched successfully", response);
	}

	private String calculatedOfferRaiseRequestPriority(LocalDateTime interviewCompletionDate) {

		long days = ChronoUnit.DAYS.between(interviewCompletionDate.toLocalDate(), LocalDate.now());

		if (days >= 3) {
			return "High";
		}

		if (days >= 2) {
			return "Medium";
		}

		return "Low";
	}

	@Override
	public ApiResponse<?> getOfferDashboardCounts() {

		log.info("OfferDetailsServiceImpl :: getOfferDashboardCounts");

		Long raiseOfferRequest = offerDetailsRepository.countBySubmitFinancialApprovalFalse();

		Long pendingApprovals = offerDetailsRepository
				.countBySubmitFinancialApprovalTrueAndApproveFalseAndRejectFalse();

		Long readyToRelease = offerDetailsRepository.countByApproveTrueAndOfferReleasedFalse();

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("raiseOfferRequest", raiseOfferRequest);
		response.put("pendingApprovals", pendingApprovals);
		response.put("readyToRelease", readyToRelease);
		response.put("releaseOfferLetter", pendingApprovals + readyToRelease);

		return ApiResponse.success(ResponseCode.SUCCESS, "Offer dashboard counts fetched successfully", response);
	}

	@Override
	public ApiResponse<?> submitFinancialApproval(UpdateRaiseOfferRequest request) {

		log.info("OfferDetailsServiceImpl :: Inside UpdateRaiseOffer");

		String authHeader = httpServletRequest.getHeader("Authorization");

		Integer userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token).intValue();
		}

		JobApplicationEntity application = jobApplicationRepository.findById(request.getApplicantId()).orElse(null);

		if (application == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Applicant Not Found");
		}

		OfferDetailsEntity offerDetails = offerDetailsRepository.findByJobApplication(application).orElse(null);

		if (offerDetails == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Offer Details Not Found");
		}

		AssignRolesEntity assignRole = assignRolesRepository.findByUserId(userId).orElse(null);

		if (assignRole == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Assigned Role Not Found");
		}

		OfferLetterTemplateEntity template = offerLetterTemplateRepository.findById(request.getOfferLetterTemplateId())
				.orElse(null);

		if (template == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Offer Letter Template Not Found");
		}

		offerDetails.setTotalCtc(request.getTotalCtc());

		offerDetails.setNoticePeriod(request.getNoticePeriod());

		offerDetails.setProbationPeriod(request.getProbationPeriod());

		offerDetails.setOfferLetterTemplate(template);

		offerDetails.setCompensation(request.getCompensation());

		offerDetails.setSubmitFinancialApproval(request.getSubmitFinancialApproval());

		offerDetails.setCreatedDate(LocalDateTime.now());

		offerDetails.setSubmittedByUserId(userId);

		offerDetails.setCreatedByRoleId(assignRole.getRoleId());

		offerDetailsRepository.save(offerDetails);

		log.info("OfferDetailsServiceImpl :: Exit UpdateRaiseOffer");
		processApprovalChain(offerDetails.getJobApplication().getId());

		return ApiResponse.success(ResponseCode.SUCCESS, "Raise Offer Request Updated Successfully", null);
	}

	@Override
	public void viewOfferLetter(Integer appId, String action, HttpServletResponse response) {

		log.info("Inside viewOfferLetter");

		JobApplicationEntity application = jobApplicationRepository.findById(appId)
				.orElseThrow(() -> new RuntimeException("Application not found"));

		String candidateName = application.getFirstName() + "_" + application.getLastName();

		String objectKey = "offer-letters/" + appId + "/" + candidateName + "_Offer_Letter.pdf";

		try (InputStream inputStream = minioClient.getObject(
				GetObjectArgs.builder().bucket("infospokejobapplicationsbucket").object(objectKey).build())) {

			response.setContentType("application/pdf");

			response.setHeader("Content-Disposition", ("view".equalsIgnoreCase(action) ? "inline" : "attachment")
					+ "; filename=\"" + candidateName + "_Offer_Letter.pdf\"");

			IOUtils.copy(inputStream, response.getOutputStream());

			response.flushBuffer();

		} catch (Exception e) {
			log.error("Error while viewing offer letter", e);
			throw new RuntimeException("Unable to fetch offer letter from MinIO");
		}
	}


	@Override
	public ApiResponse<?> getPendingApprovals(SpecificationFilterRequest request) {

		log.info("OfferDetailsServiceImpl :: getPendingApprovals");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

		Specification<OfferDetailsEntity> specification = request.buildOfferApprovalSpecification();

		Page<OfferDetailsEntity> offerPage = offerDetailsRepository.findAll(specification, pageable);

		if (offerPage.isEmpty()) {

			Map<String, Object> response = new HashMap<>();

			response.put("pendingApprovals", Collections.emptyList());
			response.put("currentPage", offerPage.getNumber());
			response.put("totalPages", offerPage.getTotalPages());
			response.put("totalElements", offerPage.getTotalElements());

			return ApiResponse.success(ResponseCode.SUCCESS, "No pending approvals found", response);
		}

		List<OfferDetailsEntity> offers = offerPage.getContent();

		List<Integer> applicationIds = offers.stream().map(offer -> offer.getJobApplication().getId()).distinct()
				.toList();

		List<JobApplicationEntity> applications = jobApplicationRepository.findByIdIn(applicationIds);

		Map<Integer, JobApplicationEntity> applicationMap = applications.stream()
				.collect(Collectors.toMap(JobApplicationEntity::getId, Function.identity()));

		List<Integer> jobIds = applications.stream().map(JobApplicationEntity::getJobId).distinct().toList();

		List<CreateJobDetailsEntity> jobs = createJobDetailsRepository.findByJobIdIn(jobIds);

		Map<Integer, CreateJobDetailsEntity> jobMap = jobs.stream()
				.collect(Collectors.toMap(CreateJobDetailsEntity::getJobId, Function.identity()));

		List<Integer> departmentIds = jobs.stream().map(CreateJobDetailsEntity::getDepartmentId).distinct().toList();

		List<DepartmentsEntity> departments = departmentsRepository.findByIdIn(departmentIds);

		Map<Integer, DepartmentsEntity> departmentMap = departments.stream()
				.collect(Collectors.toMap(DepartmentsEntity::getId, Function.identity()));

		List<Integer> offerIds = offers.stream().map(OfferDetailsEntity::getId).toList();

		List<OfferDetailsChildEntity> childEntities = offerDeatilsChildRepository.findByOffer_IdIn(offerIds);

		Map<Integer, OfferDetailsChildEntity> childMap = childEntities.stream()
				.collect(Collectors.toMap(child -> child.getOffer().getId(), Function.identity()));

		List<PendingApprovalsResponse> responseList = new ArrayList<>();
		for (OfferDetailsEntity offer : offers) {

			PendingApprovalsResponse response = new PendingApprovalsResponse();

			response.setOfferId(offer.getId());

			JobApplicationEntity application = applicationMap.get(offer.getJobApplication().getId());

			if (application != null) {

				response.setApplicationId(application.getId());

				response.setApplicantName((application.getFirstName() == null ? "" : application.getFirstName()) + " "
						+ (application.getLastName() == null ? "" : application.getLastName()));

				response.setApplicantEmail(application.getEmail());

				CreateJobDetailsEntity job = jobMap.get(application.getJobId());

				if (job != null) {

					response.setJobTitle(job.getJobTitle());

					DepartmentsEntity department = departmentMap.get(job.getDepartmentId());

					if (department != null) {
						response.setDepartment(department.getDepartmentName());
					}
				}
			}

			response.setRequestedOn(offer.getCreatedDate());

			response.setPriority(calculatePendingApprovalPriority(offer.getCreatedDate()));

			OfferDetailsChildEntity child = childMap.get(offer.getId());

			List<ApprovalStatusDto> approvals = new ArrayList<>();

			if (child != null) {

				approvals.add(
						new ApprovalStatusDto(offer.getApprover1Role(), Boolean.TRUE.equals(child.getApprover1())));

				approvals.add(
						new ApprovalStatusDto(offer.getApprover2Role(), Boolean.TRUE.equals(child.getApprover2())));

				approvals.add(
						new ApprovalStatusDto(offer.getApprover3Role(), Boolean.TRUE.equals(child.getApprover3())));
			}

			response.setApprovals(approvals);

			responseList.add(response);
		}

		Map<String, Object> response = new HashMap<>();

		response.put("pendingApprovals", responseList);
		response.put("currentPage", offerPage.getNumber());
		response.put("totalPages", offerPage.getTotalPages());
		response.put("totalElements", offerPage.getTotalElements());

		log.info("OfferDetailsServiceImpl :: Exit from getPendingApprovals");

		return ApiResponse.success(ResponseCode.SUCCESS, "Pending approvals fetched successfully", response);
	}

	private String calculatePendingApprovalPriority(LocalDateTime createdDate) {

		LocalDate dueDate = createdDate.toLocalDate().plusDays(7);

		long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

		if (remainingDays > 3) {
			return "Low";
		}

		if (remainingDays >= 1) {
			return "Medium";
		}

		return "High";
	}

	@Override
	@Transactional
	public ApiResponse<?> releaseOfferLetters(ReleaseOfferRequest request) {

		log.info("OfferDetailsServiceImpl :: releaseOfferLetters");

		String authHeader = httpServletRequest.getHeader("Authorization");

		Long userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			userId = jwtService.extractUserId(token);
		}

		if (request.getApplicationIds() == null || request.getApplicationIds().isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure",
					Collections.singletonList("Application Ids are required"));
		}

		List<OfferDetailsEntity> offers = offerDetailsRepository.findByJobApplication_IdIn(request.getApplicationIds());

		if (offers.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", Collections.singletonList("No offers found"));
		}

		List<String> releasedCandidateNames = new ArrayList<>();

		for (OfferDetailsEntity offer : offers) {

			JobApplicationEntity application = offer.getJobApplication();

			releasedCandidateNames.add(application.getFirstName() + " " + application.getLastName());

			offer.setOfferReleased(true);
			offer.setOfferReleasedBy(userId);
			offer.setOfferReleasedAt(LocalDateTime.now());

			try {

				String candidateName = application.getFirstName().trim() + "_" + application.getLastName().trim();

				String objectName = "offer-letters/" + application.getId() + "/" + candidateName + "_Offer_Letter.pdf";

				log.info("Bucket Name : {}", bucketName.trim());
				log.info("Object Name : {}", objectName);

				InputStream inputStream = minioClient
						.getObject(GetObjectArgs.builder().bucket(bucketName.trim()).object(objectName).build());

				byte[] pdfBytes = inputStream.readAllBytes();
				inputStream.close();

				CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

				String body = String.format(Constants.OFFER_LETTER_MAIL_BODY, application.getFirstName(),
						job.getJobTitle());

				mailServiceImpl.sendMailWithAttachment(Constants.NOREPLY_INDIA, application.getEmail(), null,
						"Offer Letter", body, pdfBytes, candidateName + "_Offer_Letter.pdf");

				log.info("Offer letter mail sent successfully to {}", application.getEmail());

			} catch (Exception e) {

				log.error("Unable to send offer letter for application {}", application.getId(), e);

			}
		}

		// Save all released offers
		offerDetailsRepository.saveAll(offers);

		// Send Notification
		OfferDetailsEntity firstOffer = offers.get(0);

		NotificationEvent event = new NotificationEvent();

		event.setProcessId("OFFER_RELEASE_" + System.currentTimeMillis());

		event.setMakerRoleId(firstOffer.getCreatedByRoleId());

		event.setMakerRoleName(
				rolesRepository.findById(firstOffer.getCreatedByRoleId()).map(RolesEntity::getRoleName).orElse(null));

		event.setMakerNotificationTitle("Offer Letter Released Successfully");

		event.setMakerMessage("Offer letter(s) released successfully.");

		RolesEntity checkerRole = rolesRepository.findByRoleNameIgnoreCase(firstOffer.getApprover3Role());

		if (checkerRole != null) {

			event.setCheckerId(checkerRole.getRoleId());

			event.setCheckerRoleName(checkerRole.getRoleName());
		}

		event.setCheckerNotificationTitle("Offer Letter Released");

		event.setCheckerMessage(
				"Offer letter(s) have been released for candidate(s): " + String.join(", ", releasedCandidateNames));

		Map<Integer, List<String>> roleEmailMap = new HashMap<>();

		roleEmailMap.put(firstOffer.getCreatedByRoleId(), Collections.emptyList());

		if (checkerRole != null) {
			roleEmailMap.put(checkerRole.getRoleId(), Collections.emptyList());
		}

		event.setRoleEmailMap(roleEmailMap);

		notificationService.callNotification(event);
		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Offer letters released successfully");
	}

}
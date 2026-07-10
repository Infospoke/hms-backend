package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.Comparator;
import java.util.HashMap;

import java.util.Objects;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.entity.BudgetAndCompensationEntity;

import com.hms.service.constants.Constants;
import com.hms.service.dto.NotificationEvent;
import com.hms.service.entity.ApprovalChainEntity;

import com.hms.service.entity.AssignRolesEntity;

import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.FunctionalityEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.OfferDetailsChildEntity;
import com.hms.service.entity.OfferDetailsEntity;

import com.hms.service.repository.BudgetAndCompensationRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.JobApplicationRepository;

import com.hms.service.entity.UserEntity;

import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.AssignRolesRepository;

import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.repository.OfferDeatilsChildRepository;

import com.hms.service.repository.OfferDetailsRepository;

import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.ApproveOfferRequest;
import com.hms.service.request.LevelConfig;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateRaiseOfferRequest;
import com.hms.service.response.OfferCommentsResponse;
import com.hms.service.response.OfferDetailsResponse;

import com.hms.service.service.INotificationService;

import com.hms.service.request.ReleaseOfferRequest;

import com.hms.service.response.RaiseOfferRequestResponse;

import com.hms.service.service.IOfferDetailsService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;

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
			Optional<DepartmentsEntity> departmentOptional = departmentsRepository.findById(jobDetails.getDepartmentId());

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

	        Optional<OfferDetailsEntity> offerDetailsEntity = offerDetailsRepository.findByJobApplicationId(applicantId);

	        if (offerDetailsEntity == null) {
	            return ApiResponse.failure(ResponseCode.FAILURE, "Offer details not found");
	        }
	        OfferDetailsEntity offer =offerDetailsEntity.get();
	        List<OfferCommentsResponse> responseList = new ArrayList<>();

	        // Approver 1
	        if (offer.getApprover1By() != null) {

	            OfferCommentsResponse response = new OfferCommentsResponse();
	            response.setRole(offer.getApprover1Role());
	            response.setApproverName(offer.getApprover1By());
	            response.setApproved(offer.getApprover1());
	            response.setApprovedOn(offer.getFinalApprovalTime());
	            response.setComments(offer.getApprover1Comments());

	            responseList.add(response);
	        }

	        // Approver 2
	        if (offer.getApprover2By() != null) {

	            OfferCommentsResponse response = new OfferCommentsResponse();
	            response.setRole(offer.getApprover2Role());
	            response.setApproverName(offer.getApprover2By());
	            response.setApproved(offer.getApprover2());
	            response.setApprovedOn(offer.getFinalApprovalTime());
	            response.setComments(offer.getApprover2Comments());

	            responseList.add(response);
	        }

	        // Approver 3
	        if (offer.getApprover3By() != null) {

	            OfferCommentsResponse response = new OfferCommentsResponse();
	            response.setRole(offer.getApprover3Role());
	            response.setApproverName(offer.getApprover3By());
	            response.setApproved(offer.getApprover3());
	            response.setApprovedOn(offer.getFinalApprovalTime());
	            response.setComments(offer.getApprover3Comments());

	            responseList.add(response);
	        }
	        log.info("OfferDetailsServiceImpl ::Exit from the getOfferComments");

	        return ApiResponse.success(ResponseCode.SUCCESS,
	                "Offer comments fetched successfully", responseList);

	    } catch (Exception e) {
	        log.error("OfferDetailsServiceImpl :: getOfferComments", e);
	        return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
	    }
	}


	@Override

	public ApiResponse<?> approveOffer(ApproveOfferRequest request) {
		Optional<OfferDetailsChildEntity> optional = offerDeatilsChildRepository
				.findByJobApplication_Id(request.getApplicantId());

		NotificationEvent event = new NotificationEvent();

		if (optional.isEmpty()) {

			return ApiResponse.failure(ResponseCode.FAILURE, "No approval record found",
					List.of("Invalid Applicant Id"));
		}

		OfferDetailsChildEntity entity = optional.get();

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

		// ROLE VALIDATION

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

		Integer submittedBy = entity.getOfferSubmittedBy();

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

		// FETCH SR

		Optional<OfferDetailsEntity> posOpt = offerDetailsRepository.findByJobApplication_Id(request.getApplicantId());

		if (posOpt.isEmpty()) {

			return ApiResponse.failure(ResponseCode.FAILURE, "SR not found", List.of("Invalid SR Id"));
		}

		OfferDetailsEntity pos = posOpt.get();

		Integer applicantId = pos.getJobApplication().getId();
		Long userId = null;
		String makerRoleName = null;
		Integer makerRoleId = null;

		// FIND APPROVAL LEVEL

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

		boolean approved = Boolean.TRUE.equals(request.getApprove());

		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

		// COMMON VARIABLES

		String levelName = "";
		String approverName = "";
		LocalDateTime approvedDate = null;
		Object approvalStatus = null;

		// LEVEL BASED DATA

		if (approvalLevel == 1) {

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

				entity.setApprover2(true);
			}

		} else if (approvalLevel == 2) {

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

				entity.setApprover3(true);
			}

		} else if (approvalLevel == 3) {

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

				pos.setApprove(true);
				pos.setInProgress(true);
			}
		}

		// COMMON SAVE

		if (approved) {

			pos.setReject(false);

		} else {

			pos.setReject(true);
			pos.setInProgress(true);
		}

		offerDetailsRepository.save(pos);
		offerDeatilsChildRepository.save(entity);

		// COMMON MAIL DATA

		Map<Integer, List<String>> roleEmailMap = processApprovalChain(request.getApplicantId());
		Integer roleId = null;
		for (Map.Entry<Integer, List<String>> entry : roleEmailMap.entrySet()) {
			roleId = entry.getKey();
		}

		String checkerRoleName = rolesRepository.findByRoleId(roleId).get().getRoleName();
//		Integer deptId = pos.getDepartmentId();

		// String deptName =
		// departmentsRepository.findById(deptId).get().getDepartmentName();

		event.setProcessId(pos.getId().toString());
//		event.setDeptName(deptName);
		event.setType("SR");
		event.setCheckerRoleName(checkerRoleName);
		event.setRoleEmailMap(roleEmailMap);

		// APPROVED FLOW

		if (approved) {

			event.setCheckerNotificationTitle("Level " + approvalLevel + " Approved — " + levelName);

			event.setCheckerMessage("A Staffing Requisition is now under your approval flow for review and approval");

//			event.setCheckerEmailBody(String.format(Constants.SR_TO_BE_APPROVED_MAIL_BODY, pos.getSrId(),
//					pos.getJobTitle(), deptName, pos.getCreatedBy(), pos.getOpenings(), pos.getLocation(),
//					pos.getEmploymentType(), pos.getPriority(), pos.getCreatedOn()));

			String makerSubject = "";
			String makerTitle = "";
			String makerMailBody = "";

			if (approvalLevel == 1) {

				makerSubject = "Your Staffing Requisition has been approved by Level 1 (Department Head) and is now under Level 2 approval flow";

				makerTitle = "Level 1 Approved — " + roleName;

				makerMailBody = "hgertyuiuoiuy";

			} else if (approvalLevel == 2) {

				makerSubject = "Your Staffing Requisition has been approved by Level 2 (HRBP) and is now under Level 3 approval flow";

				makerTitle = "Level 2 Approved — " + roleName;

				makerMailBody = "sadfegfrdhyjgkui";

			} else if (approvalLevel == 3) {

				makerSubject = "Your Staffing Requisition has been fully approved successfully and is now ready for Recruiter Assignment and Job Creation";

				makerTitle = "Level 3 Approved — " + roleName;

				makerMailBody = "ttretyuio";
			}

//			sendMakerMail(applicantId, userId, makerRoleId, makerSubject, makerRoleName, makerTitle, makerMailBody, event);

			return ApiResponse.success("Approved successfully at level " + approvalLevel);
		}

		// REJECT FLOW

		else {

			String rejectedMailBody = "gfdhkjsl;jf";
			event.setCheckerNotificationTitle("Level " + approvalLevel + " Rejected — " + levelName);

			event.setCheckerMessage("A Staffing Requisition has been rejected in the approval flow.");

			event.setCheckerEmailBody(rejectedMailBody);
//
//			sendMakerMail(applicantId, userId, makerRoleId,
//					"Your Staffing Requisition has been rejected by Level " + approvalLevel + " (" + levelName + ")",
//					makerRoleName, "SR Rejected", rejectedMailBody, event);

			return ApiResponse.success("Rejected successfully at level " + approvalLevel);
		}
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

		for (OfferDetailsEntity offer : offers) {
			offer.setOfferReleased(true);
			offer.setOfferReleasedBy(userId);
			offer.setOfferReleasedAt(LocalDateTime.now());
		}

		offerDetailsRepository.saveAll(offers);

		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Offer letters released successfully");
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
		response.put("releaseOfferLetter",pendingApprovals+readyToRelease );

		return ApiResponse.success(ResponseCode.SUCCESS, "Offer dashboard counts fetched successfully", response);
	}

	@Override
	public ApiResponse<?> UpdateRaiseOffer(UpdateRaiseOfferRequest request) {

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

		offerDetails.setTotalCtc(request.getTotalCtc());

		offerDetails.setNoticePeriod(request.getNoticePeriod());

		offerDetails.setProbationPeriod(request.getProbationPeriod());

		offerDetails.setOfferLetterTemplate(request.getOfferLetterTemplate());

		offerDetails.setCompensation(request.getCompensation());

		offerDetails.setSubmitFinancialApproval(request.getSubmitFinancialApproval());

		offerDetails.setCreatedDate(LocalDateTime.now());

		offerDetails.setSubmittedByUserId(userId);

		offerDetails.setCreatedByRoleId(assignRole.getRoleId());

		offerDetailsRepository.save(offerDetails);

		log.info("OfferDetailsServiceImpl :: Exit UpdateRaiseOffer");

		return ApiResponse.success(ResponseCode.SUCCESS,
				"Raise Offer Request Updated Successfully", null);
	}


}
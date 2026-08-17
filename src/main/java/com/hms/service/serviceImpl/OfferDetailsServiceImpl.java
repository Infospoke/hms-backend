package com.hms.service.serviceImpl;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.dto.ApprovalStatusDto;
import com.hms.service.dto.NotificationEvent;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.BudgetAndCompensationEntity;
import com.hms.service.entity.CandidateCreationDetailsEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.FunctionalityEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.NegotiationOfferEntity;
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
import com.hms.service.repository.NegotiateOfferRepository;
import com.hms.service.repository.OfferDeatilsChildRepository;
import com.hms.service.repository.OfferDetailsRepository;
import com.hms.service.repository.OfferLetterTemplateRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.ApproveOfferRequest;
import com.hms.service.request.FinanceRecommendation;
import com.hms.service.request.HrRecommendationRequest;
import com.hms.service.request.LevelConfig;
import com.hms.service.request.ReleaseOfferRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateRaiseOfferRequest;
import com.hms.service.response.NegotiationDetailsResponse;
import com.hms.service.response.NegotiationReviewResponse;
import com.hms.service.response.OfferCommentsResponse;
import com.hms.service.response.OfferDetailsResponse;
import com.hms.service.response.OfferNegotiationResponse;
import com.hms.service.response.PendingApprovalsResponse;
import com.hms.service.response.RaiseOfferRequestResponse;
import com.hms.service.response.ReReleaseOfferDetailsResponse;
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
	private PositionBasicsRepository positionBasicsRepository;

	@Autowired
	private OfferDeatilsChildRepository offerDetailsChildRepository;

	@Autowired
	private MinioClient minioClient;

	@Autowired
	private NegotiateOfferRepository negotiationOfferRepository;

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

		map.put("reReleaseOfferId", offer.getReReleaseOfferId());

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
			response.setCandidateId(applicant.getCandidate().getCandidateId());

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

				response.setEquity(Boolean.TRUE.equals(budget.getEquity()) ? budget.getEquityAmount() : 0);

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
				response.setJoiningDate(offer.getJoiningDate());
				response.setOfferLeterPath(offer.getOfferLetterPath());

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
			response.setRole(roleMap.get(childEntity.getRole2()));
			response.setApproverName(offerDetails.getApprover2By());
			response.setApproved(offerDetails.getApprover2());
			response.setApprovedOn(offerDetails.getDateOfApproval2());
			response.setComments(offerDetails.getApprover2Comments());

			responseList.add(response);

			// Approver 3

			response = new OfferCommentsResponse();
			response.setApproverSequence("3");
			response.setRole(roleMap.get(childEntity.getRole3()));
			response.setApproverName(offerDetails.getApprover3By());
			response.setApproved(offerDetails.getApprover3());
			response.setApprovedOn(offerDetails.getDateOfApproval3());
			response.setComments(offerDetails.getApprover3Comments());

			responseList.add(response);

			log.info("Approver 1 Role ID   : {}", childEntity.getRole1());
			log.info("Approver 1 Role Name : {}", roleMap.get(childEntity.getRole1()));
			log.info("Approver 1 Comments  : {}", offerDetails.getApprover1Comments());

			log.info("Approver 2 Role ID   : {}", childEntity.getRole2());
			log.info("Approver 2 Role Name : {}", roleMap.get(childEntity.getRole2()));
			log.info("Approver 2 Comments  : {}", offerDetails.getApprover2Comments());

			log.info("Approver 3 Role ID   : {}", childEntity.getRole3());
			log.info("Approver 3 Role Name : {}", roleMap.get(childEntity.getRole3()));
			log.info("Approver 3 Comments  : {}", offerDetails.getApprover3Comments());

			log.info("OfferDetailsServiceImpl ::Exit from the getOfferComments");

			return ApiResponse.success(ResponseCode.SUCCESS, "Offer comments fetched successfully", responseList);

		} catch (Exception e) {
			log.error("OfferDetailsServiceImpl :: getOfferComments", e);
			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}

//	@Transactional
//	@Override
//	public ApiResponse<?> approveOffer(ApproveOfferRequest request) {
//
//		NotificationEvent event = new NotificationEvent();
//
//		Optional<OfferDetailsChildEntity> optional = offerDeatilsChildRepository
//				.findByJobApplication_Id(request.getApplicantId());
//
//		if (optional.isEmpty()) {
//			return ApiResponse.failure(ResponseCode.FAILURE, "No approval record found",
//					List.of("Invalid Applicant Id"));
//		}
//
//		OfferDetailsChildEntity entity = optional.get();
//
//		int currentLevel;
//
//		if (Boolean.TRUE.equals(entity.getApprover1()) && !Boolean.TRUE.equals(entity.getApprover2())) {
//
//			currentLevel = 1;
//
//		} else if (Boolean.TRUE.equals(entity.getApprover2()) && !Boolean.TRUE.equals(entity.getApprover3())) {
//
//			currentLevel = 2;
//
//		} else if (Boolean.TRUE.equals(entity.getApprover3())) {
//
//			currentLevel = 3;
//
//		} else {
//
//			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid approval flow",
//					List.of("No active approval level found"));
//		}
//
//		String roleName = getRoleNameFromToken();
//		String username = getUsernameFromToken();
//
//		Integer expectedRole;
//
//		switch (currentLevel) {
//
//		case 1:
//			expectedRole = entity.getRole1();
//			break;
//
//		case 2:
//			expectedRole = entity.getRole2();
//			break;
//
//		case 3:
//			expectedRole = entity.getRole3();
//			break;
//
//		default:
//			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid approval level",
//					List.of("Unable to determine approval level"));
//		}
//
//		String expectedRoleName = rolesRepository.findByRoleId(expectedRole)
//				.orElseThrow(() -> new RuntimeException("Role not found")).getRoleName();
//
//		log.info("Current Approval Level : {}", currentLevel);
//		log.info("Token Role : {}", roleName);
//		log.info("Expected Role Id : {}", expectedRole);
//		log.info("Expected Role Name : {}", expectedRoleName);
//
//		if (!roleName.equalsIgnoreCase(expectedRoleName)) {
//
//			return ApiResponse.failure(ResponseCode.FAILURE, "Unauthorized",
//					List.of("You are not authorized to approve this level"));
//		}
//
//		Integer submittedBy = entity.getOfferSubmittedBy();
//
//		if (submittedBy != null) {
//
//			Optional<UserEntity> makerOpt = userRepository.findByUserId(submittedBy);
//
//			if (makerOpt.isPresent() && username.equalsIgnoreCase(makerOpt.get().getUsername())) {
//
//				return ApiResponse.failure(ResponseCode.FAILURE, "Access Denied",
//						List.of("You created this Offer, so you cannot approve it"));
//			}
//		}
//
//		Optional<OfferDetailsEntity> posOpt = offerDetailsRepository.findByJobApplication_Id(request.getApplicantId());
//
//		if (posOpt.isEmpty()) {
//
//			return ApiResponse.failure(ResponseCode.FAILURE, "Offer not found", List.of("Invalid Applicant Id"));
//		}
//
//		OfferDetailsEntity pos = posOpt.get();
//
//		String applicantId = pos.getJobApplication().getId().toString();
//
//		Long userId = null;
//		String makerRoleName = null;
//		Integer makerRoleId = null;
//
//		Integer submitteBy = entity.getOfferSubmittedBy();
//
//		if (submittedBy != null) {
//
//			Optional<UserEntity> maker = userRepository.findByUserId(submittedBy);
//
//			if (maker.isEmpty()) {
//				log.error("Maker not found for userId : {}", submittedBy);
//				return ApiResponse.failure(ResponseCode.FAILURE, "Maker not found");
//			}
//
//			UserEntity userEntity = maker.get();
//
//			if (userEntity == null) {
//				throw new RuntimeException("Maker not found");
//			}
//
//			userId = userEntity.getUserId().longValue();
//			Integer roleId = assignRolesRepository.findByUserId(submitteBy).get().getRoleId();
//
//			Optional<RolesEntity> role = rolesRepository.findByRoleId(roleId);
//
//			RolesEntity roles = role.get();
//			if (roles == null) {
//				throw new RuntimeException("Maker Role not found");
//			}
//
//			makerRoleName = roles.getRoleName();
//		}
//
//		int approvalLevel;
//
//		if (!Boolean.TRUE.equals(pos.getApprover1())) {
//
//			approvalLevel = 1;
//
//		} else if (!Boolean.TRUE.equals(pos.getApprover2())) {
//
//			approvalLevel = 2;
//
//		} else if (!Boolean.TRUE.equals(pos.getApprover3())) {
//
//			approvalLevel = 3;
//
//		} else {
//
//			return ApiResponse.success("All approvals already completed");
//		}
//
//		boolean approved = Boolean.TRUE.equals(request.getApprove());
//
//		if (approvalLevel == 3 && approved) {
//
//			if (request.getESignature() == null || request.getESignature().trim().isEmpty()) {
//
//				return ApiResponse.failure(ResponseCode.FAILURE, "Approval Failed",
//						List.of("HR Head e-signature is mandatory for Level 3 approval."));
//			}
//		}
//
//		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
//
//		String levelName = "";
//		String approverName = "";
//		LocalDateTime approvedDate = null;
//		Object approvalStatus = null;
//
//		switch (approvalLevel) {
//
//		case 1:
//
//			levelName = roleName;
//
//			pos.setApprover1By(username);
//			pos.setApprover1Role(roleName);
//			pos.setDateOfApproval1(now);
//			pos.setApprover1Comments(request.getComments());
//
//			if ("NEGOTIATION".equalsIgnoreCase(request.getApprovalType())) {
//
//				if (request.getFinanceRecommendations() == null || request.getFinanceRecommendations().isEmpty()) {
//
//					return ApiResponse.failure(ResponseCode.FAILURE, "Finance recommendations are mandatory");
//				}
//
//				if (request.getFinanceReason() == null || request.getFinanceReason().isBlank()) {
//					return ApiResponse.failure(ResponseCode.FAILURE, "Finance reason is mandatory");
//				}
//
//				// Update Negotiation Table
//
//				log.info("Request Applicant Id : {}", request.getApplicantId());
//
//				Optional<NegotiationOfferEntity> negotiationOpt = negotiationOfferRepository
//						.findByApplicant_Id(request.getApplicantId());
//
//				log.info("Negotiation Found : {}", negotiationOpt.isPresent());
//
//				if (negotiationOpt.isPresent()) {
//
//					NegotiationOfferEntity negotiation = negotiationOpt.get();
//
//					log.info("Negotiation Id : {}", negotiation.getId());
//
//					negotiation.setFinanceRecommendations(request.getFinanceRecommendations());
//
//					negotiation.setFinanceReason(request.getFinanceReason());
//
//					negotiationOfferRepository.save(negotiation);
//
//					log.info("Negotiation saved successfully");
//				} else {
//
//					log.error("Negotiation record not found for applicantId={}", request.getApplicantId());
//
//				}
//			}
//
//			approverName = pos.getApprover1By();
//			approvedDate = pos.getDateOfApproval1();
//
//			if (approved) {
//
//				pos.setApprover1(true);
//
//				approvalStatus = pos.getApprover1();
//
//				// Activate next approver
//				entity.setApprover2(true);
//			}
//
//			break;
//
//		case 2:
//
//			levelName = roleName;
//
//			pos.setApprover2By(username);
//			pos.setApprover2Role(roleName);
//			pos.setDateOfApproval2(now);
//			pos.setApprover2Comments(request.getComments());
//
//			if ("NEGOTIATION".equalsIgnoreCase(request.getApprovalType())) {
//
//				Optional<NegotiationOfferEntity> negotiationOpt = negotiationOfferRepository
//						.findByApplicant_Id(request.getApplicantId());
//
//				if (negotiationOpt.isPresent()) {
//
//					NegotiationOfferEntity negotiation = negotiationOpt.get();
//
//					if (negotiation.getFinanceRecommendations() != null) {
//
//						Long basicPay = negotiation.getFinanceRecommendations().stream()
//								.filter(f -> "Basic Pay".equalsIgnoreCase(f.getFieldName()))
//								.map(FinanceRecommendation::getAmount).findFirst().orElse(null);
//
//						if (basicPay != null) {
//							pos.setTotalCtc(basicPay);
//						}
//					}
//				}
//			}
//
//			approverName = pos.getApprover2By();
//			approvedDate = pos.getDateOfApproval2();
//
//			if (approved) {
//
//				pos.setApprover2(true);
//
//				approvalStatus = pos.getApprover2();
//
//				// Activate next approver
//				entity.setApprover3(true);
//			}
//
//			break;
//
//		case 3:
//
//			levelName = roleName;
//
//			pos.setApprover3By(username);
//			pos.setApprover3Role(roleName);
//			pos.setDateOfApproval3(now);
//			pos.setApprover3Comments(request.getComments());
//
//			approverName = pos.getApprover3By();
//			approvedDate = pos.getDateOfApproval3();
//
//			if (approved) {
//
//				pos.setApprover3(true);
//
//				// All 3 approval levels completed
//				pos.setApprove(true);
//				pos.setInProgress(false);
//				pos.setReject(false);
//
//				approvalStatus = pos.getApprover3();
//				pos.setOfferLetterPath(request.getOfferLetterPath());
//			}
//
//			break;
//
//		default:
//
//			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid Approval Level",
//					List.of("Unable to process approval"));
//		}
//
//		if (approved) {
//
//			pos.setReject(false);
//
//		} else {
//
//			pos.setReject(true);
//			pos.setInProgress(true);
//		}
//
//		offerDetailsRepository.save(pos);
//		offerDeatilsChildRepository.save(entity);
//
//		Map<Integer, List<String>> roleEmailMap = processApprovalChain(request.getApplicantId());
//
//		Integer roleId = null;
//
//		for (Map.Entry<Integer, List<String>> entry : roleEmailMap.entrySet()) {
//
//			roleId = entry.getKey();
//		}
//
//		String checkerRoleName = rolesRepository.findByRoleId(roleId).get().getRoleName();
//
//		event.setProcessId(pos.getId().toString());
//		event.setType("SR");
//		event.setCheckerRoleName(checkerRoleName);
//		event.setRoleEmailMap(roleEmailMap);
//
//		if (approved) {
//
//			event.setCheckerNotificationTitle("Level " + approvalLevel + " Approved — " + levelName);
//
//			event.setCheckerMessage("A offer is now under your approval flow for review and approval");
//
//			event.setCheckerEmailBody(String.format(Constants.OFFER_TO_BE_APPROVED_MAIL_BODY, checkerRoleName,
//					applicantId, pos.getJobApplication().getFirstName() + " " + pos.getJobApplication().getLastName(),
//					pos.getJobApplication().getEmail(), pos.getTotalCtc(), pos.getNoticePeriod(),
//					pos.getProbationPeriod(), pos.getSubmittedByUserId(), pos.getCreatedDate()));
//
//			String makerSubject = "";
//			String makerTitle = "";
//			String makerMailBody = "";
//
//			switch (approvalLevel) {
//
//			case 1:
//
//				makerSubject = "Your offer has been approved by Level 1 (Finance Analyst) and is now under Level 2 approval flow";
//
//				makerTitle = "Level 1 Approved — " + roleName;
//
//				makerMailBody = Constants.OFFER_LEVEL1_APPROVED_MAIL_BODY;
//
//				break;
//
//			case 2:
//
//				makerSubject = "Your offer has been approved by Level 2 (Finance Head) and is now under Level 3 approval flow";
//
//				makerTitle = "Level 2 Approved — " + roleName;
//
//				makerMailBody = Constants.OFFER_LEVEL2_APPROVED_MAIL_BODY;
//				;
//
//				break;
//
//			case 3:
//
//				makerSubject = "Your offer has been fully approved successfully and is now ready to release";
//
//				makerTitle = "Level 3 Approved — " + roleName;
//
//				makerMailBody = Constants.OFFER_LEVEL3_APPROVED_MAIL_BODY;
//				;
//
//				break;
//			}
//
//			sendMakerMail(applicantId, userId, makerRoleId, makerSubject, makerRoleName, makerTitle, makerMailBody,
//					event);
//
//			return ApiResponse.success("Approved successfully at level " + approvalLevel);
//		}
//
//		String rejectedMailBody = Constants.OFFER_REJECTED_MAIL_BODY;
//
//		event.setCheckerNotificationTitle("Level " + approvalLevel + " Rejected — " + levelName);
//
//		event.setCheckerMessage("A Offer has been rejected in the approval flow.");
//
//		event.setCheckerEmailBody(rejectedMailBody);
//
//		sendMakerMail(applicantId, userId, makerRoleId,
//				"Your Offer  has been rejected by Level " + approvalLevel + " (" + levelName + ")", makerRoleName,
//				"OFFER Rejected", rejectedMailBody, event);
//
//		return ApiResponse.success("Rejected successfully at level " + approvalLevel);
//	}
	@Transactional
	@Override
	public ApiResponse<?> approveOffer(ApproveOfferRequest request) {

	    NotificationEvent event = new NotificationEvent();

	   

	    Optional<OfferDetailsEntity> pendingOfferOpt =
	            offerDetailsRepository.findPendingOfferForApproval(
	                    request.getApplicantId()
	            );

	    if (pendingOfferOpt.isEmpty()) {

	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "No pending approval record found",
	                List.of("Invalid Applicant Id or no pending offer found")
	        );
	    }

	    OfferDetailsEntity pos = pendingOfferOpt.get();

	    log.info("Pending OfferDetails Id : {}", pos.getId());
	    log.info("Applicant Id : {}", request.getApplicantId());
	    log.info("Offer Status : {}", pos.getOfferStatus());


	    Optional<OfferDetailsChildEntity> optional =
	            offerDeatilsChildRepository.findByOffer_Id(pos.getId());

	    if (optional.isEmpty()) {

	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "No approval record found",
	                List.of("Approval configuration not found for this offer")
	        );
	    }

	    OfferDetailsChildEntity entity = optional.get();


	    int currentLevel;

	    if (!Boolean.TRUE.equals(pos.getApprover1())) {

	        currentLevel = 1;

	    } else if (!Boolean.TRUE.equals(pos.getApprover2())) {

	        currentLevel = 2;

	    } else if (!Boolean.TRUE.equals(pos.getApprover3())) {

	        currentLevel = 3;

	    } else {

	        return ApiResponse.success(
	                "All approvals already completed"
	        );
	    }

	    log.info("Current Approval Level : {}", currentLevel);

	    // =========================================================
	    // 4. GET TOKEN USER DETAILS
	    // =========================================================

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
	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "Invalid approval level",
	                    List.of("Unable to determine approval level")
	            );
	    }

	    // =========================================================
	    // 5. VALIDATE ROLE
	    // =========================================================

	    String expectedRoleName =
	            rolesRepository.findByRoleId(expectedRole)
	                    .orElseThrow(
	                            () -> new RuntimeException("Role not found")
	                    )
	                    .getRoleName();

	    log.info("Token Role : {}", roleName);
	    log.info("Expected Role Id : {}", expectedRole);
	    log.info("Expected Role Name : {}", expectedRoleName);

	    if (!roleName.equalsIgnoreCase(expectedRoleName)) {

	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "Unauthorized",
	                List.of("You are not authorized to approve this level")
	        );
	    }

	    // =========================================================
	    // 6. MAKER DETAILS
	    // =========================================================

	    Integer submittedBy = entity.getOfferSubmittedBy();

	    Long userId = null;
	    String makerRoleName = null;
	    Integer makerRoleId = null;

	    if (submittedBy != null) {

	        Optional<UserEntity> makerOpt =
	                userRepository.findByUserId(submittedBy);

	        if (makerOpt.isEmpty()) {

	            log.error(
	                    "Maker not found for userId : {}",
	                    submittedBy
	            );

	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "Maker not found"
	            );
	        }

	        UserEntity maker = makerOpt.get();

	        if (username.equalsIgnoreCase(maker.getUsername())) {

	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "Access Denied",
	                    List.of(
	                            "You created this Offer, so you cannot approve it"
	                    )
	            );
	        }

	        userId = maker.getUserId().longValue();

	        Optional<AssignRolesEntity> assignRoleOpt =
	                assignRolesRepository.findByUserId(submittedBy);

	        if (assignRoleOpt.isEmpty()) {

	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "Maker role not found"
	            );
	        }

	        makerRoleId = assignRoleOpt.get().getRoleId();

	        Optional<RolesEntity> roleOpt =
	                rolesRepository.findByRoleId(makerRoleId);

	        if (roleOpt.isEmpty()) {

	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "Maker role not found"
	            );
	        }

	        makerRoleName = roleOpt.get().getRoleName();
	    }

	    // =========================================================
	    // 7. DETERMINE APPROVAL LEVEL AGAINST PENDING OFFER
	    // =========================================================

	    int approvalLevel;

	    if (!Boolean.TRUE.equals(pos.getApprover1())) {

	        approvalLevel = 1;

	    } else if (!Boolean.TRUE.equals(pos.getApprover2())) {

	        approvalLevel = 2;

	    } else if (!Boolean.TRUE.equals(pos.getApprover3())) {

	        approvalLevel = 3;

	    } else {

	        return ApiResponse.success(
	                "All approvals already completed"
	        );
	    }

	    log.info("Processing Approval Level : {}", approvalLevel);

	    // =========================================================
	    // 8. APPROVE BOOLEAN
	    // =========================================================

	    boolean approved =
	            Boolean.TRUE.equals(request.getApprove());

	    // =========================================================
	    // 9. E-SIGNATURE FOR LEVEL 3
	    // =========================================================

	    if (approvalLevel == 3 && approved) {

	        if (request.getESignature() == null
	                || request.getESignature().trim().isEmpty()) {

	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "Approval Failed",
	                    List.of(
	                            "HR Head e-signature is mandatory for Level 3 approval."
	                    )
	            );
	        }
	    }

	    LocalDateTime now =
	            LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

	    String levelName = "";
	    String approverName = "";
	    LocalDateTime approvedDate = null;
	    Object approvalStatus = null;

	    // =========================================================
	    // 10. PROCESS APPROVAL LEVEL
	    // =========================================================

	    switch (approvalLevel) {

	        // -----------------------------------------------------
	        // LEVEL 1
	        // -----------------------------------------------------

	        case 1:

	            levelName = roleName;

	            pos.setApprover1By(username);
	            pos.setApprover1Role(roleName);
	            pos.setDateOfApproval1(now);
	            pos.setApprover1Comments(request.getComments());

	            // -----------------------------------------------
	            // Negotiation specific processing
	            // -----------------------------------------------

	            if ("NEGOTIATION".equalsIgnoreCase(
	                    request.getApprovalType())) {

	                if (request.getFinanceRecommendations() == null
	                        || request.getFinanceRecommendations().isEmpty()) {

	                    return ApiResponse.failure(
	                            ResponseCode.FAILURE,
	                            "Finance recommendations are mandatory"
	                    );
	                }

	                if (request.getFinanceReason() == null
	                        || request.getFinanceReason().isBlank()) {

	                    return ApiResponse.failure(
	                            ResponseCode.FAILURE,
	                            "Finance reason is mandatory"
	                    );
	                }

	                log.info(
	                        "Request Applicant Id : {}",
	                        request.getApplicantId()
	                );

	                Optional<NegotiationOfferEntity> negotiationOpt =
	                        negotiationOfferRepository
	                                .findByApplicant_Id(
	                                        request.getApplicantId()
	                                );

	                if (negotiationOpt.isPresent()) {

	                    NegotiationOfferEntity negotiation =
	                            negotiationOpt.get();

	                    log.info(
	                            "Negotiation Id : {}",
	                            negotiation.getId()
	                    );

	                    negotiation.setFinanceRecommendations(
	                            request.getFinanceRecommendations()
	                    );

	                    negotiation.setFinanceReason(
	                            request.getFinanceReason()
	                    );

	                    negotiationOfferRepository.save(
	                            negotiation
	                    );

	                    log.info(
	                            "Negotiation saved successfully"
	                    );

	                } else {

	                    log.error(
	                            "Negotiation record not found for applicantId={}",
	                            request.getApplicantId()
	                    );
	                }
	            }

	            approverName = pos.getApprover1By();
	            approvedDate = pos.getDateOfApproval1();

	            if (approved) {

	                pos.setApprover1(true);

	                approvalStatus = pos.getApprover1();

	                // Activate Level 2
	                entity.setApprover2(true);
	            }

	            break;

	        // -----------------------------------------------------
	        // LEVEL 2
	        // -----------------------------------------------------

	        case 2:

	            levelName = roleName;

	            pos.setApprover2By(username);
	            pos.setApprover2Role(roleName);
	            pos.setDateOfApproval2(now);
	            pos.setApprover2Comments(request.getComments());

	            // -----------------------------------------------
	            // Negotiation specific processing
	            // -----------------------------------------------

	            if ("NEGOTIATION".equalsIgnoreCase(
	                    request.getApprovalType())) {

	                Optional<NegotiationOfferEntity> negotiationOpt =
	                        negotiationOfferRepository
	                                .findByApplicant_Id(
	                                        request.getApplicantId()
	                                );

	                if (negotiationOpt.isPresent()) {

	                    NegotiationOfferEntity negotiation =
	                            negotiationOpt.get();

	                    if (negotiation.getFinanceRecommendations()
	                            != null) {

	                        Long basicPay =
	                                negotiation
	                                        .getFinanceRecommendations()
	                                        .stream()
	                                        .filter(
	                                                f -> "Basic Pay"
	                                                        .equalsIgnoreCase(
	                                                                f.getFieldName()
	                                                        )
	                                        )
	                                        .map(
	                                                FinanceRecommendation::getAmount
	                                        )
	                                        .findFirst()
	                                        .orElse(null);

	                        if (basicPay != null) {

	                            pos.setTotalCtc(basicPay);
	                        }
	                    }
	                }
	            }

	            approverName = pos.getApprover2By();
	            approvedDate = pos.getDateOfApproval2();

	            if (approved) {

	                pos.setApprover2(true);

	                approvalStatus = pos.getApprover2();

	                // Activate Level 3
	                entity.setApprover3(true);
	            }

	            break;

	        // -----------------------------------------------------
	        // LEVEL 3
	        // -----------------------------------------------------

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

	                // All 3 approval levels completed
	                pos.setApprove(true);
	                pos.setInProgress(false);
	                pos.setReject(false);

	                approvalStatus = pos.getApprover3();

	                pos.setOfferLetterPath(
	                        request.getOfferLetterPath()
	                );
	            }

	            break;

	        default:

	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "Invalid Approval Level",
	                    List.of(
	                            "Unable to process approval"
	                    )
	            );
	    }

	    // =========================================================
	    // 11. APPROVAL / REJECTION STATUS
	    // =========================================================

	    if (approved) {

	        pos.setReject(false);

	    } else {

	        pos.setReject(true);
	        pos.setInProgress(true);
	    }

	    // =========================================================
	    // 12. SAVE BOTH RECORDS
	    // =========================================================

	    offerDetailsRepository.save(pos);

	    offerDeatilsChildRepository.save(entity);

	    log.info(
	            "OfferDetails updated successfully. Offer Id : {}",
	            pos.getId()
	    );

	    // =========================================================
	    // 13. PROCESS APPROVAL CHAIN
	    // =========================================================

	    Map<Integer, List<String>> roleEmailMap =
	            processApprovalChain(
	                    request.getApplicantId()
	            );

	    Integer roleId = null;

	    for (Map.Entry<Integer, List<String>> entry :
	            roleEmailMap.entrySet()) {

	        roleId = entry.getKey();
	    }

	    if (roleId == null) {

	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "Approval role not found"
	        );
	    }

	    String checkerRoleName =
	            rolesRepository.findByRoleId(roleId)
	                    .orElseThrow(
	                            () -> new RuntimeException(
	                                    "Checker role not found"
	                            )
	                    )
	                    .getRoleName();

	    // =========================================================
	    // 14. NOTIFICATION EVENT
	    // =========================================================

	    event.setProcessId(
	            pos.getId().toString()
	    );

	    event.setType("SR");

	    event.setCheckerRoleName(
	            checkerRoleName
	    );

	    event.setRoleEmailMap(
	            roleEmailMap
	    );

	    String applicantId =
	            pos.getJobApplication()
	                    .getId()
	                    .toString();

	    // =========================================================
	    // 15. APPROVED FLOW
	    // =========================================================

	    if (approved) {

	        event.setCheckerNotificationTitle(
	                "Level " + approvalLevel
	                        + " Approved — "
	                        + levelName
	        );

	        event.setCheckerMessage(
	                "A offer is now under your approval flow for review and approval"
	        );

	        event.setCheckerEmailBody(
	                String.format(
	                        Constants.OFFER_TO_BE_APPROVED_MAIL_BODY,
	                        checkerRoleName,
	                        applicantId,
	                        pos.getJobApplication().getFirstName()
	                                + " "
	                                + pos.getJobApplication().getLastName(),
	                        pos.getJobApplication().getEmail(),
	                        pos.getTotalCtc(),
	                        pos.getNoticePeriod(),
	                        pos.getProbationPeriod(),
	                        pos.getSubmittedByUserId(),
	                        pos.getCreatedDate()
	                )
	        );

	        String makerSubject = "";
	        String makerTitle = "";
	        String makerMailBody = "";

	        switch (approvalLevel) {

	            case 1:

	                makerSubject =
	                        "Your offer has been approved by Level 1 "
	                        + "(Finance Analyst) and is now under Level 2 approval flow";

	                makerTitle =
	                        "Level 1 Approved — " + roleName;

	                makerMailBody =
	                        Constants.OFFER_LEVEL1_APPROVED_MAIL_BODY;

	                break;

	            case 2:

	                makerSubject =
	                        "Your offer has been approved by Level 2 "
	                        + "(Finance Head) and is now under Level 3 approval flow";

	                makerTitle =
	                        "Level 2 Approved — " + roleName;

	                makerMailBody =
	                        Constants.OFFER_LEVEL2_APPROVED_MAIL_BODY;

	                break;

	            case 3:

	                makerSubject =
	                        "Your offer has been fully approved successfully "
	                        + "and is now ready to release";

	                makerTitle =
	                        "Level 3 Approved — " + roleName;

	                makerMailBody =
	                        Constants.OFFER_LEVEL3_APPROVED_MAIL_BODY;

	                break;

	            default:
	                break;
	        }

	        sendMakerMail(
	                applicantId,
	                userId,
	                makerRoleId,
	                makerSubject,
	                makerRoleName,
	                makerTitle,
	                makerMailBody,
	                event
	        );

	        return ApiResponse.success(
	                "Approved successfully at level "
	                        + approvalLevel
	        );
	    }

	    // =========================================================
	    // 16. REJECTED FLOW
	    // =========================================================

	    String rejectedMailBody =
	            Constants.OFFER_REJECTED_MAIL_BODY;

	    event.setCheckerNotificationTitle(
	            "Level " + approvalLevel
	                    + " Rejected — "
	                    + levelName
	    );

	    event.setCheckerMessage(
	            "A Offer has been rejected in the approval flow."
	    );

	    event.setCheckerEmailBody(
	            rejectedMailBody
	    );

	    sendMakerMail(
	            applicantId,
	            userId,
	            makerRoleId,
	            "Your Offer has been rejected by Level "
	                    + approvalLevel
	                    + " (" + levelName + ")",
	            makerRoleName,
	            "OFFER Rejected",
	            rejectedMailBody,
	            event
	    );

	    return ApiResponse.success(
	            "Rejected successfully at level "
	                    + approvalLevel
	    );
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
				.findByJobApplication_IdAndNegotiationFalse(applicantId);

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

		Optional<OfferDetailsEntity> offerOptional = offerDetailsRepository
				.findByJobApplication_IdAndNegotiationFalse(applicantId);

		if (offerOptional.isPresent()) {

			OfferDetailsEntity offerEntity = offerOptional.get();

			childEntity.setOfferSubmittedBy(offerEntity.getSubmittedByUserId());

			childEntity.setJobApplication(offerEntity.getJobApplication());

			OfferDetailsEntity offer = new OfferDetailsEntity();
			offer.setId(offerEntity.getId()); // Primary key

			childEntity.setOffer(offer);

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

		Long newOfferRequests = offerDetailsRepository.countNewOfferRequests();

		Long newApprovals = offerDetailsRepository.countNewOfferApprovals();

		Long negotiationApprovals = offerDetailsRepository.countNegotiationApprovals();

		Long pendingRelease = offerDetailsRepository.countPendingRelease();

		Long reRelease = offerDetailsRepository.countReRelease();

		Long candidateResponses = offerDetailsRepository.countCandidateResponses();

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("newOfferRequests", newOfferRequests);

		response.put("offerApprovals", Map.of("new", newApprovals, "negotiation", negotiationApprovals, "total",
				newApprovals + negotiationApprovals));

		response.put("releaseOfferLetter",
				Map.of("pending", pendingRelease, "reRelease", reRelease, "total", pendingRelease + reRelease));

		response.put("candidateResponses", candidateResponses);

		return ApiResponse.success(ResponseCode.SUCCESS, "Offer dashboard counts fetched successfully", response);
	}

	@Override
	public ApiResponse<?> submitFinancialApproval(UpdateRaiseOfferRequest request) {

		log.info("OfferDetailsServiceImpl :: Inside UpdateRaiseOffer");

		String authHeader = httpServletRequest.getHeader("Authorization");

		Integer userId = null;

		String roleName = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token).intValue();

			roleName = jwtService.extractRole(token);
		}

		if (!"Recruiter".equalsIgnoreCase(roleName)) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Access Denied",
					List.of("Only Recruiter can Submit Raise offer Request."));
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

		if (!request.getJoiningDate().isAfter(LocalDate.now())) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Joining date must be a future date");
		}

		offerDetails.setJoiningDate(request.getJoiningDate());

		offerDetails.setTotalCtc(request.getTotalCtc());

		offerDetails.setNoticePeriod(request.getNoticePeriod());

		offerDetails.setProbationPeriod(request.getProbationPeriod());

		offerDetails.setOfferLetterTemplate(template);

		offerDetails.setCompensation(request.getCompensation());

		offerDetails.setSubmitFinancialApproval(request.getSubmitFinancialApproval());

		offerDetails.setCreatedDate(LocalDateTime.now());

		offerDetails.setSubmittedByUserId(userId);
		
		offerDetails.setOfferStatus("Pending");
		
		offerDetails.setCreatedByRoleId(assignRole.getRoleId());

		offerDetailsRepository.save(offerDetails);

		log.info("OfferDetailsServiceImpl :: Exit UpdateRaiseOffer");
		processApprovalChain(offerDetails.getJobApplication().getId());

		return ApiResponse.success(ResponseCode.SUCCESS, "Raise Offer Request Updated Successfully", null);
	}

	@Override
	public void viewOfferLetter(Integer appId, String action, HttpServletResponse response) {

		log.info("JobsServiceImpl: Inside viewOfferLetter method");

		JobApplicationEntity application = jobApplicationRepository.findById(appId)
				.orElseThrow(() -> new RuntimeException(Constants.APPLICATION_NOT_FOUND));

		String candidateName = application.getFirstName() + "_" + application.getLastName();

		String objectKey = "offer-letters/" + appId + "/" + candidateName + "_Offer_Letter.pdf";

		String fileName = Paths.get(objectKey).getFileName().toString();

		try {

			InputStream minioStream = minioClient
					.getObject(GetObjectArgs.builder().bucket(Constants.BUCKETNAME).object(objectKey).build());

			String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");

			response.setContentType("application/pdf");
			response.setCharacterEncoding("UTF-8");

			response.setHeader("Content-Disposition", ("view".equalsIgnoreCase(action) ? "inline" : "attachment")
					+ "; filename*=UTF-8''" + encodedFileName);

			IOUtils.copy(minioStream, response.getOutputStream());

			response.flushBuffer();

			minioStream.close();

		} catch (Exception e) {

			log.error("JobsServiceImpl::Exception occurred in viewOfferLetter method", e);

			throw new RuntimeException("Error downloading offer letter from MinIO", e);
		}
	}

	@Override
	public ApiResponse<?> getPendingApprovals(SpecificationFilterRequest request) {

		log.info("OfferDetailsServiceImpl :: getPendingApprovals");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));
		
		Page<OfferDetailsEntity> offerPage;

		String approvalType = request.getFilter("approvalType");
		
		log.info("Approval Type : {}", approvalType);

		if ("New Offer Approvals".equalsIgnoreCase(approvalType)) {

		    offerPage = offerDetailsRepository.findNewOfferApprovals(pageable);

		} else if ("Negotiation Approvals".equalsIgnoreCase(approvalType)) {

			 log.info("Fetching offers for negotiation approvals");
			
			offerPage = offerDetailsRepository.findPendingNegotiationApprovals(pageable);
			
			log.info("Negotiation approval records found : {}", offerPage.getTotalElements());

		} else {

			Specification<OfferDetailsEntity> specification = request.buildOfferApprovalSpecification();

			offerPage = offerDetailsRepository.findAll(specification, pageable);
			
			 log.info("New offer approval records found : {}", offerPage.getTotalElements());
		}

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
						new ApprovalStatusDto(offer.getApprover1Role(), Boolean.TRUE.equals(offer.getApprover1())));

				approvals.add(
						new ApprovalStatusDto(offer.getApprover2Role(), Boolean.TRUE.equals(offer.getApprover2())));

				approvals.add(
						new ApprovalStatusDto(offer.getApprover3Role(), Boolean.TRUE.equals(offer.getApprover3())));
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
			offer.setOfferStatus("Pending");
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

	@Override
	public ApiResponse<?> getAllPendingApprovals(SpecificationFilterRequest request) {

		String authHeader = httpServletRequest.getHeader("Authorization");

		Long userId = null;
		Long roleId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			roleId = jwtService.extractRoleId(token);
			userId = jwtService.extractUserId(token);
		}

		System.out.println("User Id : " + userId);
		System.out.println("Role Id : " + roleId);
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

		Specification<OfferDetailsEntity> specification = request.buildPendingApprovalSpecification(roleId);

		Page<OfferDetailsEntity> page = offerDetailsRepository.findAll(specification, pageable);
		List<PendingApprovalsResponse> response = new ArrayList<>();

		Map<String, Object> data = new HashMap<>();
		data.put("pendingApprovals", response);
		data.put("currentPage", page.getNumber());
		data.put("totalPages", page.getTotalPages());
		data.put("totalElements", page.getTotalElements());
		data.put("pageSize", page.getSize());

		for (OfferDetailsEntity entity : page.getContent()) {

			OfferDetailsChildEntity childEntity = offerDeatilsChildRepository.findByOfferId(entity.getId())
					.orElse(null);

			response.add(mapToResponse(entity, roleId.intValue(), childEntity));
		}
		return ApiResponse.success(ResponseCode.SUCCESS, "Pending approvals fetched successfully", data);

	}

	private PendingApprovalsResponse mapToResponse(OfferDetailsEntity entity, Integer roleId,
			OfferDetailsChildEntity childEntity) {

		PendingApprovalsResponse response = new PendingApprovalsResponse();

		response.setOfferId(entity.getId());

		Optional<JobApplicationEntity> jobApplicationEntity = jobApplicationRepository
				.findById(entity.getJobApplication().getId());

		JobApplicationEntity entities = jobApplicationEntity.get();
		String applicantName = entities.getFirstName() + " " + entities.getLastName();

		response.setApplicantName(applicantName);
		response.setApplicantEmail(entities.getEmail());
		response.setApplicationId(entity.getJobApplication().getId());
		CreateJobDetailsEntity createJob = createJobDetailsRepository.findByJobId(entities.getJobId());
		response.setJobTitle(createJob.getJobTitle());
		Integer deptId = createJob.getDepartmentId();
		String srId = createJob.getSrId();
		String deptName = departmentsRepository.findById(deptId).get().getDepartmentName();
		response.setDepartment(deptName);
		LocalDateTime requestedOn = null;

		if (roleId.equals(childEntity.getRole1())) {
			requestedOn = entity.getCreatedDate();
		} else if (roleId.equals(childEntity.getRole2())) {
			requestedOn = entity.getDateOfApproval1();
		} else if (roleId.equals(childEntity.getRole3())) {
			requestedOn = entity.getDateOfApproval2();
		}

		response.setRequestedOn(requestedOn);
		response.setPriority(calculatePendingApprovalPriority(entity, roleId, childEntity));
		String employementType = positionBasicsRepository.findBySrId(srId).get().getEmploymentType();
		String userName = userRepository.findByUserId(entity.getSubmittedByUserId()).get().getUsername();
		response.setUserName(userName);
		response.setEmployementType(employementType);

		return response;
	}

	private String calculatePendingApprovalPriority(OfferDetailsEntity offer, Integer loginRoleId,
			OfferDetailsChildEntity child) {

		LocalDateTime slaStartDate = null;

		if (loginRoleId.equals(child.getRole1())) {

			slaStartDate = offer.getCreatedDate();

		} else if (loginRoleId.equals(child.getRole2())) {

			slaStartDate = offer.getDateOfApproval1();

		} else if (loginRoleId.equals(child.getRole3())) {

			slaStartDate = offer.getDateOfApproval2();

		}

		if (slaStartDate == null) {
			return "Low";
		}

		LocalDate dueDate = slaStartDate.toLocalDate().plusDays(7);

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
	public ApiResponse<?> getOfferNegotiationList(SpecificationFilterRequest request) {

		log.info("NegotiationOfferServiceImpl : getOfferNegotiationList");

		Sort sort = request.getDirection().equalsIgnoreCase("ASC") ? Sort.by(request.getSortBy()).ascending()
				: Sort.by(request.getSortBy()).descending();

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		String status = request.getFilter("status");

		// ================= NEGOTIATION =================
		if ("Requested for Negotiation".equalsIgnoreCase(status)) {

			Page<NegotiationOfferEntity> negotiationPage = negotiationOfferRepository
					.findAll(request.buildOfferNegotiationSpecification(), pageable);

			List<OfferNegotiationResponse> responseList = negotiationPage.getContent().stream()
					.map(this::mapNegotiationResponse).toList();

			return ApiResponse.success(ResponseCode.SUCCESS, "Success",
					Map.of("content", responseList, "currentPage", negotiationPage.getNumber(), "totalPages",
							negotiationPage.getTotalPages(), "totalElements", negotiationPage.getTotalElements(),
							"size", negotiationPage.getSize(), "last", negotiationPage.isLast()));
		}

		Set<String> validOfferStatuses = Set.of("Pending", "Accepted", "Rejected", "Expired");

		boolean validStatus = validOfferStatuses.stream().anyMatch(s -> s.equalsIgnoreCase(status));

		if (!validStatus) {

			return ApiResponse.success(ResponseCode.SUCCESS, "Invalid status.", Collections.emptyMap());
		}

		// ================= OFFER =================
		Page<OfferDetailsEntity> offerPage = offerDetailsRepository.findAll(request.buildOfferStatusSpecification(),
				pageable);

		List<OfferNegotiationResponse> responseList = offerPage.getContent().stream().map(this::mapOfferResponse)
				.toList();

		return ApiResponse.success(ResponseCode.SUCCESS, "Success",
				Map.of("content", responseList, "currentPage", offerPage.getNumber(), "totalPages",
						offerPage.getTotalPages(), "totalElements", offerPage.getTotalElements(), "size",
						offerPage.getSize(), "last", offerPage.isLast()));
	}

	private OfferNegotiationResponse mapNegotiationResponse(NegotiationOfferEntity entity) {

		OfferNegotiationResponse response = new OfferNegotiationResponse();

		response.setNegotiationId(entity.getId());

		response.setApplicantId(entity.getApplicant().getId());

		if (entity.getCandidate() != null) {

			response.setCandidateId(entity.getCandidate().getCandidateId());
			response.setCandidateName(entity.getCandidate().getFirstName());
			response.setEmail(entity.getCandidate().getEmail());
		}

		if (entity.getJob() != null) {
			response.setJobTitle(entity.getJob().getJobTitle());
		}

		response.setApprovedAmount(entity.getApprovedAmount());

		response.setOfferNegotiationDate(entity.getOfferNegotiatedDate());

		response.setPriority(getPriority(entity.getOfferNegotiatedDate()));

		if (entity.getOffer() != null) {

			response.setOfferedAmount(entity.getOffer().getTotalCtc());

			response.setStatus(entity.getOffer().getOfferStatus());
		}

		response.setRequestedAmount(entity.getTotalRequestedAmount());

		return response;
	}

	private OfferNegotiationResponse mapOfferResponse(OfferDetailsEntity offer) {

		OfferNegotiationResponse response = new OfferNegotiationResponse();

		JobApplicationEntity application = offer.getJobApplication();

		if (application != null) {

			response.setApplicantId(application.getId());

			CandidateCreationDetailsEntity candidate = application.getCandidate();

			if (candidate != null) {

				response.setCandidateId(candidate.getCandidateId());
				response.setCandidateName(candidate.getFirstName());
				response.setEmail(candidate.getEmail());
			}

			CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

			if (job != null) {
				response.setJobTitle(job.getJobTitle());
			}
		}

		response.setOfferedAmount(offer.getTotalCtc());

		response.setStatus(offer.getOfferStatus());

		response.setOfferReleasedDate(offer.getOfferReleasedAt());

		return response;
	}

	private String getPriority(LocalDate negotiationDate) {

		if (negotiationDate == null) {
			return "";
		}

		long days = ChronoUnit.DAYS.between(negotiationDate, LocalDate.now());

		if (days <= 1) {
			return "LOW";
		}

		if (days == 2) {
			return "MEDIUM";
		}

		return "HIGH";
	}

	@Override
	public ApiResponse<?> getNegotiationDetails(Integer applicantId) {

		log.info("Fetching negotiation details for Applicant Id : {}", applicantId);

		
		Optional<NegotiationOfferEntity> negotiationDetails = negotiationOfferRepository
				.findByApplicant_Id(applicantId);

		log.info("Applicant Id : {}", applicantId);
		log.info("Negotiation Present : {}", negotiationDetails.isPresent());

		if (negotiationDetails.isEmpty()) {
			return ApiResponse.failure("Negotiation details not found");
		}

		NegotiationOfferEntity negotiation = negotiationDetails.get();

		Optional<OfferDetailsEntity> pendingOffer = offerDetailsRepository
				.findByJobApplication_IdAndReReleaseOfferIdIsNull(applicantId);
		if (pendingOffer.isEmpty()) {

			log.warn("Pending OfferDetails not found for Applicant Id : {}", applicantId);

			return ApiResponse.failure("Pending negotiation approval details not found");
		}

		OfferDetailsEntity offer = pendingOffer.get();

		log.info("Pending OfferDetails Id : {}", offer.getId());
		log.info("Applicant Id : {}", applicantId);
		log.info("Offer Status : {}", offer.getOfferStatus());
		log.info("Approver1 : {}", offer.getApprover1());
		log.info("Approver2 : {}", offer.getApprover2());
		log.info("Approver3 : {}", offer.getApprover3());


		NegotiationDetailsResponse response = new NegotiationDetailsResponse();

		BeanUtils.copyProperties(negotiation, response);

		response.setNegotiationId(negotiation.getId());
		response.setApplicantId(applicantId);

		if (negotiation.getOffer() != null) {
			response.setOfferReleasedOn(negotiation.getOffer().getCreatedDate());
		}

	

		OfferDetailsChildEntity child = offerDetailsChildRepository.findByOffer_Id(offer.getId()).orElse(null);

		String role1Name = null;
		String role2Name = null;
		String role3Name = null;

		log.info("Current Pending Offer Id : {}", offer.getId());

		if (child != null) {

			RolesEntity role1 = child.getRole1() != null ? rolesRepository.findByRoleId(child.getRole1()).orElse(null)
					: null;

			RolesEntity role2 = child.getRole2() != null ? rolesRepository.findByRoleId(child.getRole2()).orElse(null)
					: null;

			RolesEntity role3 = child.getRole3() != null ? rolesRepository.findByRoleId(child.getRole3()).orElse(null)
					: null;

			role1Name = role1 != null ? role1.getRoleName() : null;

			role2Name = role2 != null ? role2.getRoleName() : null;

			role3Name = role3 != null ? role3.getRoleName() : null;

			log.info("Role1 Id : {}", child.getRole1());
			log.info("Role2 Id : {}", child.getRole2());
			log.info("Role3 Id : {}", child.getRole3());

			log.info("Role1 Name : {}", role1Name);
			log.info("Role2 Name : {}", role2Name);
			log.info("Role3 Name : {}", role3Name);
		}

		// ---------------------------------------------------------
		// 5. Approval Stages
		// ---------------------------------------------------------

		List<NegotiationReviewResponse> stages = new ArrayList<>();

		// ---------------------------------------------------------
		// Stage 1
		// ---------------------------------------------------------

		NegotiationReviewResponse stage1 = new NegotiationReviewResponse();

		stage1.setStage("Approval Stage 1");
		stage1.setRole(role1Name);

		if (Boolean.TRUE.equals(offer.getApprover1())) {

			stage1.setStatus("APPROVED");
			stage1.setApprovedBy(offer.getApprover1By());
			stage1.setApprovedOn(offer.getDateOfApproval1());

		} else {

			stage1.setStatus("PENDING");
			stage1.setApprovedBy(null);
			stage1.setApprovedOn(null);
		}

		stages.add(stage1);

		// ---------------------------------------------------------
		// Stage 2
		// ---------------------------------------------------------

		NegotiationReviewResponse stage2 = new NegotiationReviewResponse();

		stage2.setStage("Approval Stage 2");
		stage2.setRole(role2Name);

		if (Boolean.TRUE.equals(offer.getApprover2())) {

			stage2.setStatus("APPROVED");
			stage2.setApprovedBy(offer.getApprover2By());
			stage2.setApprovedOn(offer.getDateOfApproval2());

		} else {

			stage2.setStatus("PENDING");
			stage2.setApprovedBy(null);
			stage2.setApprovedOn(null);
		}

		stages.add(stage2);

		// ---------------------------------------------------------
		// Stage 3
		// ---------------------------------------------------------

		NegotiationReviewResponse stage3 = new NegotiationReviewResponse();

		stage3.setStage("Approval Stage 3");
		stage3.setRole(role3Name);

		if (Boolean.TRUE.equals(offer.getApprover3())) {

			stage3.setStatus("APPROVED");
			stage3.setApprovedBy(offer.getApprover3By());
			stage3.setApprovedOn(offer.getDateOfApproval3());

		} else {

			stage3.setStatus("PENDING");
			stage3.setApprovedBy(null);
			stage3.setApprovedOn(null);
		}

		stages.add(stage3);

		response.setApprovalStages(stages);

		// ---------------------------------------------------------
		// 6. Candidate Details
		// ---------------------------------------------------------

		CandidateCreationDetailsEntity candidate = negotiation.getApplicant().getCandidate();

		if (candidate != null) {

			response.setCandidateId(candidate.getCandidateId());

			response.setCandidateName(candidate.getFirstName());

			response.setEmail(candidate.getEmail());
		}

		// ---------------------------------------------------------
		// 7. Job Details
		// ---------------------------------------------------------

		Integer jobId = negotiation.getApplicant().getJobId();

		response.setJobId(jobId);

		CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(jobId);

		if (job != null) {

			response.setJobTitle(job.getJobTitle());

			response.setSrId(job.getSrId());

			BudgetAndCompensationEntity budget = budgetAndCompensationRepository.findBySrId(job.getSrId()).orElse(null);

			if (budget != null) {

				response.setMinimumSalary(budget.getMinimumSalary());

				response.setMaximumSalary(budget.getMaximumSalary());

				response.setAnnualHiringCost(budget.getAnnualHiringCost());
			}
		}

		// ---------------------------------------------------------
		// 8. HR Recommendation Details
		// ---------------------------------------------------------

		response.setHrRecommendations(negotiation.getHrRecommendations());

		response.setHrRecommendedCtc(negotiation.getHrRecommendedCtc());

		response.setHrReason(negotiation.getHrReason());

		response.setRevisedJoiningDate(negotiation.getRevisedJoiningDate());

		// ---------------------------------------------------------
		// 9. Final Response
		// ---------------------------------------------------------

		return ApiResponse.success(ResponseCode.SUCCESS, "Success", response);
	}

	@Override
	@Transactional
	public ApiResponse<?> reviewNegotiationRequest(HrRecommendationRequest request) {

		log.info("OfferDetailsServiceImpl : Inside reviewNegotiationRequest");

		String authHeader = httpServletRequest.getHeader("Authorization");
		String token = authHeader.substring(7);

		Long loginUserId = jwtService.extractUserId(token);
		Long loginRoleId = jwtService.extractRoleId(token);

		Optional<NegotiationOfferEntity> negotiationOptional = negotiationOfferRepository
				.findByApplicant_Id(request.getApplicantId());

		if (negotiationOptional.isEmpty()) {
			ApiResponse.failure(ResponseCode.FAILURE, "Negotiation details not found");
		}

		NegotiationOfferEntity negotiation = negotiationOptional.get();

		negotiation.setHrRecommendedCtc(request.getHrRecommendedCtc());
		negotiation.setHrRecommendations(request.getHrRecommendations());
		negotiation.setHrReason(request.getHrReason());
		negotiation.setRevisedJoiningDate(request.getRevisedJoiningDate());
		negotiationOfferRepository.save(negotiation);

		Optional<OfferDetailsEntity> offerOptional = offerDetailsRepository
				.findByJobApplication_IdAndNegotiationTrue(request.getApplicantId());

		OfferDetailsEntity oldOffer = null;

		if (offerOptional.isPresent()) {

			oldOffer = offerOptional.get();

		} else {

			return ApiResponse.failure(ResponseCode.FAILURE, "Offer Details not found");

		}

		OfferDetailsEntity newOffer = new OfferDetailsEntity();

		newOffer.setJobApplication(oldOffer.getJobApplication());

		newOffer.setNoticePeriod(oldOffer.getNoticePeriod());

		newOffer.setProbationPeriod(oldOffer.getProbationPeriod());

		newOffer.setOfferStatus("Pending");

		newOffer.setInProgress(false);

		newOffer.setSubmitFinancialApproval(true);

		newOffer.setInterviewCompletionDate(oldOffer.getInterviewCompletionDate());

		newOffer.setInterviewCompletionStatus(oldOffer.getInterviewCompletionStatus());

		newOffer.setRecruitedBy(oldOffer.getRecruitedBy());

		newOffer.setOfferLetterTemplate(oldOffer.getOfferLetterTemplate());

		newOffer.setCreatedByRoleId(loginRoleId.intValue());

		newOffer.setSubmittedByUserId(loginUserId.intValue());

		newOffer.setCreatedDate(LocalDateTime.now());

		newOffer.setApprover1(false);
		newOffer.setApprover2(false);
		newOffer.setApprover3(false);

		newOffer.setOfferReleased(false);

		newOffer.setApprover1Role(oldOffer.getApprover1Role());
		newOffer.setApprover2Role(oldOffer.getApprover2Role());
		newOffer.setApprover3Role(oldOffer.getApprover3Role());

		OfferDetailsEntity savedOffer = offerDetailsRepository.save(newOffer);

		log.info("========== RE-RELEASE OFFER ==========");
		log.info("Old Offer ID       : {}", oldOffer.getId());
		log.info("New Offer ID       : {}", savedOffer.getId());

		negotiation.setOffer(savedOffer);
		negotiationOfferRepository.save(negotiation);

		log.info("Negotiation ID : {}", negotiation.getId());
		log.info("Negotiation Offer ID after update : {}", negotiation.getOffer().getId());

		oldOffer.setReReleaseOfferId(savedOffer.getId());

		oldOffer.setOfferStatus("Reviewed");

		offerDetailsRepository.save(oldOffer);

		processApprovalChain(newOffer.getJobApplication().getId());
		Map<Integer, List<String>> roleEmailMap = processApprovalChain(request.getApplicantId());

		log.info("Approval Chain Started Successfully : {}", roleEmailMap);

		return ApiResponse.success(ResponseCode.SUCCESS, "Success", "Review request submitted successfully");

	}

	@Override
	public void viewDocument(String filePath, String action, HttpServletResponse response) {

		log.info("Inside viewSupportingDocument");

		try (InputStream inputStream = minioClient
				.getObject(GetObjectArgs.builder().bucket("infospokejobapplicationsbucket").object(filePath).build())) {

			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

			response.setContentType("application/pdf");

			response.setHeader("Content-Disposition",
					("view".equalsIgnoreCase(action) ? "inline" : "attachment") + "; filename=\"" + fileName + "\"");

			IOUtils.copy(inputStream, response.getOutputStream());

			response.flushBuffer();

		} catch (Exception e) {
			log.error("Error while viewing supporting document", e);
			throw new RuntimeException("Unable to fetch supporting document from MinIO");
		}
	}

	@Override
	public ApiResponse<?> getReReleaseOfferDetails(Integer reReleaseOfferId) {

		log.info("OfferDetailsServiceImpl :: Inside getReReleaseOfferDetails");

		// 1. Fetch Offer Details using re_release_offer_id
		OfferDetailsEntity offer = offerDetailsRepository.findByReReleaseOfferId(reReleaseOfferId).orElse(null);

		if (offer == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Re-release offer not found");
		}

		ReReleaseOfferDetailsResponse response = new ReReleaseOfferDetailsResponse();

		// Offer Details
		response.setOfferId(offer.getReReleaseOfferId());

		response.setProbationPeriod(offer.getProbationPeriod());

		response.setTotalCtc(offer.getTotalCtc());

		// Job Application

		JobApplicationEntity application = offer.getJobApplication();

		if (application == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Job Application not found");
		}

		// Candidate Details

		CandidateCreationDetailsEntity candidate = application.getCandidate();

		if (candidate != null) {

			response.setCandidateId(candidate.getCandidateId());

			response.setCandidateName(candidate.getFirstName() + " " + candidate.getLastName());

			response.setEmail(candidate.getEmail());

		}
		// Job Details

		CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

		if (job != null) {

			response.setJobTitle(job.getJobTitle());

			response.setEmploymentType(job.getEmploymentType());

			response.setLocation(job.getLocation());

			DepartmentsEntity department = departmentsRepository.findById(job.getDepartmentId()).orElse(null);

			if (department != null) {

				response.setDepartmentName(department.getDepartmentName());

			}

			// Negotiation Details

			NegotiationOfferEntity negotiation = negotiationOfferRepository.findByOffer_Id(offer.getId()).orElse(null);

			if (negotiation != null) {

				response.setJoiningDate(negotiation.getRevisedJoiningDate());

				if (negotiation.getRevisedJoiningDate() != null) {

					response.setOfferValidity(negotiation.getRevisedJoiningDate().plusDays(6));

				}

				List<FinanceRecommendation> financeList = negotiation.getFinanceRecommendations();

				if (financeList == null) {
					financeList = new ArrayList<>();
				}

				BudgetAndCompensationEntity budget = budgetAndCompensationRepository.findBySrId(job.getSrId())
						.orElse(null);

				if (budget != null) {

					addFinanceComponent(financeList, "Basic Pay", budget.getProposedTotalCompensation() == null ? null
							: budget.getProposedTotalCompensation().longValue());

					addFinanceComponent(financeList, "Relocation Budget",
							budget.getRelocationBudgetAmount() == null ? null
									: budget.getRelocationBudgetAmount().longValue());

					addFinanceComponent(financeList, "Equity",
							budget.getEquityAmount() == null ? null : budget.getEquityAmount().longValue());

					addFinanceComponent(financeList, "Signing Bonus",
							budget.getSigningBonusAmount() == null ? null : budget.getSigningBonusAmount().longValue());

				}

				response.setFinanceRecommendations(financeList);

			}

		}

		log.info("OfferDetailsServiceImpl :: Exit getReReleaseOfferDetails");

		return ApiResponse.success(ResponseCode.SUCCESS, "Re-release Offer Details fetched successfully", response);

	}

	private void addFinanceComponent(List<FinanceRecommendation> list, String fieldName, Long amount) {

		if (amount == null) {
			return;
		}

		boolean exists = list.stream().anyMatch(item -> fieldName.equalsIgnoreCase(item.getFieldName()));

		if (!exists) {

			list.add(new FinanceRecommendation(fieldName, amount));

		}

	}

}
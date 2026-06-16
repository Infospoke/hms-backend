package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.dto.NotificationEvent;
import com.hms.service.entity.AIInterviewQuestionsEntity;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.ChildLinkCommentsEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.InterviewCandidateDetailsEntity;
import com.hms.service.entity.InterviewFeedbackEntity;
import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewQuestionsEntity;
import com.hms.service.entity.InterviewRoundEntity;
import com.hms.service.entity.InterviewScheduleEntity;
import com.hms.service.entity.InterviewSessionEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.ResumeAnalysisUpdateEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.AInterviewQuestionsRepository;
import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.ChildLinkCommentsRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.repository.InterviewCandidateDetailsRepository;
import com.hms.service.repository.InterviewFeedbackRepository;
import com.hms.service.repository.InterviewPlanRepository;
import com.hms.service.repository.InterviewRoundRepository;
import com.hms.service.repository.InterviewScheduleRepository;
import com.hms.service.repository.InterviewSessionRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.ResumeAnalysisUpdateRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.InterviewFeedbackRequest;
import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.InterviewRoundRequest;
import com.hms.service.request.InterviewScheduleRequest;
import com.hms.service.request.LevelConfig;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateInterviewPlanRequest;
import com.hms.service.response.CommentTimelineResponse;
import com.hms.service.response.InterviewApplicantDetailsResponse;
import com.hms.service.response.InterviewDetailsResponse;
import com.hms.service.response.InterviewExperienceResponse;
import com.hms.service.response.InterviewPlanResponse;
import com.hms.service.response.InterviewProjectResponse;
import com.hms.service.response.InterviewRoundsResponse;
import com.hms.service.service.IInterviewPlanService;
import com.hms.service.service.INotificationService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InterviewPlanServiceImpl implements IInterviewPlanService {

	@Autowired
	private InterviewPlanRepository interviewPlanRepository;

	@Autowired
	private ChildLinkCommentsRepository childLinkCommentsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AssignRolesRepository assignRolesRepository;

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private INotificationService notificationService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private ApprovalChainRepository approvalChainRepository;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private InterviewRoundRepository interviewRoundRepository;

	@Autowired
	private InterviewFeedbackRepository interviewFeedbackRepository;

	@Autowired
	private InterviewScheduleRepository interviewScheduleRepository;

	@Autowired
	private InterviewCandidateDetailsRepository interviewCandidateDetailsRepository;

	
	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;
	
	@Autowired
	private ResumeAnalysisUpdateRepository resumeAnalysisUpdateRepository;
	
	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private JobApplicationRepository jobApplicationRepository;
	
	@Autowired
	private InterviewSessionRepository interviewSessionRepository;
	
	@Autowired
	private AInterviewQuestionsRepository aiInterviewQuestionsRepository;

	@Override
	public ApiResponse<?> createInterviewPlan(InterviewPlanRequest request, HttpServletRequest httpRequest) {

		log.info("InterviewPlanServiceImpl :: Inside the createInterviewPlan method");

		try {

			String authHeader = httpRequest.getHeader("Authorization");
			String token = authHeader.substring(7);
			String username = jwtService.extractUsernameFromClaims(token);
			Long userId = jwtService.extractUserId(token);

			InterviewPlanEntity entity = new InterviewPlanEntity();
			entity.setPlanName(request.getPlanName());
			entity.setDescription(request.getDescription());
			entity.setApprovalStatus("INPROGRESS");
			entity.setRequestType("PLAN CREATED");
			entity.setStatus(request.getStatus());
			entity.setCreatedBy(username);
			entity.setUserId(userId);
			entity.setCreatedOn(LocalDateTime.now());

			List<InterviewRoundEntity> roundEntities = new ArrayList<>();

			for (InterviewRoundRequest round : request.getRounds()) {

				InterviewRoundEntity roundEntity = new InterviewRoundEntity();
				roundEntity.setRoundOrder(round.getRoundOrder());
				roundEntity.setStageName(round.getStageName());
				roundEntity.setStageType(round.getStageType());
				roundEntity.setInterviewMode(round.getInterviewMode());
				roundEntity.setMandatory(round.getMandatory());
				roundEntity.setInterviewPlan(entity);
				roundEntities.add(roundEntity);
			}

			entity.setRounds(roundEntities);

			interviewPlanRepository.save(entity);

			return ApiResponse.success(ResponseCode.SUCCESS, "success", "Interview Plan Created Successfully");

		} catch (Exception e) {

			log.error("Error while creating interview plan : {}", e.getMessage());

			return ApiResponse.failure(ResponseCode.FAILURE, "Failed To Create Interview Plan");
		}
	}

	private void sendWorkflowNotification(String srId, String type, String makerMessage, String department,

			String makerEmail, String makerRole, Integer makerRoleId, String makerTitle, String makerBody,

			String checkerRole, String checkerMessage, String checkerTitle, String checkerBody,

			Map<Integer, List<String>> roleEmailMap) {

		NotificationEvent event = new NotificationEvent();

		event.setProcessId(srId);
		event.setType(type);

		// MAKER

		event.setMakerEmailAddress(makerEmail);
		event.setMakerRoleName(makerRole);
		event.setMakerNotificationTitle(makerTitle);
		event.setMakerEmailBody(makerBody);
		event.setMakerRoleId(makerRoleId);

		// CHECKER

		event.setCheckerRoleName(checkerRole);
		event.setCheckerNotificationTitle(checkerTitle);
		event.setCheckerEmailBody(checkerBody);

		event.setDeptName(department);

		event.setMakerMessage(makerMessage);
		event.setCheckerMessage(checkerMessage);

		event.setRoleEmailMap(roleEmailMap);

		log.info("Maker Email = {}", makerEmail);
		log.info("Maker Role = {}", makerRole);
		log.info("Maker Role Id = {}", makerRoleId);

		log.info("Checker Role = {}", checkerRole);
		log.info("Role Email Map = {}", roleEmailMap);

		roleEmailMap.forEach((roleId, emails) -> log.info("RoleId {} -> Emails {}", roleId, emails));

		notificationService.callNotification(event);
	}

	@Override
	public ApiResponse<?> updateInterviewPlan(UpdateInterviewPlanRequest request, HttpServletRequest httpRequest) {

		log.info("InterviewPlanServiceImpl :: Inside updateInterviewPlan");

		// VALIDATIONS

		if (request.getId() == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Interview Plan Id is required");
		}

		if (request.getApproval() == null && request.getStatus() == null && request.getActiveApproval() == null
				&& request.getDeactiveApproval() == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "At least one action is required");
		}

		if (request.getApproval() != null
				&& (request.getComments() == null || request.getComments().trim().isEmpty())) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Comments are mandatory");
		}

		if (request.getStatus() != null
				&& (request.getDescription() == null || request.getDescription().trim().isEmpty())) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Description is mandatory");
		}

		if (request.getDeactiveApproval() != null
				&& (request.getComments() == null || request.getComments().trim().isEmpty())) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Comments are mandatory for deactivation approval");
		}

		if (request.getActiveApproval() != null
				&& (request.getComments() == null || request.getComments().trim().isEmpty())) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Comments are mandatory for activation approval");
		}

		// FETCH ENTITY

		InterviewPlanEntity interviewPlanEntity = interviewPlanRepository.findById(request.getId())
				.orElseThrow(() -> new RuntimeException("Interview Plan not found"));

		// CHILD COMMENTS

		ChildLinkCommentsEntity childLinkCommentsEntity = new ChildLinkCommentsEntity();

		// JWT DETAILS

		String authHeader = httpRequest.getHeader("Authorization");

		String token = authHeader.substring(7);

		String userName = jwtService.extractUsernameFromClaims(token);

		Long userId = jwtService.extractUserId(token);

		String email = jwtService.extractUsername(token);

		String approverEmail = userRepository.findByUserId(userId).map(UserEntity::getEmail).orElse(null);

		String roleName = jwtService.extractRole(token);

		log.info("Logged In Role : {}", roleName);

		// COMMON DETAILS

		String planName = interviewPlanEntity.getPlanName();

		String description = interviewPlanEntity.getDescription();

		String createdBy = interviewPlanEntity.getCreatedBy();

		Integer planId = interviewPlanEntity.getId();

		Integer makerUserId = interviewPlanEntity.getUserId().intValue();

		Integer makerRoleId = assignRolesRepository.findByUserId(makerUserId).get().getRoleId();

		String makerRoleName = rolesRepository.findByRoleId(makerRoleId).get().getRoleName();

		UserEntity creator = userRepository.findByUsername(createdBy);

		String creatorEmail = creator.getEmail();

		// APPROVE / REJECT

		if (request.getApproval() != null) {

			if (!"Hiring Manager".equalsIgnoreCase(roleName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only Hiring Manager can approve/reject");
			}

			String approval = request.getApproval().trim().toUpperCase();

			// APPROVE

			if ("APPROVED".equals(approval)) {

				interviewPlanEntity.setStatus("ACTIVE");
				interviewPlanEntity.setApprovalStatus("APPROVED");
				childLinkCommentsEntity.setPlanId(planId);
				childLinkCommentsEntity.setAction("APPROVE");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				// MAIL & NOTIFICATION

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer checkerRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(checkerRoleId, List.of(approverEmail));

				sendWorkflowNotification(

						interviewPlanEntity.getId().toString(),

						"INTERVIEW PLAN WORKFLOW",

						"Your Interview Plan has been approved by the Hiring Manager.",

						planName,

						creatorEmail,

						makerRoleName,

						makerRoleId,

						Constants.INTERVIEW_PLAN_APPROVED_MAIL_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_APPROVED_MAKER_BODY, planId, planName, userName,
								LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleName,

						"You have successfully approved the Interview Plan.",

						Constants.INTERVIEW_PLAN_APPROVER_CONFIRMATION_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_APPROVED_CHECKER_BODY, planId, planName, userName,
								LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleEmailMap);
			}

			// REJECT

			else if ("REJECTED".equals(approval)) {

				interviewPlanEntity.setApprovalStatus("REJECTED");
				childLinkCommentsEntity.setPlanId(planId);
				childLinkCommentsEntity.setAction("REJECT");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				// MAIL & NOTIFICATION

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer checkerRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(checkerRoleId, List.of(approverEmail));

				sendWorkflowNotification(

						interviewPlanEntity.getId().toString(),

						"INTERVIEW PLAN WORKFLOW",

						"Your Interview Plan has been rejected by the Hiring Manager.",

						planName,

						creatorEmail,

						makerRoleName,

						makerRoleId,

						Constants.INTERVIEW_PLAN_REJECTED_MAIL_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_REJECTED_MAKER_BODY, createdBy, planId, planName,
								description, request.getComments(), LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleName,

						"You have successfully rejected the Interview Plan.",

						Constants.INTERVIEW_PLAN_REJECTION_CONFIRMATION_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_REJECTED_CHECKER_BODY, userName, planId, planName,
								description, LocalDateTime.now(ZoneId.of("Asia/Kolkata")), request.getComments()),

						roleEmailMap);
			}

			else {

				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid approval value");
			}
		}

		// DEACTIVATION REQUEST

		if (request.getStatus() != null && "DEACTIVE".equalsIgnoreCase(request.getStatus())) {

			if (!"Recruiting Operations".equalsIgnoreCase(roleName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only Recruiting Operations can request deactivation");
			}

			interviewPlanEntity.setApprovalStatus("INPROGRESS");
			interviewPlanEntity.setRequestType("PLAN DEACTIVE");
			interviewPlanEntity.setDeactiveApproval(false);
			childLinkCommentsEntity.setPlanId(planId);
			childLinkCommentsEntity.setAction("DEACTIVE");
			childLinkCommentsEntity.setDescription(request.getDescription());
			childLinkCommentsEntity.setCreatedBy(userName);
			childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

			// MAIL & NOTIFICATION
			// mail sent to all hiring manager

			Map<Integer, List<String>> roleEmailMap = new HashMap<>();

			Integer checkerRoleId = rolesRepository.findByRoleNameIgnoreCase("Hiring Manager").getRoleId();

			List<Integer> userIds = assignRolesRepository.findByRoleId(checkerRoleId).stream()
					.map(AssignRolesEntity::getUserId).toList();

			List<String> checkerEmails = userRepository.findByUserIdIn(userIds).stream().map(UserEntity::getEmail)
					.filter(Objects::nonNull).distinct().toList();

			roleEmailMap.put(checkerRoleId, checkerEmails);

			sendWorkflowNotification(

					interviewPlanEntity.getId().toString(),

					"INTERVIEW PLAN WORKFLOW",

					"Interview Plan deactivation request submitted",

					planName,

					creatorEmail,

					makerRoleName,

					makerRoleId,

					Constants.INTERVIEW_PLAN_DEACTIVATION_REQUEST_MAIL_SUBJECT,

					String.format(Constants.INTERVIEW_PLAN_DEACTIVATION_REQUEST_MAKER_BODY, planId, planName,
							request.getDescription(), createdBy, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

					"Hiring Manager",

					"Interview Plan deactivation approval pending",

					Constants.INTERVIEW_PLAN_DEACTIVATION_REQUEST_MAIL_SUBJECT,

					String.format(Constants.INTERVIEW_PLAN_DEACTIVATION_REQUEST_CHECKER_BODY, planId, planName,
							request.getDescription(), createdBy, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

					roleEmailMap);
		}

		// DEACTIVATION APPROVAL

		if (request.getDeactiveApproval() != null) {

			if (!"Hiring Manager".equalsIgnoreCase(roleName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only Hiring Manager can process deactivation");
			}

			// APPROVED

			if (Boolean.TRUE.equals(request.getDeactiveApproval())) {

				interviewPlanEntity.setStatus("DEACTIVE");
				interviewPlanEntity.setApprovalStatus("APPROVED");
				interviewPlanEntity.setDeactiveApproval(true);
				interviewPlanEntity.setActiveApproval(false);
				childLinkCommentsEntity.setPlanId(planId);
				childLinkCommentsEntity.setAction("APPROVE");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				// MAIL&NOTIFICATION

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer checkerRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(checkerRoleId, List.of(email));

				sendWorkflowNotification(

						interviewPlanEntity.getId().toString(),

						"INTERVIEW PLAN WORKFLOW",

						"Your Interview Plan has been deactivated.",

						planName,

						creatorEmail,

						makerRoleName,

						makerRoleId,

						Constants.INTERVIEW_PLAN_DEACTIVATED_MAIL_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_DEACTIVATED_MAKER_BODY, createdBy, planId, planName,
								description, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleName,

						"You approved deactivation.",

						Constants.INTERVIEW_PLAN_DEACTIVATED_MAIL_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_DEACTIVATED_CHECKER_BODY, userName, planId, planName,
								description, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleEmailMap);
			}

			// REJECTED

			else {

				interviewPlanEntity.setApprovalStatus("REJECTED");
				interviewPlanEntity.setDeactiveApproval(false);
				childLinkCommentsEntity.setPlanId(planId);
				childLinkCommentsEntity.setAction("REJECT");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer checkerRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(checkerRoleId, List.of(email));

				sendWorkflowNotification(

						interviewPlanEntity.getId().toString(),

						"INTERVIEW PLAN WORKFLOW",

						"Your Interview Plan deactivation request has been rejected.",

						planName,

						creatorEmail,

						makerRoleName,

						makerRoleId,

						Constants.INTERVIEW_PLAN_DEACTIVATION_REJECTED_MAIL_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_DEACTIVATION_REJECTED_MAKER_BODY, createdBy, planId,
								planName, description, request.getComments(),
								LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleName,

						"You rejected deactivation.",

						Constants.INTERVIEW_PLAN_DEACTIVATION_REJECTED_MAIL_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_DEACTIVATION_REJECTED_CHECKER_BODY, userName, planId,
								planName, description, request.getComments(),
								LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleEmailMap);
			}
		}

		// ACTIVATION REQUEST

		if (request.getStatus() != null && "ACTIVE".equalsIgnoreCase(request.getStatus())) {

			if (!"Recruiting Operations".equalsIgnoreCase(roleName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only Recruiting Operations can request activation");
			}

			interviewPlanEntity.setApprovalStatus("INPROGRESS");
			interviewPlanEntity.setRequestType("PLAN ACTIVE");
			childLinkCommentsEntity.setPlanId(planId);
			childLinkCommentsEntity.setAction("ACTIVE");
			childLinkCommentsEntity.setDescription(request.getDescription());
			childLinkCommentsEntity.setCreatedBy(userName);
			childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

			// MAIL&NOTIFICATION
			// mail sent to all hiring manager

			Map<Integer, List<String>> roleEmailMap = new HashMap<>();

			Integer checkerRoleId = rolesRepository.findByRoleNameIgnoreCase("Hiring Manager").getRoleId();

			List<Integer> userIds = assignRolesRepository.findByRoleId(checkerRoleId).stream()
					.map(AssignRolesEntity::getUserId).toList();

			List<String> checkerEmails = userRepository.findByUserIdIn(userIds).stream().map(UserEntity::getEmail)
					.filter(Objects::nonNull).distinct().toList();

			roleEmailMap.put(checkerRoleId, checkerEmails);

			sendWorkflowNotification(

					interviewPlanEntity.getId().toString(),

					"INTERVIEW PLAN WORKFLOW",

					"Interview Plan activation request submitted",

					planName,

					creatorEmail,

					makerRoleName,

					makerRoleId,

					Constants.INTERVIEW_PLAN_ACTIVATION_REQUEST_MAIL_SUBJECT,

					String.format(Constants.INTERVIEW_PLAN_ACTIVATION_REQUEST_MAKER_BODY, planId, planName,
							request.getDescription(), createdBy, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

					"Hiring Manager",

					"Interview Plan activation approval pending",

					Constants.INTERVIEW_PLAN_ACTIVATION_REQUEST_MAIL_SUBJECT,

					String.format(Constants.INTERVIEW_PLAN_ACTIVATION_REQUEST_CHECKER_BODY, planId, planName,
							request.getDescription(), createdBy, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

					roleEmailMap);
		}

		// ACTIVATION APPROVAL

		if (request.getActiveApproval() != null) {

			if (!"Hiring Manager".equalsIgnoreCase(roleName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only Hiring Manager can process activation");
			}

			// APPROVED

			if (Boolean.TRUE.equals(request.getActiveApproval())) {

				interviewPlanEntity.setStatus("ACTIVE");
				interviewPlanEntity.setApprovalStatus("APPROVED");
				interviewPlanEntity.setActiveApproval(true);
				interviewPlanEntity.setDeactiveApproval(false);
				childLinkCommentsEntity.setPlanId(planId);
				childLinkCommentsEntity.setAction("APPROVE");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				// MAIL&NOTIFICATION

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer checkerRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(checkerRoleId, List.of(email));

				sendWorkflowNotification(

						interviewPlanEntity.getId().toString(),

						"INTERVIEW PLAN WORKFLOW",

						"Your Interview Plan has been activated.",

						planName,

						creatorEmail,

						makerRoleName,

						makerRoleId,

						Constants.INTERVIEW_PLAN_ACTIVATED_MAIL_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_ACTIVATED_MAKER_BODY, createdBy, planId, planName,
								description, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleName,

						"You approved activation.",

						Constants.INTERVIEW_PLAN_ACTIVATED_MAIL_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_ACTIVATED_CHECKER_BODY, userName, planId, planName,
								description, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleEmailMap);
			}

			// REJECTED

			else {

				interviewPlanEntity.setApprovalStatus("REJECTED");
				interviewPlanEntity.setActiveApproval(false);
				childLinkCommentsEntity.setPlanId(planId);
				childLinkCommentsEntity.setAction("REJECT");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer checkerRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(checkerRoleId, List.of(email));

				sendWorkflowNotification(

						interviewPlanEntity.getId().toString(),

						"INTERVIEW PLAN WORKFLOW",

						"Your Interview Plan activation request has been rejected.",

						planName,

						creatorEmail,

						makerRoleName,

						makerRoleId,

						Constants.INTERVIEW_PLAN_ACTIVATION_REJECTED_MAIL_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_ACTIVATION_REJECTED_MAKER_BODY, createdBy, planId,
								planName, description, request.getComments(),
								LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleName,

						"You rejected activation.",

						Constants.INTERVIEW_PLAN_ACTIVATION_REJECTED_MAIL_SUBJECT,

						String.format(Constants.INTERVIEW_PLAN_ACTIVATION_REJECTED_CHECKER_BODY, userName, planId,
								planName, description, request.getComments(),
								LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleEmailMap);
			}
		}

		interviewPlanEntity.setUpdatedBy(userName);
		interviewPlanEntity.setUpdatedAt(LocalDateTime.now());
		interviewPlanRepository.save(interviewPlanEntity);
		childLinkCommentsRepository.save(childLinkCommentsEntity);

		log.info("InterviewPlanServiceImpl :: Exit from updateInterviewPlans");
		return ApiResponse.success("Interview Plan Updated Successfully");
	}

	@Override
	public ApiResponse<?> getInterviewPlans(SpecificationFilterRequest request) {

		log.info("InterviewPlanServiceImpl :: Inside getInterviewPlans");

		if (request.getPage() == null || request.getSize() == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("page and size must be provided"));
		}

		if (request.getPage() < 0 || request.getSize() <= 0) {

			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Invalid page or size values"));
		}

		Sort sort = Sort.by(

				"DESC".equalsIgnoreCase(request.getDirection())

						? Sort.Direction.DESC
						: Sort.Direction.ASC,

				request.getSortBy() != null ? request.getSortBy() : "createdOn");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Specification<InterviewPlanEntity> spec = request.buildInterviewPlanSpecification();

		Page<InterviewPlanEntity> pageResult = interviewPlanRepository.findAll(spec, pageable);

		List<Map<String, Object>> plans = pageResult.getContent().stream().map(plan -> {

			Map<String, Object> map = new LinkedHashMap<>();

			map.put("id", plan.getId());
			map.put("planName", plan.getPlanName());
			map.put("description", plan.getDescription());
			map.put("status", plan.getStatus());
			map.put("approvalStatus", plan.getApprovalStatus());
			map.put("createdBy", plan.getCreatedBy());
			map.put("createdOn", plan.getCreatedOn());
			map.put("rounds", plan.getRounds() != null ? plan.getRounds().size() : 0);

			return map;

		}).toList();
		Map<String, Long> counts = request.buildInterviewPlanCounts(interviewPlanRepository);

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("interviewPlans", plans);

		response.put("counts", counts);

		response.put("currentPage", pageResult.getNumber());

		response.put("totalPages", pageResult.getTotalPages());

		response.put("totalElements", pageResult.getTotalElements());

		log.info("InterviewPlanServiceImpl :: Exit getInterviewPlans");

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview plans fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getInterviewPlanCounts() {

		log.info("InterviewPlanServiceImpl :: Inside getInterviewPlanCounts");

		long allPlans = interviewPlanRepository.count();

		long activePlans = interviewPlanRepository.countByStatus("ACTIVE");

		long deactivePlans = interviewPlanRepository.countByStatus("DEACTIVE");

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("allPlans", allPlans);

		response.put("activePlans", activePlans);

		response.put("deactivePlans", deactivePlans);

		log.info("InterviewPlanServiceImpl :: Exit getInterviewPlanCounts");

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview plan counts fetched successfully", response);
	}

	@Override

	public ApiResponse<?> getInterviewPlanDetailsById(Integer id) {

		log.info("InterviewPlanServiceImpl :: getInterviewPlanDetailsById");

		Optional<InterviewPlanEntity> optionalPlan = interviewPlanRepository.findById(id);

		if (optionalPlan.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Failed To fetch details");
		}

		InterviewPlanEntity interviewPlan = optionalPlan.get();

		List<InterviewRoundEntity> rounds = interviewRoundRepository.findByInterviewPlan_IdOrderByRoundOrderAsc(id);

		List<InterviewRoundsResponse> roundsResponse = rounds.stream()
				.map(round -> new InterviewRoundsResponse(round.getRoundOrder(), round.getStageName(),
						round.getStageType(), round.getInterviewMode(), round.getMandatory()))
				.toList();

		List<ChildLinkCommentsEntity> comments = childLinkCommentsRepository.findByPlanIdOrderByCreatedAtAsc(id);

		List<CommentTimelineResponse> timelineResponse = comments.stream()
				.map(comment -> new CommentTimelineResponse(comment.getAction(), comment.getComments(),
						comment.getDescription(), comment.getCreatedBy(), comment.getCreatedAt()))
				.toList();

		// final response
		InterviewPlanResponse response = new InterviewPlanResponse(interviewPlan.getPlanName(),
				interviewPlan.getDescription(), interviewPlan.getStatus(), interviewPlan.getCreatedBy(),
				interviewPlan.getCreatedOn(), roundsResponse, timelineResponse);

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	public ApiResponse<?> getInterviewPlanApprovals(SpecificationFilterRequest request) {

		log.info("InterviewPlanServiceImpl :: Inside getInterviewPlanApprovals");

		try {

			String authHeader = httpServletRequest.getHeader("Authorization");

			Long roleId = null;

			if (authHeader != null && authHeader.startsWith("Bearer ")) {

				String token = authHeader.substring(7);

				roleId = jwtService.extractRoleId(token);
			}

			if (roleId == null) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Role not found in token");
			}

			Integer functionalityId = functionalityRepository.findByFunctionalityName("Interview Plan").get().getId();

			if (functionalityId == null) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Interview Plan functionality not configured");
			}

			ApprovalChainEntity approvalChain = approvalChainRepository.findByFunctionality(functionalityId);

			if (approvalChain == null) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Approval chain not found");
			}

			boolean roleExists = false;

			for (LevelConfig level : approvalChain.getLevelConfig()) {

				if (level.getRoleId() != null && level.getRoleId().longValue() == roleId.longValue()) {

					roleExists = true;
					break;
				}
			}

			if (!roleExists) {

				return ApiResponse.failure(ResponseCode.FAILURE, "You are not authorized");
			}

			Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
					Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

			Page<InterviewPlanEntity> page = interviewPlanRepository
					.findAll(request.buildInterviewPlanApprovalSpecification(), pageable);

			List<Map<String, Object>> content = page.getContent().stream().map(plan -> {

				Map<String, Object> map = new LinkedHashMap<>();

				map.put("id", plan.getId());
				map.put("planName", plan.getPlanName());
				map.put("requestedBy", plan.getCreatedBy());
				map.put("requestedOn", plan.getCreatedOn());
				map.put("status", plan.getApprovalStatus());
				map.put("requestedRoleBy", plan.getRoleName());
				map.put("requestType", plan.getRequestType());
				map.put("rounds", plan.getRounds() == null ? 0 : plan.getRounds().size());

				return map;
			}).toList();

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("currentPage", page.getNumber());
			response.put("totalPages", page.getTotalPages());
			response.put("size", page.getSize());
			response.put("totalElements", page.getTotalElements());
			response.put("content", content);
			log.info("InterviewPlanServiceImpl :: Exit getInterviewPlanApprovals");

			return ApiResponse.success(ResponseCode.SUCCESS, "Interview Plans fetched successfully", response);
		} catch (Exception e) {

			e.printStackTrace();

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}

	}

	@Override

	public ApiResponse<?> interviewFeedback(InterviewFeedbackRequest request) {
		log.info("InterviewPlanServiceImpl :: Inside interviewFeedback");
		String authHeader = httpServletRequest.getHeader("Authorization");

		Long userId = null;
		String username = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token);
			username = jwtService.extractUsernameFromClaims(token);
		}

		InterviewFeedbackEntity interviewFeedbackEntity = new InterviewFeedbackEntity();
		interviewFeedbackEntity.setApplicantId(request.getApplicantId());
		interviewFeedbackEntity.setOverallRating(request.getOverallRating());
		interviewFeedbackEntity.setTechnicalKnowledge(request.getTechnicalKnowledge());
		interviewFeedbackEntity.setAnalyticalThinking(request.getAnalyticalThinking());
		interviewFeedbackEntity.setCommunication(request.getCommunication());
		interviewFeedbackEntity.setAreasOfImprovemnets(request.getAreasOfImprovemnets());
		interviewFeedbackEntity.setCulturalFit(request.getCulturalFit());
		interviewFeedbackEntity.setProblemSolving(request.getProblemSolving());
		interviewFeedbackEntity.setStrengths(request.getStrengths());
		interviewFeedbackEntity.setAdditionalComments(request.getAdditionalComments());
		interviewFeedbackEntity.setDecision(request.getDecision());
		interviewFeedbackEntity.setSubmittedOn(LocalDateTime.now());
		interviewFeedbackEntity.setSubmittedBy(username);
		interviewFeedbackEntity.setUserId(userId.intValue());

		interviewFeedbackRepository.save(interviewFeedbackEntity);

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview Feedback Submitted successfully");
	}

	public ApiResponse<?> scheduleInterview(InterviewScheduleRequest request) {
		log.info("InterviewPlanServiceImpl:Inside the scheduleInterview method");
		InterviewScheduleEntity entity = new InterviewScheduleEntity();

		String authHeader = httpServletRequest.getHeader("Authorization");
		String token = authHeader.substring(7);
		String userName = jwtService.extractUsernameFromClaims(token);
		Long userId = jwtService.extractUserId(token);

		entity.setApplicantId(request.getApplicantId());
		entity.setRoundType(request.getRoundType());
		entity.setInterviewDate(request.getInterviewDate());
		entity.setStartTime(request.getStartTime());
		entity.setEndTime(request.getEndTime());
		entity.setInterviewType(request.getInterviewType());
		entity.setUserId(userId.intValue());
		if (request.getInterviewType().equalsIgnoreCase("Online Interview")) {
			entity.setMeetingLink(request.getMeetingLink());
		} else if (request.getInterviewType().equalsIgnoreCase("Offline Interview")) {
			entity.setVenueDetails(request.getVenueDetails());
		}
		entity.setCreatedBy(userName);
		entity.setCreatedOn(LocalDateTime.now());

		interviewScheduleRepository.save(entity);

		log.info("InterviewPlanServiceImpl:Exit from  the scheduleInterview method");

		return ApiResponse.success(ResponseCode.SUCCESS, "Success", "Interview Scheduled Sucessfully");

	}

	@Override
	public ApiResponse<?> getTodayInterviews(SpecificationFilterRequest request) {

		String authHeader = httpServletRequest.getHeader("Authorization");
		String token = authHeader.substring(7);

		Long userId = jwtService.extractUserId(token);

		Integer userIdFromToken = userId.intValue();

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

		Page<InterviewCandidateDetailsEntity> interviews = interviewCandidateDetailsRepository
				.findAll(request.buildTodayInterviewSpecification(userIdFromToken), pageable);

		Map<String, Object> response = new HashMap<>();
		response.put("content", interviews.getContent());
		response.put("currentPage", interviews.getNumber());
		response.put("totalPages", interviews.getTotalPages());
		response.put("totalElements", interviews.getTotalElements());
		response.put("size", interviews.getSize());
		return ApiResponse.success(ResponseCode.SUCCESS, "Success", response);

	}

	@Override
	public ApiResponse<?> getFeedbackList(SpecificationFilterRequest request) {

		try {

			String authHeader = httpServletRequest.getHeader("Authorization");

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Authorization token is missing");
			}

			String token = authHeader.substring(7);

			Long userId = jwtService.extractUserId(token);

			Integer userIdFromToken = userId.intValue();

			Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
					Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

			Specification<InterviewCandidateDetailsEntity> specification = request
					.buildTodayInterviewSpecification(userIdFromToken);

			Page<InterviewCandidateDetailsEntity> page = interviewCandidateDetailsRepository.findAll(specification,
					pageable);

			List<Map<String, Object>> content = page.getContent().stream()
					.filter(candidate -> Objects.equals(candidate.getUserId(), userIdFromToken)).map(candidate -> {

						Map<String, Object> map = new LinkedHashMap<>();

						map.put("candidateName", candidate.getCanidateName());

						map.put("jobTitle", candidate.getJobTitle());

						map.put("interviewDate", null);

						map.put("round", candidate.getRound());

						map.put("priority", null);

						return map;
					}).toList();

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("content", content);

			response.put("currentPage", page.getNumber());

			response.put("totalPages", page.getTotalPages());

			response.put("totalElements", page.getTotalElements());

			response.put("size", page.getSize());

			return ApiResponse.success(ResponseCode.SUCCESS, "Feedback list fetched successfully", response);

		} catch (Exception e) {

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}
	
	@Override
	public ApiResponse<?> getInterviewDetails(Integer applicationId) {

	    log.info("InterviewPlanServiceImpl :: getInterviewDetails");

	    Optional<ResumeAnalysisUpdateEntity> optional =
	            resumeAnalysisUpdateRepository.findByApplicationId(applicationId);

	    if (optional.isEmpty()) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "Interview details not found");
	    }
	    

	    ResumeAnalysisUpdateEntity entity = optional.get();
	    
	    Integer jobId=entity.getJobId();
	    
	    Optional<CreateJobDetailsEntity> jobDetailsEnity=createJobDetailsRepository.findById(jobId);
	    CreateJobDetailsEntity createJobDetailsEntity=jobDetailsEnity.get();
	    Integer deptId=createJobDetailsEntity.getDepartmentId();
	    
	    InterviewDetailsResponse response = new InterviewDetailsResponse();
        String department=departmentsRepository.findById(deptId).get().getDepartmentName();
        
	    response.setCandidateName(entity.getName());
	    response.setJobTitle(createJobDetailsEntity.getJobTitle());
	    response.setDepartment(department);
	    response.setInterviewMode(null);
	    response.setInterviewRound(null);
	    response.setInterviewType(null);
	    response.setInterviewFlatform(null);
	    response.setScheduleTime(null);
	    response.setDuration(null);
	    response.setDesignation(entity.getDesignation());
	    response.setTotalExperience(entity.getTotalExperience());
	    response.setCurrentCompany(entity.getCurrentCompany());

	    // Experience Details
	    List<InterviewExperienceResponse> experienceResponses =
	            new ArrayList<>();

	    if (entity.getExperienceDetails() != null) {

	        for (Map<String, Object> exp : entity.getExperienceDetails()) {

	            InterviewExperienceResponse experience =
	                    new InterviewExperienceResponse();

	            experience.setCompany(
	                    String.valueOf(exp.get("company")));

	            experience.setRole(
	                    String.valueOf(exp.get("job_title")));

	            experience.setStartDate(
	                    String.valueOf(exp.get("start_date")));

	            experience.setEndDate(
	                    String.valueOf(exp.get("end_date")));

	            experienceResponses.add(experience);
	        }
	    }

	    response.setExperienceDetails(experienceResponses);

	    // Project Details
	    List<InterviewProjectResponse> projectResponses =
	            new ArrayList<>();

	    if (entity.getProjects() != null) {

	        for (Map<String, Object> project : entity.getProjects()) {

	            InterviewProjectResponse projectResponse =
	                    new InterviewProjectResponse();

	            projectResponse.setProjectTitle(
	                    String.valueOf(project.get("project_title")));

	            projectResponse.setTechStack(
	                    (List<String>) project.get("tech_stack"));

	            projectResponse.setDescription(
	                    (List<String>) project.get("description"));

	            projectResponses.add(projectResponse);
	        }
	    }

	    response.setProjectDetails(projectResponses);

	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            "Interview details fetched successfully",
	            response);
	}
	
	@Override
	public ApiResponse<?> getScheduleList(SpecificationFilterRequest request) {

		try {

			String authHeader = httpServletRequest.getHeader("Authorization");

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Authorization token is missing");
			}

			String token = authHeader.substring(7);

			Long userId = jwtService.extractUserId(token);

			Integer userIdFromToken = userId.intValue();

			Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
					Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

			Specification<InterviewCandidateDetailsEntity> specification = request
					.buildTodayInterviewSpecification(userIdFromToken);

			Page<InterviewCandidateDetailsEntity> page = interviewCandidateDetailsRepository.findAll(specification,
					pageable);

			List<Map<String, Object>> content = page.getContent().stream()
					.filter(candidate -> Objects.equals(candidate.getUserId(), userIdFromToken)).map(candidate -> {

						Map<String, Object> map = new LinkedHashMap<>();

						map.put("candidateName", candidate.getCanidateName());

						map.put("jobTitle", candidate.getJobTitle());

						map.put("interviewDate", null);

						map.put("round", candidate.getRound());

						map.put("priority", null);

						return map;
					}).toList();

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("content", content);

			response.put("currentPage", page.getNumber());

			response.put("totalPages", page.getTotalPages());

			response.put("totalElements", page.getTotalElements());

			response.put("size", page.getSize());

			return ApiResponse.success(ResponseCode.SUCCESS, "Feedback list fetched successfully", response);

		} catch (Exception e) {

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}

	@Override
	public ApiResponse<?> getInterviewProgressDetailsById(Integer applicationId) {
		log.info("InterviewPlanServiceImpl:Inside the getInterviewProgressDetailsById method");
		Optional<JobApplicationEntity> jobApplicationEntity=jobApplicationRepository.findById(applicationId);
		
		
		JobApplicationEntity entity=jobApplicationEntity.get();
		String applicantName=entity.getFirstName();
		String applicantEmail=entity.getEmail();
		String applicantMobileNumber=entity.getPhNo();
		Integer jobId=entity.getJobId();
		
		InterviewApplicantDetailsResponse response=new InterviewApplicantDetailsResponse();
		response.setApplicantName(applicantName);
		response.setApplicantEmail(applicantEmail);
		response.setApplicantPhoneNumber(applicantMobileNumber);
		
		Optional<CreateJobDetailsEntity> jobDetailsEntity=createJobDetailsRepository.findById(jobId);
		CreateJobDetailsEntity jobEntity=jobDetailsEntity.get();
		String jobTitile=jobEntity.getJobTitle();
		String jobCode=jobEntity.getJobCode();
		Integer deptId=jobEntity.getDepartmentId();
		String departmentName=departmentsRepository.findById(deptId).get().getDepartmentName();
		Integer maxExperience=jobEntity.getMaxExperience();
		Integer minExperience=jobEntity.getMinExperience();
		response.setJobTitle(jobTitile);
		response.setJobCode(jobCode);
		response.setDepartment(departmentName);
		response.setMaxExperience(maxExperience);
		response.setMinExperience(minExperience);
		
		List<InterviewSessionEntity> sessionEntities = interviewSessionRepository.findByJobId(jobId);

		if (sessionEntities != null && !sessionEntities.isEmpty()) {

		    // Latest session
		    InterviewSessionEntity sessionEntity = sessionEntities.get(sessionEntities.size() - 1);

		    response.setInterviewMailSentAt(sessionEntity.getScheduledTime());
		    response.setInterviewScheduledAt(sessionEntity.getInterviewScheduledDateTime());
		    response.setScheduledBy(sessionEntity.getScheduledBy());
		}
		
		Optional<AIInterviewQuestionsEntity> questionsEntity=aiInterviewQuestionsRepository.findByApplicationId(applicationId);
		AIInterviewQuestionsEntity interviewQuestionsEntity=questionsEntity.get();
		Integer noOfQuestions=interviewQuestionsEntity.getNumberOfQuestions();
		List<Object> questions=interviewQuestionsEntity.getQuestions();
		List<String> questionType=interviewQuestionsEntity.getQuestionType();
		String questionDifficulty=interviewQuestionsEntity.getDifficultyLevel();
		response.setNoOfQuestions(noOfQuestions);
		response.setQuestionType(questionType);
		response.setQuestions(questions);
		response.setQuestionDifficulty(questionDifficulty);
		
		
		log.info("InterviewPlanServiceImpl:Exit from the getInterviewProgressDetailsById method");
		return ApiResponse.success(ResponseCode.SUCCESS, "Interview progress details fetched successfully", response);
	
	}
	

}

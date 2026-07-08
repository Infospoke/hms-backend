package com.hms.service.serviceImpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

import org.apache.kafka.common.errors.ResourceNotFoundException;
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
import com.hms.service.dto.NotificationEvent;
import com.hms.service.entity.AIInterviewQuestionsEntity;
import com.hms.service.entity.ActivityFeedEntity;
import com.hms.service.entity.ApplicanDetailsEntity;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.ChildLinkCommentsEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.InterviewCurrentStageEntity;
import com.hms.service.entity.InterviewFeedbackEntity;
import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewRoundDropDownEntity;
import com.hms.service.entity.InterviewRoundEntity;
import com.hms.service.entity.InterviewScheduleEntity;
import com.hms.service.entity.InterviewSessionEntity;
import com.hms.service.entity.InterviewerAssignmentEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.ResumeAnalysisEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.AInterviewQuestionsRepository;
import com.hms.service.repository.ActivityFeedRepository;
import com.hms.service.repository.ApplicantDetailsRepository;
import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.ChildLinkCommentsRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.repository.InterviewCurrentStageRepository;
import com.hms.service.repository.InterviewFeedbackRepository;
import com.hms.service.repository.InterviewPlanRepository;
import com.hms.service.repository.InterviewRoundDropDownRepository;
import com.hms.service.repository.InterviewRoundRepository;
import com.hms.service.repository.InterviewScheduleRepository;
import com.hms.service.repository.InterviewSessionRepository;
import com.hms.service.repository.InterviewerAssignmentRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.ResumeAnalysisRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.ApplicantFeedBackRequest;
import com.hms.service.request.InterviewCompleteRequest;
import com.hms.service.request.InterviewFeedbackRequest;
import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.InterviewRoundRequest;
import com.hms.service.request.InterviewScheduleRequest;
import com.hms.service.request.LevelConfig;
import com.hms.service.request.RescheduleInterviewRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateInterviewCompletionStatusRequest;
import com.hms.service.request.UpdateInterviewPlanRequest;
import com.hms.service.response.AIInterviewScheduleResponse;
import com.hms.service.response.ApplicantFeedBackResponse;
import com.hms.service.response.CommentTimelineResponse;
import com.hms.service.response.InterviewApplicantDetailsResponse;
import com.hms.service.response.InterviewDashboardResponse;
import com.hms.service.response.InterviewDetailsResponse;
import com.hms.service.response.InterviewExperienceResponse;
import com.hms.service.response.InterviewPlanResponse;
import com.hms.service.response.InterviewProgressListResponse;
import com.hms.service.response.InterviewProjectResponse;
import com.hms.service.response.InterviewRoundResponse;
import com.hms.service.response.InterviewRoundsResponse;
import com.hms.service.response.InterviewScheduleDetailsResponse;
import com.hms.service.response.InterviewSummaryResponse;
import com.hms.service.response.InterviewUpcomingListResponse;
import com.hms.service.response.RoundKey;
import com.hms.service.service.IInterviewPlanService;
import com.hms.service.service.IMailService;
import com.hms.service.service.INotificationService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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
	private ResumeAnalysisRepository resumeAnalysisRepository;

	@Autowired
	private INotificationService notificationService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private IMailService iMailService;

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
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private ApplicantDetailsRepository applicantDetailsRepository;

	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private InterviewSessionRepository interviewSessionRepository;

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private AInterviewQuestionsRepository aiInterviewQuestionsRepository;

	@Autowired

	private InterviewCurrentStageRepository interviewCurrentStageRepository;

	@Autowired
	private InterviewRoundDropDownRepository interviewRoundDropDownRepository;

	@Autowired
	private InterviewerAssignmentRepository interviewerAssignmentRepository;

	@Autowired
	private ActivityFeedRepository activityFeedRepository;

	@Autowired
	private MailServiceImpl mailService;

	@Value("${spring.mail.username}")
	private String fromEmail;

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

				InterviewRoundDropDownEntity dropdown = interviewRoundDropDownRepository
						.findById(round.getStageTypeId())
						.orElseThrow(() -> new RuntimeException("Invalid Stage Type Id"));
				roundEntity.setStageTypeId(round.getStageTypeId());

				roundEntity.setStageType(dropdown.getRoundName());
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

	@Transactional
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
		interviewFeedbackEntity.setCurrentStageId(request.getCurrentStageId());
		interviewFeedbackEntity.setInterviewMode(request.getInterviewMode());
		interviewFeedbackEntity.setDecision(request.getDecision());
		interviewFeedbackEntity.setSubmittedOn(LocalDateTime.now());
		interviewFeedbackEntity.setSubmittedBy(username);
		interviewFeedbackEntity.setUserId(userId.intValue());
		interviewFeedbackRepository.save(interviewFeedbackEntity);
		InterviewCurrentStageEntity currentStage = interviewCurrentStageRepository
				.findByApplicationIdAndFeedbackFalse(request.getApplicantId());
		log.info("enter into interview current satge" + currentStage);

		currentStage.setFeedback(true);
		currentStage.setFeedbackStatus(request.getDecision());
		interviewCurrentStageRepository.save(currentStage);

		if (request.getDecision().equalsIgnoreCase(Constants.MOVE_TO_INTERVIEW)) {
			ApiResponse<?> response = updateInterviewFeedback(request);
			if (!response.getMessage().equalsIgnoreCase("")) {
				return ApiResponse.failure(ResponseCode.FAILURE, response.getMessage());
			}
		}

		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Interview Feedback Submitted successfully");
	}

	private void sendNextRoundNotification(InterviewFeedbackRequest request, JobApplicationEntity applicant) {

		int planId = createJobDetailsRepository.findByJobId(request.getJobId()).getPlanId();


		InterviewRoundEntity currentRound = interviewRoundRepository.findByInterviewPlan_IdAndStageTypeId(planId,
				request.getCurrentStageId());

		if (currentRound == null) {
			throw new ResourceNotFoundException("Current interview round not found");
		}

		InterviewRoundEntity nextRound = interviewRoundRepository.findByInterviewPlan_IdAndRoundOrder(planId,
				currentRound.getRoundOrder() + 1);

		if (nextRound == null) {

			return;
		}

		Optional<InterviewerAssignmentEntity> assignment = interviewerAssignmentRepository
				.findByJobIdAndStageTypeId(request.getJobId(), nextRound.getStageTypeId());

		if (assignment == null) {
			throw new ResourceNotFoundException("Next interviewer assignment not found");
		}

		AssignRolesEntity assignRole = assignRolesRepository
				.findByUserId(assignment.get().getInterviewerUserId().intValue())
				.orElseThrow(() -> new ResourceNotFoundException("Role not assigned"));

		Optional<RolesEntity> role = rolesRepository.findByRoleId(assignRole.getRoleId());

		NotificationEvent event = new NotificationEvent();

		event.setType("INTERVIEW");
		event.setProcessId(applicant.getId().toString());

		Integer deptId = createJobDetailsRepository.findByJobId(request.getJobId()).getDepartmentId();
		String deptName = departmentsRepository.findById(deptId).get().getDepartmentName();
		event.setDeptName(deptName);

		event.setCheckerNotificationTitle("Interview Assigned");

		event.setCheckerMessage(applicant.getFirstName() + " has been moved to " + nextRound.getStageName()
				+ ". Please schedule/interview the candidate.");

		event.setMakerNotificationTitle("Interview Feedback Submitted");

		event.setMakerMessage("Interview feedback submitted successfully.");

		event.setMakerRoleId(assignRole.getRoleId());

		event.setMakerRoleName(role.get().getRoleName());

		event.setMakerEmailAddress("");

		Map<Integer, List<String>> roleEmailMap = new HashMap<>();

		roleEmailMap.put(assignRole.getRoleId(), Collections.emptyList());

		event.setRoleEmailMap(roleEmailMap);

		event.setCheckerRoleName(role.get().getRoleName());

		notificationService.callNotification(event);
	}

	private void sendInterviewDecisionMail(JobApplicationEntity applicant, String decision) {

		String subject;
		String mailBody;
		String jobTitle=createJobDetailsRepository.findByJobId(applicant.getJobId()).getJobTitle();

           if (Constants.REJECT.equalsIgnoreCase(decision)) {

			subject = "Interview Result";

			 mailBody = String.format(
			        Constants.CANDIDATE_REJECTION_MAIL_BODY,
			        applicant.getFirstName(),
			        jobTitle
			        
			);
		} else {
			return;
		}

		mailService.sendMail(fromEmail, applicant.getEmail(), null, subject,mailBody , null);
	}

	public ApiResponse<?> scheduleInterview(InterviewScheduleRequest request) {
		log.info("InterviewPlanServiceImpl:Inside the scheduleInterview method");
		InterviewScheduleEntity entity = new InterviewScheduleEntity();

		String authHeader = httpServletRequest.getHeader("Authorization");
		String token = authHeader.substring(7);
		String userName = jwtService.extractUsernameFromClaims(token);
		Long userId = jwtService.extractUserId(token);

		entity.setApplicantId(request.getApplicantId());
		entity.setRoundId(request.getRoundId());
		entity.setInterviewDate(request.getInterviewDate());
		entity.setStartTime(request.getStartTime());
		entity.setEndTime(request.getEndTime());
		entity.setUserId(userId.intValue());
		entity.setMeetingLink(request.getMeetingLink());
		entity.setVenueDetails(request.getVenueDetails());
		entity.setCreatedBy(userName);
		entity.setCreatedOn(LocalDateTime.now());

		interviewScheduleRepository.save(entity);

		InterviewCurrentStageEntity interviewCurrentStageEntity = interviewCurrentStageRepository
				.findByApplicationIdAndToScheduleFalse(request.getApplicantId());
		interviewCurrentStageEntity.setInterviewDate(request.getInterviewDate());
		interviewCurrentStageEntity.setStartTime(request.getStartTime());
		interviewCurrentStageEntity.setEndTime(request.getEndTime());
		interviewCurrentStageEntity.setToSchedule(true);
		interviewCurrentStageEntity.setInterviewCompleted(false);
		interviewCurrentStageEntity.setFeedback(false);
		interviewCurrentStageRepository.save(interviewCurrentStageEntity);

		// Fetch applicant
		JobApplicationEntity applicant = jobApplicationRepository.findById(request.getApplicantId()).orElse(null);

		Integer jobId = applicant.getJobId();

		Optional<InterviewerAssignmentEntity> interviewerAssignments = interviewerAssignmentRepository
				.findByJobIdAndStageTypeId(jobId, request.getRoundId());

		InterviewerAssignmentEntity interviewerAssignmentEntity = null;

		if (interviewerAssignments.isPresent()) {
			interviewerAssignmentEntity = interviewerAssignments.get();
		}

		// Send mail
		try {

			if (applicant != null && applicant.getEmail() != null) {

				String subject = Constants.INTERVIEW_SCHEDULE_SUBJECT;

				String body = String.format(
				        Constants.INTERVIEW_SCHEDULE_BODY,
				        applicant.getFirstName(),
				        interviewerAssignmentEntity.getJobTitle(),
				        interviewerAssignmentEntity.getStageName(),
				        interviewerAssignmentEntity.getStageName(),
				        request.getInterviewDate(),
				        request.getStartTime(),
				        request.getEndTime(),
				        request.getMeetingLink() != null ? "Online" : "Offline",
				        request.getMeetingLink() != null
				                ? request.getMeetingLink()
				                : request.getVenueDetails()
				);

				mailService.sendMail(fromEmail, applicant.getEmail(), null, subject, body, null);

			}

		} catch (Exception e) {

			log.error("InterviewPlanServiceImpl::Mail failed : {}", e.getMessage());

		}

		
		try {

			if (interviewerAssignmentEntity != null) {

				Integer interviewerUserId = interviewerAssignmentEntity.getInterviewerUserId().intValue();

				// Fetch interviewer details
				UserEntity interviewer = userRepository.findById(interviewerUserId).orElse(null);

				if (interviewer != null && interviewer.getEmail() != null) {

					String subject = Constants.INTERVIEW_SCHEDULE_SUBJECT;

					String body = String.format(
					        Constants.INTERVIEWER_SCHEDULE_BODY,
					        interviewerAssignmentEntity.getInterviewerName(),                 
					        interviewerAssignmentEntity.getStageName(),                       
					        applicant.getFirstName(),                                         
					        interviewerAssignmentEntity.getJobTitle(),                        
					        interviewerAssignmentEntity.getStageName(),                       
					        request.getInterviewDate(),                                       
					        request.getStartTime(),                                           
					        request.getEndTime(),                                             
					        request.getMeetingLink() != null ? "Online" : "Offline",      
					        request.getMeetingLink() != null
					                ? request.getMeetingLink()
					                : request.getVenueDetails()                               
					);

					mailService.sendMail(fromEmail, interviewer.getEmail(), null, subject, body, null);
				}
			}

		} catch (Exception e) {

			log.error("InterviewPlanServiceImpl::Interviewer Mail failed : {}", e.getMessage());

		}

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

		Page<InterviewCurrentStageEntity> currentStage = interviewCurrentStageRepository
				.findAll(request.buildTodayInterviewSpecification(userIdFromToken), pageable);

		log.info("Total Elements: {}", currentStage.getTotalElements());
		log.info("Content Size: {}", currentStage.getContent().size());

		if (currentStage.getContent().isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "No interviews found");
		}

		List<Integer> applicationIds = currentStage.getContent().stream()
				.map(InterviewCurrentStageEntity::getApplicationId).distinct().toList();

		List<JobApplicationEntity> applications = jobApplicationRepository.findAllById(applicationIds);

		List<Integer> jobIds = applications.stream().map(JobApplicationEntity::getJobId).distinct().toList();

		List<CreateJobDetailsEntity> jobDetailsList = createJobDetailsRepository.findByJobIdIn(jobIds);

		List<Integer> departmentIds = jobDetailsList.stream().map(CreateJobDetailsEntity::getDepartmentId).distinct()
				.toList();

		List<DepartmentsEntity> departments = departmentsRepository.findAllById(departmentIds);

		Map<Integer, String> departmentMap = departments.stream()
				.collect(Collectors.toMap(DepartmentsEntity::getId, DepartmentsEntity::getDepartmentName));

		List<InterviewCurrentStageEntity> currentStages = interviewCurrentStageRepository
				.findByApplicationIdIn(applicationIds);

		List<Integer> stageIds = currentStages.stream().map(InterviewCurrentStageEntity::getCurrentStageType).distinct()
				.toList();

		List<InterviewRoundDropDownEntity> rounds = interviewRoundDropDownRepository.findAllById(stageIds);

		Map<Integer, String> stageNameMap = rounds.stream().collect(
				Collectors.toMap(InterviewRoundDropDownEntity::getId, InterviewRoundDropDownEntity::getRoundName));

		Map<Integer, JobApplicationEntity> applicationMap = applications.stream()
				.collect(Collectors.toMap(JobApplicationEntity::getId, Function.identity()));

		Map<Integer, String> applicantMap = applications.stream()
				.collect(Collectors.toMap(JobApplicationEntity::getId, JobApplicationEntity::getFirstName));
		Map<Integer, CreateJobDetailsEntity> jobMap = jobDetailsList.stream()
				.collect(Collectors.toMap(CreateJobDetailsEntity::getJobId, Function.identity()));

		Map<Integer, InterviewCurrentStageEntity> stageMap = currentStages.stream()
				.collect(Collectors.toMap(InterviewCurrentStageEntity::getApplicationId, Function.identity()));

		List<Map<String, Object>> responseList = new ArrayList<>();

		for (Integer applicationId : applicationIds) {

			JobApplicationEntity application = applicationMap.get(applicationId);

			if (application == null) {
				continue;
			}

			String applicantName = applicantMap.get(applicationId);

			Integer jobId = application.getJobId();

			CreateJobDetailsEntity job = jobMap.get(jobId);

			if (job == null) {
				continue;
			}

			InterviewCurrentStageEntity stage = stageMap.get(applicationId);

			if (stage == null) {
				continue;
			}

			Integer departmentId = job.getDepartmentId();
			Integer planId = job.getPlanId();

			String departmentName = departmentMap.get(departmentId);
			Integer currentStageType = stage.getCurrentStageType();

			Integer roundOrder = interviewRoundRepository.findByInterviewPlanIdAndStageType(planId, currentStageType);

			String stageName = stageNameMap.get(currentStageType);

			Map<String, Object> interviewData = new HashMap<>();

			interviewData.put("applicationId", applicationId);
			interviewData.put("applicantName", applicantName);
			interviewData.put("jobId", jobId);
			interviewData.put("jobTitle", job.getJobTitle());
			interviewData.put("jobCode", job.getJobCode());
			interviewData.put("departmentName", departmentName);
			interviewData.put("currentStageType", currentStageType);
			interviewData.put("stageName", stageName);
			interviewData.put("startTime", stage.getStartTime());
			interviewData.put("endTime", stage.getEndTime());
			interviewData.put("round", roundOrder);

			responseList.add(interviewData);
		}

		String search = request.getFilter("search");

		if (search != null && !search.isBlank()) {

			String keyword = search.toLowerCase();

			responseList = responseList.stream().filter(item ->

			String.valueOf(item.getOrDefault("applicantName", "")).toLowerCase().contains(keyword)

					||

					String.valueOf(item.getOrDefault("jobTitle", "")).toLowerCase().contains(keyword)

			).toList();
		}
		int totalRecords = responseList.size();

		int page = request.getPage();
		int size = request.getSize();

		int start = page * size;
		int end = Math.min(start + size, totalRecords);

		List<Map<String, Object>> paginatedList;

		if (start >= totalRecords) {
			paginatedList = Collections.emptyList();
		} else {
			paginatedList = responseList.subList(start, end);
		}

		Map<String, Object> response = new HashMap<>();

		response.put("content", paginatedList);
		response.put("currentPage", page);
		response.put("pageSize", size);
		response.put("totalPages", totalRecords == 0 ? 0 : (int) Math.ceil((double) totalRecords / size));
		response.put("totalElements", totalRecords);
		response.put("numberOfElements", paginatedList.size());

		return ApiResponse.success(ResponseCode.SUCCESS, "Application details fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getInterviewDetails(Integer applicationId) {

		log.info("InterviewPlanServiceImpl :: getInterviewDetails");

		Optional<ApplicanDetailsEntity> optional = applicantDetailsRepository.findByApplicationId(applicationId);

		if (optional.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Interview details not found");
		}

		ApplicanDetailsEntity entity = optional.get();

		Integer jobId = entity.getJobId();

		Optional<CreateJobDetailsEntity> jobDetailsEnity = createJobDetailsRepository.findById(jobId);
		CreateJobDetailsEntity createJobDetailsEntity = jobDetailsEnity.get();
		Integer deptId = createJobDetailsEntity.getDepartmentId();
		Integer job = createJobDetailsEntity.getJobId();

		InterviewDetailsResponse response = new InterviewDetailsResponse();
		String department = departmentsRepository.findById(deptId).get().getDepartmentName();

		InterviewScheduleEntity interviewScheduleEntity = interviewScheduleRepository
				.findByApplicantIdAndInterviewDate(applicationId, LocalDate.now());

		InterviewCurrentStageEntity currentStageEntity = interviewCurrentStageRepository
				.findByApplicationIdAndFeedbackFalse(applicationId);

		Duration duration = Duration.between(interviewScheduleEntity.getStartTime(),
				interviewScheduleEntity.getEndTime());

		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();

		String durationText = hours > 0 ? hours + " Hour(s) " + minutes + " Minute(s)" : minutes + " Minute(s)";

		Integer currentStageId = currentStageEntity.getCurrentStageType();

		String currentStageName = interviewRoundDropDownRepository.findById(currentStageId).get().getRoundName();

		Integer interviewRound = interviewCurrentStageRepository.countByApplicationId(applicationId);
		interviewRound = interviewRound + 1;

		String interviewMode = interviewScheduleEntity.getMeetingLink() != null ? "Online" : "Offline";

		response.setCandidateName(entity.getName());
		response.setJobTitle(createJobDetailsEntity.getJobTitle());
		response.setDepartment(department);
		response.setInterviewMode(interviewMode);

		response.setInterviewRound(interviewRound);
		response.setInterviewType(currentStageName);
		response.setDuration(durationText);
		response.setDesignation(entity.getDesignation());
		response.setTotalExperience(entity.getTotalExperience());
		response.setCurrentCompany(entity.getCurrentCompany());
		response.setScheduleTime(currentStageEntity.getStartTime());
		response.setScheduleDate(currentStageEntity.getInterviewDate());
		response.setJobId(job);
		if (interviewMode.equalsIgnoreCase("Online")) {
			response.setMeetingPlatForm(interviewScheduleEntity.getMeetingLink());
		} else {
			response.setVenueDetails(interviewScheduleEntity.getVenueDetails());
		}

		// Experience Details
		List<InterviewExperienceResponse> experienceResponses = new ArrayList<>();

		if (entity.getExperienceDetails() != null) {

			for (Map<String, Object> exp : entity.getExperienceDetails()) {

				InterviewExperienceResponse experience = new InterviewExperienceResponse();

				experience.setCompany(String.valueOf(exp.get("company")));

				experience.setRole(String.valueOf(exp.get("job_title")));

				experience.setStartDate(String.valueOf(exp.get("start_date")));

				experience.setEndDate(String.valueOf(exp.get("end_date")));

				experienceResponses.add(experience);
			}
		}

		response.setExperienceDetails(experienceResponses);

		// Project Details
		List<InterviewProjectResponse> projectResponses = new ArrayList<>();

		if (entity.getProjects() != null) {

			for (Map<String, Object> project : entity.getProjects()) {

				InterviewProjectResponse projectResponse = new InterviewProjectResponse();

				projectResponse.setProjectTitle(String.valueOf(project.get("project_title")));

				projectResponse.setTechStack((List<String>) project.get("tech_stack"));

				projectResponse.setDescription((List<String>) project.get("description"));

				projectResponses.add(projectResponse);
			}
		}

		response.setProjectDetails(projectResponses);

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview details fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getScheduleList(SpecificationFilterRequest request) {

		String authHeader = httpServletRequest.getHeader("Authorization");
		String token = authHeader.substring(7);

		Long userId = jwtService.extractUserId(token);
		Integer userIdFromToken = userId.intValue();

		try {

			Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
					Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

			Specification<InterviewCurrentStageEntity> specification = request
					.toBeScheduleInterviewSpecification(userIdFromToken);

			List<InterviewCurrentStageEntity> stages = interviewCurrentStageRepository.findAll(specification);

			List<Map<String, Object>> content = stages.stream().map(stage -> {

				Map<String, Object> map = new LinkedHashMap<>();

				JobApplicationEntity application = jobApplicationRepository.findById(stage.getApplicationId())
						.orElse(null);

				String candidateName = null;
				String jobTitle = null;

				if (application != null) {

					candidateName = application.getFirstName();

					CreateJobDetailsEntity job = createJobDetailsRepository.findById(application.getJobId())
							.orElse(null);

					if (job != null) {
						jobTitle = job.getJobTitle();
					}
				}

				String roundName = null;

				InterviewRoundDropDownEntity round = interviewRoundDropDownRepository
						.findById(stage.getCurrentStageType()).orElse(null);

				if (round != null) {
					roundName = round.getRoundName();
				}

				map.put("candidateName", candidateName);
				map.put("jobTitle", jobTitle);
				map.put("round", roundName);
				map.put("requestedOn", stage.getCreatedOn());
				map.put("roundId", stage.getCurrentStageType());

				map.put("priority", stage.getCreatedOn() != null ? calculatePriority(stage.getCreatedOn()) : "Low");

				map.put("applicationId", stage.getApplicationId());

				return map;

			}).toList();

			String search = request.getFilter("search");

			if (search != null && !search.isBlank()) {

				String searchText = search.toLowerCase().trim();

				content = content.stream().filter(map -> {

					String candidateName = String.valueOf(map.getOrDefault("candidateName", "")).toLowerCase();

					String jobTitle = String.valueOf(map.getOrDefault("jobTitle", "")).toLowerCase();

					return candidateName.contains(searchText) || jobTitle.contains(searchText);

				}).toList();
			}
			String priorityFilter = request.getFilter("priority");

			if (priorityFilter != null && !priorityFilter.isBlank()) {

				content = content.stream().filter(
						map -> priorityFilter.equalsIgnoreCase(String.valueOf(map.getOrDefault("priority", ""))))
						.toList();
			}
			// Dynamic Pagination After Search
			int totalElements = content.size();

			int start = request.getPage() * request.getSize();

			int end = Math.min(start + request.getSize(), totalElements);

			List<Map<String, Object>> paginatedContent = start < totalElements ? content.subList(start, end)
					: Collections.emptyList();

			int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

			Map<String, Object> response = new LinkedHashMap<>();

			response.put("content", paginatedContent);
			response.put("currentPage", request.getPage());
			response.put("totalPages", totalPages);
			response.put("totalElements", totalElements);
			response.put("size", paginatedContent.size());

			return ApiResponse.success(ResponseCode.SUCCESS, "Applicants to be schedule list fetched successfully",
					response);

		} catch (Exception e) {

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}

	private String calculatePriority(LocalDate createdOn) {

		long daysPassed = ChronoUnit.DAYS.between(createdOn, LocalDate.now());

		if (daysPassed >= 5) {
			return "High";
		}

		if (daysPassed >= 3) {
			return "Medium";
		}

		return "Low";
	}

	@Override
	public ApiResponse<?> getInterviewProgressDetailsById(Integer applicationId) {
		log.info("InterviewPlanServiceImpl:Inside the getInterviewProgressDetailsById method");
		Optional<JobApplicationEntity> jobApplicationEntity = jobApplicationRepository.findById(applicationId);

		JobApplicationEntity entity = jobApplicationEntity.get();
		String applicantName = entity.getFirstName();
		String applicantEmail = entity.getEmail();
		String applicantMobileNumber = entity.getPhNo();
		Integer jobId = entity.getJobId();

		InterviewApplicantDetailsResponse response = new InterviewApplicantDetailsResponse();
		response.setApplicantName(applicantName);
		response.setApplicantEmail(applicantEmail);
		response.setApplicantPhoneNumber(applicantMobileNumber);

		Optional<CreateJobDetailsEntity> jobDetailsEntity = createJobDetailsRepository.findById(jobId);
		CreateJobDetailsEntity jobEntity = jobDetailsEntity.get();
		String jobTitile = jobEntity.getJobTitle();
		String jobCode = jobEntity.getJobCode();
		Integer deptId = jobEntity.getDepartmentId();
		String departmentName = departmentsRepository.findById(deptId).get().getDepartmentName();
		Integer maxExperience = jobEntity.getMaxExperience();
		Integer minExperience = jobEntity.getMinExperience();
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

		Optional<AIInterviewQuestionsEntity> questionsEntity = aiInterviewQuestionsRepository
				.findByApplicationId(applicationId);
		AIInterviewQuestionsEntity interviewQuestionsEntity = questionsEntity.get();
		Integer noOfQuestions = interviewQuestionsEntity.getNumberOfQuestions();
		List<Object> questions = interviewQuestionsEntity.getQuestions();
		List<String> questionType = interviewQuestionsEntity.getQuestionType();
		String questionDifficulty = interviewQuestionsEntity.getDifficultyLevel();
		response.setNoOfQuestions(noOfQuestions);
		response.setQuestionType(questionType);
		response.setQuestions(questions);
		response.setQuestionDifficulty(questionDifficulty);

		log.info("InterviewPlanServiceImpl:Exit from the getInterviewProgressDetailsById method");
		return ApiResponse.success(ResponseCode.SUCCESS, "Interview progress details fetched successfully", response);

	}

	@Override
	public ApiResponse<?> getAllAIInterviews(SpecificationFilterRequest request) {

		log.info("InterviewSessionServiceImpl :: getAllAIInterviews");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

		Page<InterviewSessionEntity> sessionPage = interviewSessionRepository
				.findAll(request.buildAIScheduleInterviewSpecification(interviewPlanRepository), pageable);

		List<AIInterviewScheduleResponse> responseList = new ArrayList<>();

		for (InterviewSessionEntity session : sessionPage.getContent()) {

			if (Boolean.TRUE.equals(session.getMoveToSchedule())) {

				AIInterviewScheduleResponse response = new AIInterviewScheduleResponse();

				response.setApplicationId(session.getApplicationId());

				if (session.getApplicant() != null) {

					response.setCandidateName(session.getApplicant().getCandidateName());

					response.setEmail(session.getApplicant().getEmail());
				}

				Integer planId = null;

				if (session.getJob() != null) {

					response.setJobTitle(session.getJob().getJobTitle());

					planId = session.getJob().getPlanId();
				}

				if (planId != null) {

					Optional<InterviewPlanEntity> planOptional = interviewPlanRepository.findById(planId);

					if (planOptional.isPresent()) {

						response.setInterviewPlan(planOptional.get().getPlanName());
					}
				}

				if (session.getMoveToScheduleDateTime() != null) {

					LocalDate dueDate = session.getMoveToScheduleDateTime().plusDays(7).toLocalDate();

					response.setDueDate(dueDate);

					LocalDate createdDate = session.getMoveToScheduleDateTime().toLocalDate();

					long days = ChronoUnit.DAYS.between(createdDate, LocalDate.now());

					if (days <= 2) {

						response.setPriority("Low");

					} else if (days <= 3) {

						response.setPriority("Medium");

					} else {

						response.setPriority("High");
					}
				}

				responseList.add(response);
			}
		}

		Map<String, Object> result = new HashMap<>();

		result.put("content", responseList);
		result.put("currentPage", sessionPage.getNumber());
		result.put("totalElements", sessionPage.getTotalElements());
		result.put("totalPages", sessionPage.getTotalPages());

		return ApiResponse.success(ResponseCode.SUCCESS, result);
	}

	@Override
	public ApiResponse<?> candidateOverview(Integer applicationId) {

		log.info("InterviewPlanServiceImpl :: Inside candidateOverview");

		InterviewSessionEntity session = interviewSessionRepository.findByApplicationId(applicationId).orElse(null);

		if (session == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Interview Session Not Found");
		}

		ResumeAnalysisEntity candidate = resumeAnalysisRepository.findByApplicationId(session.getApplicationId())
				.orElse(null);

		if (candidate == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Candidate Not Found");
		}

		CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(candidate.getJobId());

		if (job == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Job Not Found");
		}

		InterviewPlanEntity plan = interviewPlanRepository.findById(job.getPlanId()).orElse(null);

		if (plan == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Interview Plan Not Found");
		}

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("candidateName", candidate.getCandidateName());

		response.put("email", candidate.getEmail());

		response.put("jobTitle", job.getJobTitle());

		response.put("planName", plan.getPlanName());

		response.put("interviewType", "AI Video Interview");

		response.put("duration", null);

		log.info("InterviewPlanServiceImpl :: Exit candidateOverview");

		return ApiResponse.success(ResponseCode.SUCCESS, "Candidate Overview fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getFeedbackList(SpecificationFilterRequest request) {
		log.info("InterviewPlanServiceImpl :: Inside of getFeedbackList method");

		try {

			String authHeader = httpServletRequest.getHeader("Authorization");
			String token = authHeader.substring(7);

			Long userId = jwtService.extractUserId(token);
			Integer userIdFromToken = userId.intValue();

			Specification<InterviewCurrentStageEntity> spec = request.buildFeedbackSpecification(userIdFromToken);

			List<InterviewCurrentStageEntity> interviewStages = interviewCurrentStageRepository.findAll(spec);
			log.info("InterviewPlanServiceImpl :: retrived data from JobApplicationEntity ");
			List<Map<String, Object>> responseList = new ArrayList<>();

			for (InterviewCurrentStageEntity stage : interviewStages) {

				Optional<JobApplicationEntity> applicationOpt = jobApplicationRepository
						.findById(stage.getApplicationId());
				log.info("InterviewPlanServiceImpl :: retrived data from JobApplicationEntity ");

				if (applicationOpt.isEmpty()) {
					continue;
				}

				JobApplicationEntity application = applicationOpt.get();

				CreateJobDetailsEntity job = createJobDetailsRepository.findById(application.getJobId()).orElse(null);
				log.info("InterviewPlanServiceImpl :: retrived data from CreateJobDetailsEntity ");

				Map<String, Object> response = new HashMap<>();

				response.put("applicationId", stage.getApplicationId());

				String applicantName = (application.getFirstName() == null ? "" : application.getFirstName()) + " "
						+ (application.getLastName() == null ? "" : application.getLastName());

				response.put("applicantName", applicantName.trim());
				Integer currentStageType = stage.getCurrentStageType();
				String stageName = interviewRoundDropDownRepository.findById(currentStageType).get().getRoundName();

				response.put("currentStageType", stageName);

				response.put("currentStageId", stage.getCurrentStageType());

				response.put("interviewDate", stage.getInterviewDate());

				response.put("endTime", stage.getEndTime());
				
				response.put("feedbackStatus",stage.getFeedbackStatus());

				response.put("jobId", job.getJobId());
				Integer deptId = job.getDepartmentId();
				String departmentName = departmentsRepository.findById(deptId).get().getDepartmentName();

				// SLA

				String sla = "LOW";

				if (stage.getInterviewCompletedOn() != null) {

					long days = ChronoUnit.DAYS.between(stage.getInterviewCompletedOn().toLocalDate(), LocalDate.now());

					if (days > 3) {
						sla = "HIGH";
					} else if (days >= 2) {
						sla = "MEDIUM";
					}
				}

				response.put("sla", sla);

				if (job != null) {

					response.put("jobId", job.getJobId());

					response.put("jobCode", job.getJobCode());

					response.put("jobTitle", job.getJobTitle());

					response.put("department", departmentName);
				}

				responseList.add(response);
			}

			String search = request.getFilter("search");

			if (search != null && !search.isBlank()) {

				String keyword = search.toLowerCase();

				responseList = responseList.stream()

						.filter(item ->

						String.valueOf(item.getOrDefault("applicantName", "")).toLowerCase().contains(keyword)

								||

								String.valueOf(item.getOrDefault("jobTitle", "")).toLowerCase().contains(keyword)

						).toList();
			}

			String jobTitle = request.getFilter("jobTitle");

			if (jobTitle != null && !jobTitle.isBlank()) {

				responseList = responseList.stream()

						.filter(item -> jobTitle.equalsIgnoreCase(String.valueOf(item.get("jobTitle"))))

						.toList();
			}

			String round = request.getFilter("round");

			if (round != null && !round.isBlank()) {

				responseList = responseList.stream()

						.filter(item -> round.equals(String.valueOf(item.get("stageName"))))

						.toList();
			}

			String slaFilter = request.getFilter("sla");

			if (slaFilter != null && !slaFilter.isBlank()) {

				responseList = responseList.stream()

						.filter(item -> slaFilter.equalsIgnoreCase(String.valueOf(item.get("sla"))))

						.toList();
			}

			int totalRecords = responseList.size();

			int page = request.getPage();

			int size = request.getSize();

			int start = page * size;

			int end = Math.min(start + size, totalRecords);

			List<Map<String, Object>> paginatedList;

			if (start >= totalRecords) {

				paginatedList = Collections.emptyList();

			} else {

				paginatedList = responseList.subList(start, end);
			}

			Map<String, Object> finalResponse = new HashMap<>();

			finalResponse.put("content", paginatedList);
			finalResponse.put("currentPage", page);
			finalResponse.put("pageSize", size);
			finalResponse.put("totalPages", totalRecords == 0 ? 0 : (int) Math.ceil((double) totalRecords / size));
			finalResponse.put("totalElements", totalRecords);
			finalResponse.put("numberOfElements", paginatedList.size());
			log.info("InterviewPlanServiceImpl :: Exit from getFeedBackListMethod");

			return ApiResponse.success(ResponseCode.SUCCESS, "Feedback list fetched successfully", finalResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch feedback list");
		}

	}

	@Override
	public ApiResponse<?> updateInterviewFeedback(InterviewFeedbackRequest interviewFeedbackRequest) {
		log.info("InterviewPlanServiceImpl :: Inside updateInterviewFeedback");
		if (interviewFeedbackRequest.getDecision().equalsIgnoreCase(Constants.MOVE_TO_INTERVIEW)) {

			int planId = createJobDetailsRepository.findByJobId(interviewFeedbackRequest.getJobId()).getPlanId();
			log.info("Plan Id : {}", planId);
			log.info("Stage Type Id : {}", interviewFeedbackRequest.getStageTypeId());
			int currentOrder = interviewRoundRepository
					.findByInterviewPlan_IdAndStageTypeId(planId, interviewFeedbackRequest.getStageTypeId())
					.getRoundOrder();

			List<InterviewRoundEntity> interviewRoundEntities = interviewRoundRepository
					.findByInterviewPlan_IdOrderByRoundOrderAsc(planId);
			int nextStageid = 0;
			int roundOrder = 0;
			for (InterviewRoundEntity interviewRoundEntity : interviewRoundEntities) {
				if (interviewRoundEntity.getRoundOrder() > currentOrder) {
					nextStageid = interviewRoundEntity.getStageTypeId();
					roundOrder = interviewRoundEntity.getRoundOrder();
					break;
				}
			}
			if (nextStageid != 0) {
				InterviewCurrentStageEntity interviewCurrentStageEntity = new InterviewCurrentStageEntity();
				interviewCurrentStageEntity.setApplicationId(interviewFeedbackRequest.getApplicantId());
				interviewCurrentStageEntity.setCurrentStageType(nextStageid);
				interviewCurrentStageEntity.setRoundOrder(roundOrder);
				int interviewerId = interviewerAssignmentRepository
						.findByJobIdAndPlanIdAndStageTypeId(interviewFeedbackRequest.getJobId(), planId, roundOrder)
						.getInterviewerUserId().intValue();
				interviewCurrentStageEntity.setInterviewerId(interviewerId);
				interviewCurrentStageEntity.setToSchedule(false);
				interviewCurrentStageEntity.setCreatedOn(LocalDate.now(ZoneId.of("Asia/Kolkata")));
				interviewCurrentStageEntity.setFeedbackStatus("Pending");
				interviewCurrentStageRepository.save(interviewCurrentStageEntity);
				JobApplicationEntity applicant = jobApplicationRepository
						.findById(interviewFeedbackRequest.getApplicantId())
						.orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));
				sendInterviewDecisionMail(applicant, interviewFeedbackRequest.getDecision());
				sendNextRoundNotification(interviewFeedbackRequest, applicant);
				log.info("InterviewPlanServiceImpl :: Applicant moved to the next Round");
			} else {
				JobApplicationEntity applicationEntity = jobApplicationRepository
						.findById(interviewFeedbackRequest.getApplicantId()).get();
				applicationEntity.setInPersonInterviews(true);
				jobApplicationRepository.save(applicationEntity);
				ActivityFeedEntity entity = new ActivityFeedEntity();
				entity.setTimeStamp(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
				String jobTitle = createJobDetailsRepository.findByJobId(applicationEntity.getJobId()).getJobTitle();
				entity.setActivity(applicationEntity.getFirstName() + "finished all interview rounds for" + jobTitle);
				activityFeedRepository.save(entity);
				log.info("InterviewPlanServiceImpl :: All Rounds of the Applicant are Completed");
			}
		}
		
		log.info("InterviewPlanServiceImpl :: Exit from the updateInterviewFeedback");
		return ApiResponse.success(ResponseCode.SUCCESS, "Applicant moved to next round");
	}

	@Override
	public ApiResponse<?> getInterviewProgressList(SpecificationFilterRequest request) {

		log.info("InterviewPlanServiceImpl :: Inside getInterviewProgressList");

		try {

			Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
					Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy()));

			Specification<ApplicanDetailsEntity> specification = request.buildInterviewProgressSpecification();

			Page<ApplicanDetailsEntity> applicantPage = applicantDetailsRepository.findAll(specification, pageable);

			List<InterviewProgressListResponse> responseList = new ArrayList<>();

			String search = request.getFilter("search");

			String departmentFilter = request.getFilter("departmentId");

			String currentStageFilter = request.getFilter("currentStage");

			for (ApplicanDetailsEntity applicant : applicantPage.getContent()) {

				InterviewProgressListResponse response = buildInterviewProgressResponse(applicant, search,
						departmentFilter, currentStageFilter);

				if (response != null) {
					responseList.add(response);
				}

			}

			Map<String, Object> result = new LinkedHashMap<>();

			result.put("content", responseList);

			result.put("currentPage", request.getPage());

			result.put("pageSize", request.getSize());

			result.put("totalElements", responseList.size());

			result.put("totalPages",
					responseList.isEmpty() ? 0 : (int) Math.ceil((double) responseList.size() / request.getSize()));

			return ApiResponse.success(ResponseCode.SUCCESS, "Interview Progress List fetched successfully", result);

		} catch (Exception e) {

			log.error("Error while fetching Interview Progress List", e);

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}

	}

	private InterviewProgressListResponse buildInterviewProgressResponse(ApplicanDetailsEntity applicant, String search,
			String departmentFilter, String currentStageFilter) {

		CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(applicant.getJobId());

		if (job == null) {
			return null;
		}

		if (search != null && !search.trim().isEmpty()) {

			String keyword = search.trim().toLowerCase();

			boolean matches = false;

			if (applicant.getName() != null && applicant.getName().toLowerCase().contains(keyword)) {

				matches = true;
			}

			if (!matches && applicant.getEmail() != null && applicant.getEmail().toLowerCase().contains(keyword)) {

				matches = true;
			}

			if (!matches && job.getJobTitle() != null && job.getJobTitle().toLowerCase().contains(keyword)) {

				matches = true;
			}

			if (!matches) {
				return null;
			}

		}

		if (departmentFilter != null && job.getDepartmentId() != null
				&& !job.getDepartmentId().equals(Integer.parseInt(departmentFilter))) {

			return null;
		}

		Integer appId = applicant.getApplicationId();

		InterviewCurrentStageEntity currentStage = interviewCurrentStageRepository.findTopByApplicationIdOrderByIdDesc(applicant.getApplicationId());

		if (currentStage == null) {
			return null;
		}
		if (currentStageFilter != null
				&& !currentStage.getCurrentStageType().equals(Integer.parseInt(currentStageFilter))) {

			return null;
		}

		InterviewProgressListResponse dto = new InterviewProgressListResponse();

		dto.setApplicationId(applicant.getApplicationId());

		dto.setCandidateName(applicant.getName());

		dto.setEmail(applicant.getEmail());

		dto.setJobTitle(job.getJobTitle());

		dto.setDepartment(getDepartmentName(job.getDepartmentId()));

		dto.setCurrentStageId(currentStage.getCurrentStageType());

		dto.setCurrentStage(getStageName(currentStage.getCurrentStageType()));

		dto.setLastActivity(currentStage.getInterviewCompletedOn());

		buildRoundDetails(dto, job.getPlanId(), currentStage);

		return dto;

	}

	private void buildRoundDetails(InterviewProgressListResponse dto, Integer planId,
			InterviewCurrentStageEntity currentStage) {

		List<InterviewRoundEntity> rounds = interviewRoundRepository.findByInterviewPlan_IdOrderByRoundOrderAsc(planId);

		List<InterviewRoundResponse> roundResponses = new ArrayList<>();

		int completedRounds = 0;

		Integer currentRoundOrder = rounds.stream()
				.filter(r -> r.getStageTypeId().equals(currentStage.getCurrentStageType()))
				.map(InterviewRoundEntity::getRoundOrder).findFirst().orElse(0);

		for (InterviewRoundEntity round : rounds) {

			InterviewRoundResponse response = new InterviewRoundResponse();

			response.setRoundOrder(round.getRoundOrder());

			response.setStageTypeId(round.getStageTypeId());

			response.setRoundName(getStageName(round.getStageTypeId()));

			if (round.getRoundOrder() < currentRoundOrder) {

				response.setStatus("COMPLETED");
				completedRounds++;

			} else if (round.getRoundOrder().equals(currentRoundOrder)) {

				if (Boolean.TRUE.equals(currentStage.getInterviewCompleted())) {

					response.setStatus("COMPLETED");
					completedRounds++;

				} else {

					response.setStatus("IN_PROGRESS");
				}

			} else {

				response.setStatus("PENDING");
			}

			roundResponses.add(response);
		}

		dto.setCompletedRounds(completedRounds);

		dto.setTotalRounds(rounds.size());

		dto.setRoundDetails(roundResponses);
	}

	private String getStageName(Integer stageTypeId) {

		return interviewRoundDropDownRepository.findById(stageTypeId).map(InterviewRoundDropDownEntity::getRoundName)
				.orElse("");
	}

	private String getDepartmentName(Integer departmentId) {

		if (departmentId == null) {
			return "";
		}

		return departmentsRepository.findById(departmentId).map(DepartmentsEntity::getDepartmentName).orElse("");
	}

	@Override
	public ApiResponse<InterviewDashboardResponse> getInterviewProgressCount() {

		Object[] result = jobApplicationRepository.getInterviewDashboard().get(0);

		InterviewDashboardResponse response = new InterviewDashboardResponse();

		response.setAllClearedCandidates(((Number) result[0]).intValue());
		response.setAiInterview(((Number) result[1]).intValue());
		response.setTechnicalRound(((Number) result[2]).intValue());
		response.setManagerialRound(((Number) result[3]).intValue());
		response.setHrRound(((Number) result[4]).intValue());

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview progress count fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getInterviewUpcomingList(SpecificationFilterRequest request) {

		try {

			String search = Optional.ofNullable(request.getFilter("search")).orElse("").trim().toLowerCase();

			Integer departmentId = Optional.ofNullable(request.getFilter("departmentId")).map(Integer::parseInt)
					.orElse(0);

			Integer roundId = Optional.ofNullable(request.getFilter("roundId")).map(Integer::parseInt).orElse(0);

			String interviewMode = Optional.ofNullable(request.getFilter("interviewMode")).orElse("").trim()
					.toLowerCase();

			LocalDate interviewDate = Optional.ofNullable(request.getFilter("interviewDate")).map(LocalDate::parse)
					.orElse(null);

			List<InterviewScheduleEntity> schedules = interviewScheduleRepository
					.findByInterviewDateAfter(LocalDate.now());

			if (schedules.isEmpty()) {
				return ApiResponse.success("Upcoming Interviews fetched successfully", Collections.emptyList(), 0);
			}

			List<Integer> applicantIds = schedules.stream().map(InterviewScheduleEntity::getApplicantId).distinct()
					.toList();

			List<JobApplicationEntity> applications = jobApplicationRepository.findByIdIn(applicantIds);

			Map<Integer, JobApplicationEntity> applicationMap = applications.stream()
					.collect(Collectors.toMap(JobApplicationEntity::getId, Function.identity()));

			List<Integer> jobIds = applications.stream().map(JobApplicationEntity::getJobId).distinct().toList();

			List<CreateJobDetailsEntity> jobs = createJobDetailsRepository.findByJobIdIn(jobIds);

			Map<Integer, CreateJobDetailsEntity> jobMap = jobs.stream()
					.collect(Collectors.toMap(CreateJobDetailsEntity::getJobId, Function.identity()));

			List<Integer> departmentIds = jobs.stream().map(CreateJobDetailsEntity::getDepartmentId).distinct()
					.toList();

			List<DepartmentsEntity> departments = departmentsRepository.findByIdIn(departmentIds);

			Map<Integer, DepartmentsEntity> departmentMap = departments.stream()
					.collect(Collectors.toMap(DepartmentsEntity::getId, Function.identity()));

			List<InterviewCurrentStageEntity> currentStages = interviewCurrentStageRepository
					.findByApplicationIdIn(applicantIds);

			Map<Integer, InterviewCurrentStageEntity> currentStageMap = currentStages.stream()
					.collect(Collectors.toMap(InterviewCurrentStageEntity::getApplicationId, Function.identity()));

			List<Integer> planIds = jobs.stream().map(CreateJobDetailsEntity::getPlanId).filter(Objects::nonNull)
					.distinct().toList();

			List<InterviewRoundEntity> rounds = interviewRoundRepository.findByInterviewPlan_IdIn(planIds);

			Map<RoundKey, InterviewRoundEntity> roundMap = rounds.stream().collect(Collectors
					.toMap(r -> new RoundKey(r.getInterviewPlan().getId(), r.getStageTypeId()), Function.identity()));

			List<InterviewUpcomingListResponse> response = new ArrayList<>();

			for (InterviewScheduleEntity schedule : schedules) {

				JobApplicationEntity application = applicationMap.get(schedule.getApplicantId());

				if (application == null)
					continue;

				CreateJobDetailsEntity job = jobMap.get(application.getJobId());

				if (job == null)
					continue;

				DepartmentsEntity department = departmentMap.get(job.getDepartmentId());

				InterviewCurrentStageEntity currentStage = currentStageMap.get(application.getId());

				InterviewRoundEntity round = roundMap.get(new RoundKey(job.getPlanId(), schedule.getRoundId()));

				InterviewUpcomingListResponse dto = new InterviewUpcomingListResponse();

				dto.setScheduleId(schedule.getId());
				dto.setApplicantId(application.getId());
				dto.setCandidateName(application.getFirstName() + " " + application.getLastName());

				dto.setJobTitle(job.getJobTitle());

				dto.setDepartment(department != null ? department.getDepartmentName() : null);

				dto.setRound(round != null ? round.getStageName() : null);

				dto.setInterviewMode(round != null ? round.getInterviewMode() : null);
				dto.setInterviewMode(round != null ? round.getInterviewMode() : null);
				long totalRounds = 0;

				if (job.getPlanId() != null) {

					totalRounds = rounds.stream().filter(r -> r.getInterviewPlan().getId().equals(job.getPlanId()))
							.count();
				}

				if (currentStage != null) {

					dto.setRoundProgress("Round " + currentStage.getRoundOrder() + " of " + totalRounds);
				}

				dto.setInterviewDate(schedule.getInterviewDate());
				dto.setStartTime(schedule.getStartTime());
				dto.setEndTime(schedule.getEndTime());
				dto.setMeetingLink(schedule.getMeetingLink());
				dto.setVenueDetails(schedule.getVenueDetails());

				boolean matches = true;

				if (!search.isBlank()) {

					String candidateName = dto.getCandidateName() == null ? "" : dto.getCandidateName().toLowerCase();

					String jobTitle = dto.getJobTitle() == null ? "" : dto.getJobTitle().toLowerCase();

					matches = candidateName.contains(search) || jobTitle.contains(search);
				}

				if (!matches)
					continue;

				if (departmentId != 0) {

					if (department == null || !departmentId.equals(department.getId())) {

						continue;
					}

				}

				if (roundId != 0) {

					if (schedule.getRoundId() == null || !roundId.equals(schedule.getRoundId())) {

						continue;
					}

				}

				if (!interviewMode.isBlank()) {

					String mode = dto.getInterviewMode() == null ? "" : dto.getInterviewMode().toLowerCase();

					if (!mode.equals(interviewMode)) {

						continue;

					}

				}

				if (interviewDate != null) {

					if (!interviewDate.equals(dto.getInterviewDate())) {

						continue;

					}

				}

				response.add(dto);

			}

			Comparator<InterviewUpcomingListResponse> comparator;

			switch (request.getSortBy()) {

			case "candidateName":

				comparator = Comparator.comparing(InterviewUpcomingListResponse::getCandidateName,
						Comparator.nullsLast(String::compareToIgnoreCase));

				break;

			case "jobTitle":

				comparator = Comparator.comparing(InterviewUpcomingListResponse::getJobTitle,
						Comparator.nullsLast(String::compareToIgnoreCase));

				break;

			case "department":

				comparator = Comparator.comparing(InterviewUpcomingListResponse::getDepartment,
						Comparator.nullsLast(String::compareToIgnoreCase));

				break;

			case "interviewDate":

				comparator = Comparator.comparing(InterviewUpcomingListResponse::getInterviewDate);

				break;

			default:

				comparator = Comparator.comparing(InterviewUpcomingListResponse::getScheduleId);

			}

			if ("desc".equalsIgnoreCase(request.getDirection())) {

				comparator = comparator.reversed();

			}

			response.sort(comparator);

			int total = response.size();

			int start = request.getPage() * request.getSize();

			if (start > total) {

				start = total;

			}

			int end = Math.min(start + request.getSize(), total);

			List<InterviewUpcomingListResponse> pageData = response.subList(start, end);

			return ApiResponse.success("Upcoming Interviews fetched successfully", pageData, total);

		} catch (Exception e) {

			log.error("Error while fetching interview schedules", e);

			return ApiResponse.failure(ResponseCode.FAILURE, List.of(e.getMessage()));
		}
	}

	@Override
	public ApiResponse<?> getInterviewSummary(Integer applicationId) {

		try {

			List<Object[]> result = interviewScheduleRepository.getInterviewSummary(applicationId);

			if (result == null || result.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Interview summary not found");
			}

			InterviewSummaryResponse response = mapInterviewSummary(result.get(0));

			return ApiResponse.success(ResponseCode.SUCCESS, "Interview summary fetched successfully", response);

		} catch (Exception e) {
			log.error("Error while fetching interview summary", e);
			return ApiResponse.failure(ResponseCode.FAILURE, List.of(e.getMessage()));
		}
	}

	private InterviewSummaryResponse mapInterviewSummary(Object[] obj) {

		InterviewSummaryResponse dto = new InterviewSummaryResponse();

		dto.setJobTitle((String) obj[0]);

		dto.setDepartment((String) obj[1]);

		dto.setRound((String) obj[2]);

		dto.setInterviewMode((String) obj[3]);

		dto.setInterviewType((String) obj[4]);

		dto.setEmploymentType((String) obj[5]);

		dto.setLocation((String) obj[6]);

		dto.setWorkMode((String) obj[7]);

		dto.setExperienceRequired((String) obj[8]);

		dto.setCandidateName((String) obj[9]);

		dto.setEmail((String) obj[10]);

		dto.setPhone((String) obj[11]);

		dto.setCurrentOrganization((String) obj[12]);

		dto.setCurrentLocation((String) obj[13]);

		dto.setTotalExperience((String) obj[14]);

		dto.setNoticePeriod((String) obj[15]);

		dto.setCurrentStage((String) obj[16]);

		dto.setInterviewCompletedOn((LocalDateTime) obj[17]);

		dto.setSalary((Integer) obj[18]);

		dto.setRoundId((Integer) obj[19]);

		dto.setJobId((Integer) obj[20]);

		return dto;
	}

	@Override
	public ApiResponse<?> getInterviewScheduleDetailsById(Integer scheduleId) {
		log.info("InterviewPlanServiceImpl :: Inside the getInterviewScheduleDetailsById");

		InterviewScheduleDetailsResponse response = new InterviewScheduleDetailsResponse();
		try {
			Optional<InterviewScheduleEntity> entity = interviewScheduleRepository.findById(scheduleId);

			if (entity == null || entity.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Interview Schedule Details not found");
			}

			InterviewScheduleEntity interviewScheduleEntity = entity.get();

			response.setInterviewDate(interviewScheduleEntity.getInterviewDate());

			response.setStartTime(interviewScheduleEntity.getStartTime());

			response.setEndTime(interviewScheduleEntity.getEndTime());

			response.setInterviewType(interviewScheduleEntity.getMeetingLink() != null ? "Online" : "Offline");

			return ApiResponse.success(ResponseCode.SUCCESS, "Interview schedule details fetched successfully",
					response);

		} catch (Exception e) {

			log.error("Error while fetching interview schedule details", e);

			return ApiResponse.failure(ResponseCode.FAILURE, List.of(e.getMessage()));
		}
	}

	@Override
	public ApiResponse<?> rescheduleInterview(RescheduleInterviewRequest request) {
		Optional<InterviewScheduleEntity> scheduleEntity = interviewScheduleRepository
				.findById(request.getScheduleId());
		if (scheduleEntity == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "schedule details not found");
		}
		InterviewScheduleEntity entity = scheduleEntity.get();
		entity.setRescheduleDate(request.getRescheduleDate());
		entity.setRescheduleVenueDetails(request.getRescheduleVenueDetails());
		entity.setRescheduleMeetingLink(request.getRescheduleMeetingLink());
		entity.setRescheduleStartTime(request.getRescheduleStartTime());
		entity.setRescheduleEndTime(request.getRescheduleEndTime());
		Integer applicationId = entity.getApplicantId();
		Integer roundId = entity.getRoundId();

		interviewScheduleRepository.save(entity);

		InterviewCurrentStageEntity currentStageEntity = interviewCurrentStageRepository
				.findByApplicationIdAndCurrentStageType(applicationId, roundId);

		currentStageEntity.setInterviewDate(request.getRescheduleDate());
		currentStageEntity.setStartTime(request.getRescheduleStartTime());
		currentStageEntity.setEndTime(request.getRescheduleEndTime());
		interviewCurrentStageRepository.save(currentStageEntity);

		// Fetch applicant
		JobApplicationEntity applicant = jobApplicationRepository.findById(entity.getApplicantId()).orElse(null);

		Integer jobId = applicant.getJobId();

		Optional<InterviewerAssignmentEntity> interviewerAssignments = interviewerAssignmentRepository
				.findByJobIdAndStageTypeId(jobId, entity.getRoundId());

		InterviewerAssignmentEntity interviewerAssignmentEntity = null;

		if (interviewerAssignments.isPresent()) {
			interviewerAssignmentEntity = interviewerAssignments.get();
		}

		// Send mail
		try {

			if (applicant != null && applicant.getEmail() != null) {

				String subject = Constants.INTERVIEW_RESCHEDULE_SUBJECT;

				String body = String.format(
				        Constants.INTERVIEW_RESCHEDULE_BODY,
				        applicant.getFirstName(),
				        interviewerAssignmentEntity.getJobTitle(),
				        interviewerAssignmentEntity.getStageName(),
				        request.getRescheduleDate(),
				        request.getRescheduleStartTime(),
				        request.getRescheduleEndTime(),
				        request.getRescheduleMeetingLink() != null ? "Online" : "Offline",
				        request.getRescheduleMeetingLink() != null
				                ? request.getRescheduleMeetingLink()
				                : request.getRescheduleVenueDetails()
				);
				mailService.sendMail(fromEmail, applicant.getEmail(), null, subject, body, null);

			}

		} catch (Exception e) {

			log.error("InterviewPlanServiceImpl::Mail failed : {}", e.getMessage());

		}

		// Send mail to Interviewer
		try {

			if (interviewerAssignmentEntity != null) {

				Integer interviewerUserId = interviewerAssignmentEntity.getInterviewerUserId().intValue();

				// Fetch interviewer details
				UserEntity interviewer = userRepository.findById(interviewerUserId).orElse(null);

				if (interviewer != null && interviewer.getEmail() != null) {

					String subject = Constants.INTERVIEW_RESCHEDULE_SUBJECT;

					String body = String.format(
					        Constants.INTERVIEWER_RESCHEDULE_BODY,
					        interviewerAssignmentEntity.getInterviewerName(),
					        applicant.getFirstName(),
					        interviewerAssignmentEntity.getJobTitle(),
					        interviewerAssignmentEntity.getStageName(),
					        request.getRescheduleDate(),
					        request.getRescheduleStartTime(),
					        request.getRescheduleEndTime(),
					        request.getRescheduleMeetingLink() != null ? "Online" : "Offline",
					        request.getRescheduleMeetingLink() != null
					                ? request.getRescheduleMeetingLink()
					                : request.getRescheduleVenueDetails()
					);

					mailService.sendMail(fromEmail, interviewer.getEmail(), null, subject, body, null);
				}
			}

		} catch (Exception e) {

			log.error("InterviewPlanServiceImpl::Interviewer Mail failed : {}", e.getMessage());

		}

		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Interview Rescheduled successfully");

	}

	@Override
	public ApiResponse<?> updateInterviewCompletionStatus(UpdateInterviewCompletionStatusRequest request) {

		log.info("InterviewPlanServiceImpl :: Inside updateInterviewCompletionStatus");

		// Validations
		Integer applicationId = request.getApplicantId();
		String status = request.getStatus();

		if (applicationId == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "ApplicantId is required");
		}

		if (status == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Status is required");
		}

		if (!(status.equalsIgnoreCase("HOLD") || status.equalsIgnoreCase("ACCEPTED")
				|| status.equalsIgnoreCase("REJECTED"))) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid Status");
		}

		JobApplicationEntity application = jobApplicationRepository.findById(request.getApplicantId()).orElse(null);

		if (application == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Applicant Not Found");
		}

		String currentStatus = application.getInterviewCompletionStatus();

		if ("ACCEPTED".equalsIgnoreCase(currentStatus) || "REJECTED".equalsIgnoreCase(currentStatus)) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Interview completion status cannot be modified.");
		}

		application.setInterviewCompletionStatus(request.getStatus());

		jobApplicationRepository.save(application);

		if ("ACCEPTED".equalsIgnoreCase(request.getStatus())) {

			sendInterviewSelectedMail(application);

		} else if ("REJECTED".equalsIgnoreCase(request.getStatus())) {

			sendInterviewRejectedMail(application);
		}

		log.info("InterviewPlanServiceImpl :: Exit updateInterviewCompletionStatus");

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview completion status updated successfully", null);
	}

	private void sendInterviewSelectedMail(JobApplicationEntity application) {

		log.info("InterviewPlanServiceImpl :: Inside sendInterviewSelectedMail");

		CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

		if (job == null) {
			throw new RuntimeException("Job Title Not Found");
		}

		String jobTitle = job.getJobTitle();

		String subject = Constants.INTERVIEW_SELECTED_SUBJECT;

		String body = String.format(Constants.INTERVIEW_SELECTED_BODY, application.getFirstName(), jobTitle);

		iMailService.sendMail(fromEmail, application.getEmail(), null, subject, body, null);
	}

	private void sendInterviewRejectedMail(JobApplicationEntity application) {

		log.info("InterviewPlanServiceImpl :: Inside sendInterviewRejectedMail");

		CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

		if (job == null) {
			throw new RuntimeException("Job Details Not Found");
		}

		String jobTitle = job.getJobTitle();

		String subject = Constants.INTERVIEW_REJECTED_SUBJECT;

		String body = String.format(Constants.INTERVIEW_REJECTED_BODY, application.getFirstName(), jobTitle);

		iMailService.sendMail(fromEmail, application.getEmail(), null, subject, body, null);
	}

	@Override
	public ApiResponse<?> interviewComplete(InterviewCompleteRequest request) {
		log.info("InterviewPlanServiceImpl :: Inside InterviewCompleteMethod");
		InterviewCurrentStageEntity currentStageEntity = interviewCurrentStageRepository
				.findByApplicationIdAndCurrentStageType(request.getApplicantId(), request.getCurrentStageType());
		log.info("localDateNow is" + LocalDate.now());
		log.info("current stage entity interview date is" + currentStageEntity.getInterviewDate());
		if (currentStageEntity.getInterviewDate().isEqual(LocalDate.now())) {

			currentStageEntity.setInterviewCompleted(request.getInterviewCompleted());
			currentStageEntity.setInterviewCompletedOn(request.getInterviewCompletedOn());
			interviewCurrentStageRepository.save(currentStageEntity);
			log.info("InterviewPlanServiceImpl :: Exit from InterviewCompleteMethod");
			return ApiResponse.success(ResponseCode.SUCCESS, "success", "Interview Completed successfully");

		} else {
			log.info("InterviewPlanServiceImpl :: Exit from InterviewCompleteMethod");
			return ApiResponse.failure(ResponseCode.FAILURE, "The interview is not scheduled for today");
		}

	}

	@Override
	public ApiResponse<?> getApplicantFeedbackById(ApplicantFeedBackRequest request) {

		log.info("InterviewPlanServiceImpl :: Inside the getApplicantFeedBackById");

		String authHeader = httpServletRequest.getHeader("Authorization");
		String token = authHeader.substring(7);

		Long userId = jwtService.extractUserId(token);
		Integer userIdFromToken = userId.intValue();

		InterviewFeedbackEntity entity = interviewFeedbackRepository
				.findByApplicantIdAndCurrentStageId(request.getApplicantId(), request.getCurrentStageId());
		ApplicantFeedBackResponse response = new ApplicantFeedBackResponse();
		if (userIdFromToken != entity.getUserId()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Your are authorised person to view the details");
		}
		
		BeanUtils.copyProperties(entity, response);

		log.info("InterviewPlanServiceImpl :: Exit from the getApplicantFeedBackById");
		return ApiResponse.success(ResponseCode.SUCCESS, "Applicant feedback details fetched successfully", response);
	}

}

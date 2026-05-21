package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.hms.service.dto.NotificationEvent;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.ChildLinkCommentsEntity;
import com.hms.service.entity.FunctionalityEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.ChildLinkCommentsRepository;
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.ApprovalChainRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateApprovalChainRequest;
import com.hms.service.response.ApprovalChainResponse;
import com.hms.service.response.CommentTimelineResponse;
import com.hms.service.service.IApprovalChainService;
import com.hms.service.service.INotificationService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class ApprovalChainServiceImpl implements IApprovalChainService {

	@Autowired
	private ApprovalChainRepository approvalChainRepository;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private INotificationService notificationService;

	@Autowired
	private AssignRolesRepository assignRolesRepository;

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private ChildLinkCommentsRepository childLinkCommentsRepository;

	@Override
	public ApiResponse<?> getApprovalChainCounts() {

		log.info("ApprovalChainServiceImpl:: Inside getApprovalChainCounts");

		Long total = approvalChainRepository.count();
		Long approved = approvalChainRepository.countByApprovalIgnoreCase("APPROVED");
		Long rejected = approvalChainRepository.countByApprovalIgnoreCase("REJECTED");
		Long pending = approvalChainRepository.countByApprovalIgnoreCase("IN_PROGRESS");

		Long active = approvalChainRepository.countByStatusIgnoreCase("ACTIVE");
		Long deactive = approvalChainRepository.countByStatusIgnoreCase("DEACTIVE");
		Long totalFunctionalities = functionalityRepository.count();

		Long chainCreatedCount = functionalityRepository.countByIsChaincreatedTrue();

		Map<String, Object> response = new HashMap<>();

		response.put("total", total);
		response.put("approved", approved);
		response.put("pending", pending);
		response.put("rejected", rejected);

		response.put("active", active);
		response.put("deactive", deactive);
		response.put("totalFunctionalities", totalFunctionalities);
		response.put("chainCreated", chainCreatedCount);

		log.info("ApprovalChainServiceImpl:: Exit getApprovalChainCounts");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	public ApiResponse<?> getApprovalChainById(Integer id) {

		log.info("ApprovalChainServiceImpl:: Inside getApprovalChainById with id: {}", id);

		if (id == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Id must not be null"));
		}

		ApprovalChainEntity entity = approvalChainRepository.findById(id).orElse(null);

		if (entity == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure",
					List.of("Approval Chain not found with id: " + id));
		}

		ApprovalChainResponse response = new ApprovalChainResponse();

		BeanUtils.copyProperties(entity, response);

		response.setLevels(entity.getLevelConfig() != null ? entity.getLevelConfig().size() : 0);

		response.setLevelConfig(entity.getLevelConfig());

		if (entity.getFunctionality() != null) {

			Optional<FunctionalityEntity> functionalityOptional = functionalityRepository
					.findById(entity.getFunctionality());

			functionalityOptional
					.ifPresent(functionality -> response.setFunctionalityName(functionality.getFunctionalityName()));
		}

		List<ChildLinkCommentsEntity> commentsList = childLinkCommentsRepository.findByChainId(entity.getId());

		List<CommentTimelineResponse> timeline = commentsList.stream().map(comment -> {

			CommentTimelineResponse dto = new CommentTimelineResponse();

			dto.setAction(comment.getAction());
			dto.setComments(comment.getComments());
			dto.setDescription(comment.getDescription());
			dto.setCreatedAt(comment.getCreatedAt());
			dto.setCreatedBy(comment.getCreatedBy());

			return dto;

		}).toList();

		response.setCommentTimeline(timeline);

		log.info("ApprovalChainServiceImpl:: Exit getApprovalChainById");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	@Transactional
	public ApiResponse<?> createApprovalChain(ApprovalChainRequest request) {

		log.info("ApprovalChainServiceImpl::Inside the createApprovalChain method");

		ApprovalChainEntity chainName = approvalChainRepository.findByChainNameIgnoreCase(request.getChainName());
		if (chainName != null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Chain Name already exists");

		}
		ApprovalChainEntity approvalChainEntity = new ApprovalChainEntity();

		ChildLinkCommentsEntity childLinkCommentsEntity = new ChildLinkCommentsEntity();

		approvalChainEntity.setChainName(request.getChainName());
		approvalChainEntity.setDescription(request.getDescription());
		approvalChainEntity.setStatus(request.getStatus());
		if (functionalityRepository.existsById(request.getFunctionality())) {
			approvalChainEntity.setFunctionality(request.getFunctionality());
		} else {

			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Functionality is not matched"));
		}

		String authHeader = httpServletRequest.getHeader("Authorization");
		String userName = "";
		String roleName = "";
		Long userId= null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			userName = jwtService.extractUsernameFromClaims(token);
			roleName = jwtService.extractRole(token);
			userId = jwtService.extractUserId(token);

		}
		
		Integer makerRoleId=assignRolesRepository.findByUserId(userId.intValue()).get().getRoleId();
		log.info("The username is :" + userName);
		log.info("the role name is :" + roleName);
		approvalChainEntity.setCreatedBy(userName);
		approvalChainEntity.setUserId(userId.intValue());
		approvalChainEntity.setLevelConfig(request.getLevelConfig());
		approvalChainEntity.setApproval("In_Progress");
		approvalChainEntity.setRequestType("Chain Created");

		approvalChainEntity.setCreatedAt(LocalDate.now());

		Optional<FunctionalityEntity> functionalityEntity = functionalityRepository
				.findById(request.getFunctionality());
		FunctionalityEntity functionality = functionalityEntity.get();
		functionality.setIsChaincreated(true);
		functionalityRepository.save(functionality);
		approvalChainEntity.setFunctionalityName(functionality.getFunctionalityName());

		approvalChainRepository.save(approvalChainEntity);

		childLinkCommentsEntity.setChainId(approvalChainEntity.getId());
		childLinkCommentsEntity.setAction("Create");
		childLinkCommentsEntity.setDescription(approvalChainEntity.getDescription());
		childLinkCommentsEntity.setCreatedBy(userName);
		childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
		

		childLinkCommentsRepository.save(childLinkCommentsEntity);

		// emails sending

		String email = userRepository.findByUserId(userId).get().getEmail();
		log.info("the emai is" + email);
		Map<Integer, List<String>> roleEmailMap = new HashMap<>();

		Integer roleId = rolesRepository.findByRoleNameIgnoreCase("Administrator").getRoleId();
		log.info("the role id is" + roleId);
		List<Integer> userIds = assignRolesRepository.findByRoleId(roleId).stream().map(AssignRolesEntity::getUserId)
				.toList();

		List<String> emails = userRepository.findByUserIdIn(userIds).stream().map(UserEntity::getEmail)
				.filter(Objects::nonNull).toList();

		roleEmailMap.put(roleId, emails);

		log.info("Role Email Map : {}", roleEmailMap);

		NotificationEvent event = new NotificationEvent();
		event.setProcessId(approvalChainEntity.getId().toString());

		log.info("mail sending started");
		event.setMakerEmailAddress(email);
		event.setMakerRoleName(roleName);
		event.setMakerNotificationTitle(Constants.CHAIN_CREATED_MAIL_SUBJECT);
		event.setDeptName(approvalChainEntity.getFunctionalityName());
		event.setMakerRoleId(makerRoleId);

		event.setMakerMessage("Approval Chain has been created successfully and submitted for approval.");
		event.setType("Chain Configurations");
		event.setMakerEmailBody(String.format(Constants.CHAIN_CREATED_SUCESSFULLY_MAIL_BODY, userName,
				approvalChainEntity.getId(), approvalChainEntity.getChainName(), approvalChainEntity.getDescription(),
				userName, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))));
		event.setCheckerMessage("A new Approval Chain is awaiting your review and approval.");
		event.setCheckerNotificationTitle(Constants.CHAIN_TO_BE_APPROVED_MAIL_SUBJECT);
		event.setCheckerEmailBody(String.format(Constants.CHAIN_TO_BE_APPROVED, approvalChainEntity.getId(),
				approvalChainEntity.getChainName(), approvalChainEntity.getDescription(), userName,
				LocalDateTime.now(ZoneId.of("Asia/Kolkata"))));

		event.setCheckerRoleName("Adminstrator");
		event.setRoleEmailMap(roleEmailMap);

		notificationService.callNotification(event);
		log.info("the event is " + event);

		log.info("ApprovalChainServiceImpl::Exit from the createApprovalChain method");
		return ApiResponse.success("Approval Chain Created Successfully");
	}

	private void sendWorkflowNotification(String processId, String type, String makerMessage, String department,

			String makerEmail, String makerRole, Integer makerRoleId,String makerTitle, String makerBody,

			String checkerRole, String checkerMessage, String checkerTitle, String checkerBody,

			Map<Integer, List<String>> roleEmailMap) {

		NotificationEvent event = new NotificationEvent();

		event.setProcessId(processId);
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

		log.info("the maker email is " + makerEmail);

		log.info("the role map contains " + roleEmailMap);

		notificationService.callNotification(event);
	}

	@Override
	public ApiResponse<?> updateApprovalChain(UpdateApprovalChainRequest request) {

		log.info("ApprovalChainServiceImpl::Inside updateApprovalChain");

		ApprovalChainEntity approvalChainEntity = approvalChainRepository.findById(request.getId())
				.orElseThrow(() -> new RuntimeException("Approval Chain not found"));

		ChildLinkCommentsEntity childLinkCommentsEntity = new ChildLinkCommentsEntity();

		String authHeader = httpServletRequest.getHeader("Authorization");

		String userName = "";
		String roleName = "";
		Long userId = null;
		String chainName = approvalChainEntity.getChainName();
		String description = approvalChainEntity.getDescription();
		String functionalityName = approvalChainEntity.getFunctionalityName();
		String createdBy = approvalChainEntity.getCreatedBy();
		Integer makerUserId=approvalChainEntity.getUserId();

		Integer chainId = approvalChainEntity.getId();
		
		Integer makerRoleId=assignRolesRepository.findByUserId(makerUserId).get().getRoleId();
		
		String makerRoleName=rolesRepository.findByRoleId(makerRoleId).get().getRoleName();

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userName = jwtService.extractUsernameFromClaims(token);
			roleName = jwtService.extractRole(token);
			userId = jwtService.extractUserId(token);
		}

		UserEntity creator = userRepository.findByUsername(approvalChainEntity.getCreatedBy());
		String creatorEmail = creator.getEmail();

		String approverEmail = userRepository.findByUserId(userId).map(UserEntity::getEmail).orElse(null);

		// Approval flow

		if (request.getApproval() != null) {

			if (!"Administrator".equalsIgnoreCase(roleName)) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Only Administrator can approve/reject");
			}

			String approval = request.getApproval().trim().toUpperCase();

			if ("APPROVED".equals(approval)) {

				approvalChainEntity.setApproval("Approved");
				approvalChainEntity.setStatus("ACTIVE");
				childLinkCommentsEntity.setChainId(chainId);
				childLinkCommentsEntity.setAction("Approve");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
						"Your Approval Chain has been approved successfully and is now active.", functionalityName,

						creatorEmail, makerRoleName,makerRoleId, Constants.CHAIN_APPROVED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_APPROVED_MAIL_BODY, createdBy, chainId, chainName, description,
								LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleName, "You have successfully approved the Approval Chain.",
						Constants.CHAIN_APPROVER_CONFIRMATION_SUBJECT,
						String.format(Constants.CHAIN_APPROVER_CONFIRMATION_BODY, userName, approvalChainEntity.getId(),
								chainName, description, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),
						roleEmailMap);

			} else if ("REJECTED".equals(approval)) {

				approvalChainEntity.setApproval("Rejected");
				//child link comments 
				childLinkCommentsEntity.setChainId(chainId);
				childLinkCommentsEntity.setAction("Reject");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
;
				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
						"Your Approval Chain has been rejected by the reviewer.", functionalityName,

						creatorEmail, makerRoleName,makerRoleId, Constants.CHAIN_REJECTED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_REJECTED_MAIL_BODY, createdBy, chainId, chainName, description,
								request.getComments(), LocalDateTime.now(ZoneId.of("Asia/Kolkata"))

						),

						roleName, "You have successfully rejected the Approval Chain.",
						Constants.CHAIN_REJECTION_CONFIRMATION_SUBJECT,
						String.format(Constants.CHAIN_REJECTION_CONFIRMATION_BODY, userName,
								approvalChainEntity.getId(), chainName, description,
								LocalDateTime.now(ZoneId.of("Asia/Kolkata")), null),

						roleEmailMap);
			}
		}

		// Deactivate Request

		if (request.getStatus() != null && "DEACTIVE".equalsIgnoreCase(request.getStatus())) {

			if (!approvalChainEntity.getCreatedBy().equalsIgnoreCase(userName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only creator can request deactivation");
			}

			approvalChainEntity.setApproval("In_Progress");

			approvalChainEntity.setRequestType("Chain-Deactive");
			approvalChainEntity.setDeactiveApproval(false);
			childLinkCommentsEntity.setChainId(chainId);
			childLinkCommentsEntity.setAction("Deactive");
			childLinkCommentsEntity.setDescription(request.getDescription());
			childLinkCommentsEntity.setCreatedBy(userName);
			childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

			// mail sent to all admins

			Map<Integer, List<String>> roleEmailMap = new HashMap<>();

			Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase("Administrator").getRoleId();

			List<Integer> userIds = assignRolesRepository.findByRoleId(adminRoleId).stream()
					.map(AssignRolesEntity::getUserId).toList();

			List<String> adminEmails = userRepository.findByUserIdIn(userIds).stream().map(UserEntity::getEmail)
					.filter(Objects::nonNull).distinct().collect(Collectors.toList());

			roleEmailMap.put(adminRoleId, adminEmails);

			sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
					"Approval Chain deactivation request submitted successfully.", functionalityName,

					creatorEmail, makerRoleName,makerRoleId, Constants.CHAIN_DEACTIVATION_REQUEST_MAIL_SUBJECT,
					String.format(Constants.CHAIN_DEACTIVATION_REQUEST_MAIL_BODY, userName, chainId, chainName,
							description, functionalityName, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

					"Administrator", "Approval Chain deactivation request is awaiting your approval.",
					Constants.CHAIN_DEACTIVATION_REQUEST_APPROVER_SUBJECT,
					String.format(Constants.CHAIN_DEACTIVATION_REQUEST_APPROVER_BODY, chainId, chainName, description,
							functionalityName, userName, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

					roleEmailMap);
		}

		// DEACTIVATION APPROVAL

		if (request.getDeactiveApproval() != null) {

			if (!"Administrator".equalsIgnoreCase(roleName)) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Only Administrator can process deactivation");
			}

			if (Boolean.TRUE.equals(request.getDeactiveApproval())) {

				approvalChainEntity.setStatus("DEACTIVE");
				approvalChainEntity.setApproval("Approved");
				approvalChainEntity.setDeactiveApproval(true);
				approvalChainEntity.setActiveApproval(false);

				// child table
				childLinkCommentsEntity.setChainId(chainId);
				childLinkCommentsEntity.setAction("Approve");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
						"Your Approval Chain has been deactivated successfully.", functionalityName,

						creatorEmail, makerRoleName,makerRoleId, Constants.CHAIN_DEACTIVATED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_DEACTIVATED_MAIL_BODY, createdBy, chainId, chainName, description,
								LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleName, "You have successfully approved the Approval Chain deactivation request.",
						Constants.CHAIN_DEACTIVE_APPROVER_SUBJECT,
						String.format(Constants.CHAIN_DEACTIVATED_MAIL_BODY, userName, chainId, chainName, description,
								functionalityName, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleEmailMap);

			} else {

				approvalChainEntity.setApproval("REJECTED");
				approvalChainEntity.setDeactiveApproval(false);
				// child table details

				childLinkCommentsEntity.setChainId(chainId);
				childLinkCommentsEntity.setAction("Reject");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
						"Your Approval Chain deactivation request has been rejected.", functionalityName,

						creatorEmail, makerRoleName, makerRoleId,Constants.CHAIN_DEACTIVE_REJECTED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_DEACTIVE_REJECTED_MAIL_BODY, createdBy, chainId, chainName,
								description, request.getComments(), LocalDateTime.now(ZoneId.of("Asia/Kolkata")), null),

						roleName, "You have successfully rejected the Approval Chain deactivation request.",
						Constants.CHAIN_DEACTIVE_REJECTION_CONFIRMATION_SUBJECT,
						String.format(Constants.CHAIN_DEACTIVE_REJECTION_CONFIRMATION_BODY, chainId, chainName,
								description, LocalDateTime.now(ZoneId.of("Asia/Kolkata")),
								request.getDeactivateComments()),
						roleEmailMap);
			}
		}

		// Activation Request

		if (request.getStatus() != null && "ACTIVE".equalsIgnoreCase(request.getStatus())) {

			if (!approvalChainEntity.getCreatedBy().equalsIgnoreCase(userName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only creator can request activation");
			}

			approvalChainEntity.setApproval("IN_PROGRESS");
			approvalChainEntity.setRequestType("Chain-Active");

			// child table details

			childLinkCommentsEntity.setChainId(chainId);
			childLinkCommentsEntity.setAction("Active");
			childLinkCommentsEntity.setDescription(request.getDescription());
			childLinkCommentsEntity.setCreatedBy(userName);
			childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

			Map<Integer, List<String>> roleEmailMap = new HashMap<>();

			Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase("Administrator").getRoleId();

			List<Integer> userIds = assignRolesRepository.findByRoleId(adminRoleId).stream()
					.map(AssignRolesEntity::getUserId).toList();

			List<String> adminEmails = userRepository.findByUserIdIn(userIds).stream().map(UserEntity::getEmail)
					.filter(Objects::nonNull).distinct().collect(Collectors.toList());

			roleEmailMap.put(adminRoleId, adminEmails);

			sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
					"Approval Chain activation request submitted successfully.", functionalityName,

					creatorEmail,makerRoleName,makerRoleId, Constants.CHAIN_ACTIVATION_REQUEST_MAIL_SUBJECT,
					String.format(Constants.CHAIN_ACTIVATION_REQUEST_MAIL_BODY, userName, approvalChainEntity.getId(),
							chainName, description, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

					"Administrator", "Approval Chain activation request is awaiting your approval.",
					Constants.CHAIN_ACTIVATION_REQUEST_APPROVER_SUBJECT,
					String.format(Constants.CHAIN_ACTIVATION_REQUEST_APPROVER_BODY, chainId, chainName, description,
							approvalChainEntity.getCreatedBy(), LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

					roleEmailMap);
		}

		// ACTIVATION APPROVAL

		if (request.getActiveApproval() != null) {

			if (!"Administrator".equalsIgnoreCase(roleName)) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Only Administrator can process activation");
			}

			if (Boolean.TRUE.equals(request.getActiveApproval())) {

				approvalChainEntity.setStatus("ACTIVE");
				approvalChainEntity.setApproval("Approved");
				approvalChainEntity.setActiveApproval(true);
				approvalChainEntity.setDeactiveApproval(false);
				// child table details

				childLinkCommentsEntity.setChainId(chainId);
				childLinkCommentsEntity.setAction("Approve");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
						"Your Approval Chain activation request has been approved successfully.", functionalityName,

						creatorEmail,makerRoleName,makerRoleId, Constants.CHAIN_ACTIVATED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_ACTIVATED_MAIL_BODY, createdBy, chainId, chainName, description,
								LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),

						roleName, "You have successfully approved the Approval Chain activation request.",
						Constants.CHAIN_ACTIVATE_APPROVER_SUBJECT,
						String.format(Constants.CHAIN_ACTIVATE_APPROVER_BODY, approvalChainEntity.getId(), chainName,
								description, LocalDateTime.now(ZoneId.of("Asia/Kolkata"))),
						roleEmailMap);

			} else {

				approvalChainEntity.setApproval("Rejected");
				approvalChainEntity.setActiveApproval(false);

				// child table details

				childLinkCommentsEntity.setChainId(chainId);
				childLinkCommentsEntity.setAction("Reject");
				childLinkCommentsEntity.setComments(request.getComments());
				childLinkCommentsEntity.setCreatedBy(userName);
				childLinkCommentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
						"Your Approval Chain activation request has been rejected.", functionalityName,

						creatorEmail, makerRoleName,makerRoleId, Constants.CHAIN_ACTIVATION_REJECTED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_ACTIVATION_REJECTED_MAIL_BODY, createdBy, chainId, chainName,
								description, LocalDateTime.now(ZoneId.of("Asia/Kolkata")), request.getComments()),

						roleName, "You have successfully rejected the Approval Chain activation request.",
						Constants.CHAIN_ACTIVATE_REJECTION_CONFIRMATION_SUBJECT,
						String.format(Constants.CHAIN_ACTIVATE_REJECTION_CONFIRMATION_BODY, userName,
								approvalChainEntity.getId(), chainName, description,
								LocalDateTime.now(ZoneId.of("Asia/Kolkata")), request.getActivateComments()),
						roleEmailMap);
			}
		}

		approvalChainEntity.setUpdatedBy(userName);
		approvalChainEntity.setUpdatedAt(LocalDate.now());

		approvalChainRepository.save(approvalChainEntity);

		childLinkCommentsRepository.save(childLinkCommentsEntity);

		return ApiResponse.success("Approval Chain Updated Successfully");
	}

	@Override
	public ApiResponse<?> getApprovalChainsList(SpecificationFilterRequest request) {

		log.info("ApprovalChainServiceImpl :: getApprovalChainsList started");

		if (request.getPage() == null || request.getSize() == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("page and size must be provided"));
		}

		if (request.getPage() < 0 || request.getSize() <= 0) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Invalid page or size values"));
		}

		Sort sort = Sort.by("DESC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
				request.getSortBy() != null ? request.getSortBy() : "updatedAt");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Specification<ApprovalChainEntity> listSpec = request.buildBaseSpec();

		Page<ApprovalChainEntity> pageResult = approvalChainRepository.findAll(listSpec, pageable);

		List<ApprovalChainResponse> responseList = pageResult.getContent().stream().map(this::mapToResponse).toList();

		Specification<ApprovalChainEntity> countSpec = request.buildCountSpec();

		Map<String, Object> counts = new LinkedHashMap<>();

		counts.put("total", approvalChainRepository.count(countSpec));

		counts.put("active", approvalChainRepository.count(countSpec.and(statusEquals("ACTIVE"))));

		counts.put("inactive", approvalChainRepository.count(countSpec.and(statusEquals("DEACTIVE"))));

		counts.put("approved", approvalChainRepository.count(countSpec.and(approvalEquals("APPROVED"))));

		counts.put("rejected", approvalChainRepository.count(countSpec.and(approvalEquals("REJECTED"))));

		counts.put("inProgress", approvalChainRepository.count(countSpec.and(approvalEquals("IN_PROGRESS"))));

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("approvalChains", responseList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalPages", pageResult.getTotalPages());
		response.put("totalElements", pageResult.getTotalElements());
		response.put("counts", counts);

		log.info("ApprovalChainServiceImpl :: getApprovalChainsList completed");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	private ApprovalChainResponse mapToResponse(ApprovalChainEntity entity) {

		ApprovalChainResponse response = new ApprovalChainResponse();

		response.setId(entity.getId());

		response.setChainName(entity.getChainName());

		response.setDescription(entity.getDescription());

		response.setStatus(entity.getStatus());

		response.setLevels(entity.getLevelConfig() != null ? entity.getLevelConfig().size() : 0);

		response.setUpdatedBy(entity.getUpdatedBy());

		response.setUpdatedAt(entity.getUpdatedAt());

		response.setCreatedAt(entity.getCreatedAt());

		response.setCreatedBy(entity.getCreatedBy());

		response.setApproval(entity.getApproval());

		response.setLevelConfig(entity.getLevelConfig());

		response.setFunctionality(entity.getFunctionality());

		response.setFunctionalityName(entity.getFunctionalityName());

//		response.setActivateComments(entity.getActivateComments());
//
//		response.setDeactivateComments(entity.getDeactivateComments());
//
//		response.setApprovedComments(entity.getApprovedComments());
//
//		response.setRejectedComments(entity.getRejectedComments());

		response.setRequestType(entity.getRequestType());

		return response;
	}

	private Specification<ApprovalChainEntity> approvalEquals(String value) {

		return (root, query, cb) -> cb.equal(cb.lower(root.get("approval")), value.toLowerCase());
	}

	private Specification<ApprovalChainEntity> statusEquals(String value) {

		return (root, query, cb) -> cb.equal(cb.lower(root.get("status")), value.toLowerCase());
	}
}

package com.hms.service.serviceImpl;

import java.time.LocalDate;
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

import com.hms.service.constants.Constants;
import com.hms.service.dto.NotificationEvent;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.FunctionalityEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.ApprovalChainRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateApprovalChainRequest;
import com.hms.service.response.ApprovalChainResponse;
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

	@Override
	public ApiResponse<?> getApprovalChainsList(SpecificationFilterRequest request) {

		log.info("ApprovalChainServiceImpl:: Inside getApprovalChainsList");

		if (request.getPage() == null || request.getSize() == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("page and size must be provided"));
		}

		if (request.getPage() < 0 || request.getSize() <= 0) {

			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Invalid page or size values"));
		}

		Sort sort = Sort.by(

				"DESC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,

				request.getSortBy() != null ? request.getSortBy() : "id");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Specification<ApprovalChainEntity> baseSpec = request.buildBaseSpec();

		Page<ApprovalChainEntity> pageResult = approvalChainRepository.findAll(baseSpec, pageable);

		List<ApprovalChainResponse> responseList = pageResult.getContent().stream()
				.map(entity -> new ApprovalChainResponse(

						entity.getId(),

						entity.getChainName(),

						entity.getDescription(),

						entity.getStatus(),

						entity.getLevelConfig() != null ? entity.getLevelConfig().size() : 0,

						entity.getUpdatedBy(),

						entity.getUpdatedAt(),

						entity.getCreatedAt(),

						entity.getCreatedBy(),

						entity.getApproval(),

						entity.getLevelConfig(),

						entity.getFunctionality(),

						entity.getFunctionalityName(),

						entity.getActivateComments(),

						entity.getDeactivateComments(),

						entity.getApprovedComments(),

						entity.getRejectedComments()))

				.toList();

		Specification<ApprovalChainEntity> countSpec = request.buildCountSpec();

		long totalCount = approvalChainRepository.count(countSpec);

		long approvedCount = approvalChainRepository.count(countSpec.and(approvalEquals("APPROVED")));

		long rejectedCount = approvalChainRepository.count(countSpec.and(approvalEquals("REJECTED")));

		long inProgressCount = approvalChainRepository.count(countSpec.and(approvalEquals("IN_PROGRESS")));

		long activeCount = approvalChainRepository.count(countSpec.and(statusEquals("ACTIVE")));

		long deactiveCount = approvalChainRepository.count(countSpec.and(statusEquals("DEACTIVE")));

		Map<String, Object> counts = new LinkedHashMap<>();

		counts.put("total", totalCount);

		counts.put("approved", approvedCount);

		counts.put("rejected", rejectedCount);

		counts.put("inProgress", inProgressCount);

		counts.put("active", activeCount);

		counts.put("deactive", deactiveCount);

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("approvalChains", responseList);

		response.put("currentPage", pageResult.getNumber());

		response.put("totalPages", pageResult.getTotalPages());

		response.put("totalElements", pageResult.getTotalElements());

		response.put("counts", counts);

		log.info("ApprovalChainServiceImpl:: Exit getApprovalChainsList");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	private Specification<ApprovalChainEntity> approvalEquals(String value) {

		return (r, q, c) -> c.equal(c.lower(r.get("approval")), value.toLowerCase());
	}

	private Specification<ApprovalChainEntity> statusEquals(String value) {

		return (r, q, c) -> c.equal(c.lower(r.get("status")), value.toLowerCase());
	}

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

			if (functionalityOptional.isPresent()) {

				response.setFunctionalityName(functionalityOptional.get().getFunctionalityName());
			}
		}

		log.info("ApprovalChainServiceImpl:: Exit getApprovalChainById");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	public ApiResponse<?> createApprovalChain(ApprovalChainRequest request) {

		log.info("ApprovalChainServiceImpl::Inside the createApprovalChain method");

		ApprovalChainEntity chainName = approvalChainRepository.findByChainNameIgnoreCase(request.getChainName());
		if (chainName != null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Chain Name already exists");

		}
		ApprovalChainEntity approvalChainEntity = new ApprovalChainEntity();

		approvalChainEntity.setChainName(request.getChainName());
		approvalChainEntity.setDescription(request.getDescription());
		approvalChainEntity.setStatus(request.getStatus());
		if (functionalityRepository.existsById(request.getFunctionality())) {
			approvalChainEntity.setFunctionality(request.getFunctionality());
		} else {
			log.info("BusinessUnit Id is required");
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Functionality is not matched"));
		}

		String authHeader = httpServletRequest.getHeader("Authorization");
		String userName = "";
		String roleName = "";
		Long userId = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			userName = jwtService.extractUsernameFromClaims(token);
			roleName = jwtService.extractRole(token);
			userId = jwtService.extractUserId(token);

		}
		log.info("The username is :" + userName);
		log.info("the role name is :" + roleName);
		approvalChainEntity.setCreatedBy(userName);
		approvalChainEntity.setLevelConfig(request.getLevelConfig());
		approvalChainEntity.setApproval("In_Progress");
		approvalChainEntity.setRequestType("Chain Created");

		approvalChainEntity.setCreatedAt(LocalDate.now());

		approvalChainRepository.save(approvalChainEntity);

		Optional<FunctionalityEntity> functionalityEntity = functionalityRepository
				.findById(request.getFunctionality());
		FunctionalityEntity functionality = functionalityEntity.get();
		functionality.setIsChaincreated(true);
		functionalityRepository.save(functionality);
		approvalChainEntity.setFunctionalityName(functionality.getFunctionalityName());

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
		event.setMakerMessage("chain created");
		event.setType("Chain Created");
		event.setMakerEmailBody(String.format(Constants.CHAIN_CREATED_SUCESSFULLY_MAIL_BODY, approvalChainEntity.getId(),approvalChainEntity.getFunctionalityName()));

		event.setCheckerNotificationTitle(Constants.CHAIN_APPROVED_MAIL_SUBJECT);
		event.setCheckerEmailBody(String.format(Constants.CHAIN_TO_BE_APPROVED, approvalChainEntity.getId(),
				approvalChainEntity.getFunctionalityName()));
		
		event.setCheckerRoleName("Adminstrator");
		event.setRoleEmailMap(roleEmailMap);

		notificationService.callNotification(event);
		log.info("the event is " + event);

		log.info("ApprovalChainServiceImpl::Exit from the createApprovalChain method");
		return ApiResponse.success("Approval Chain Created Successfully");
	}


	
	private void sendWorkflowNotification(String processId, String type, String message, String department,

			String makerEmail, String makerRole, String makerTitle, String makerBody,

			String checkerRole, String checkerTitle, String checkerBody,

			Map<Integer, List<String>> roleEmailMap) {

		NotificationEvent event = new NotificationEvent();

		event.setProcessId(processId);
		event.setType(type);

		// MAKER

		event.setMakerEmailAddress(makerEmail);
		event.setMakerRoleName(makerRole);
		event.setMakerNotificationTitle(makerTitle);
		event.setMakerEmailBody(makerBody);

		// CHECKER

		event.setCheckerRoleName(checkerRole);
		event.setCheckerNotificationTitle(checkerTitle);
		event.setCheckerEmailBody(checkerBody);

		event.setDeptName(department);
		event.setCheckerMessage(message);

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

		String authHeader = httpServletRequest.getHeader("Authorization");

		String userName = "";
		String roleName = "";
		Long userId = null;

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
				approvalChainEntity.setApprovedComments(request.getApprovedComments());

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
						"Approval Chain Approved", "Chain Configurations",

						creatorEmail, "System Admin", Constants.CHAIN_APPROVED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_APPROVED_MAIL_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleName, Constants.CHAIN_APPROVER_CONFIRMATION_SUBJECT,
						String.format(Constants.CHAIN_APPROVER_CONFIRMATION_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleEmailMap);

			} else if ("REJECTED".equals(approval)) {

				approvalChainEntity.setApproval("Rejected");
				approvalChainEntity.setRejectedComments(request.getRejectedComments());
				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
						"Approval Chain Rejected", "Chain Configurations",

						creatorEmail, "System Admin", Constants.CHAIN_REJECTED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_REJECTED_MAIL_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleName, Constants.CHAIN_REJECTION_CONFIRMATION_SUBJECT,
						String.format(Constants.CHAIN_REJECTION_CONFIRMATION_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleEmailMap);
			}
		}

		// Deactivate Request

		if (request.getStatus() != null && "DEACTIVE".equalsIgnoreCase(request.getStatus())) {

			if (!approvalChainEntity.getCreatedBy().equalsIgnoreCase(userName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only creator can request deactivation");
			}

			approvalChainEntity.setApproval("In_Progress");
			approvalChainEntity.setDeactivateComments(request.getDeactivateComments());
			approvalChainEntity.setRequestType("Chain-Deactive");
			approvalChainEntity.setDeactiveApproval(false);

			// mail sent to all admins

			Map<Integer, List<String>> roleEmailMap = new HashMap<>();

			Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase("Administrator").getRoleId();

			List<Integer> userIds = assignRolesRepository.findByRoleId(adminRoleId).stream()
					.map(AssignRolesEntity::getUserId).toList();

			List<String> adminEmails = userRepository.findByUserIdIn(userIds).stream().map(UserEntity::getEmail)
					.filter(Objects::nonNull).distinct().collect(Collectors.toList());

			roleEmailMap.put(adminRoleId, adminEmails);

			sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
					"Deactivation Request Raised", "Chain Configurations",

					creatorEmail, roleName, Constants.CHAIN_DEACTIVATION_REQUEST_MAIL_SUBJECT,
					String.format(Constants.CHAIN_DEACTIVATION_REQUEST_MAIL_BODY, approvalChainEntity.getId(),
							approvalChainEntity.getFunctionalityName()),

					"Administrator", Constants.CHAIN_DEACTIVATION_REQUEST_APPROVER_SUBJECT,
					String.format(Constants.CHAIN_DEACTIVATION_REQUEST_APPROVER_BODY, approvalChainEntity.getId(),
							approvalChainEntity.getFunctionalityName()),

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
				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW", "Chain Deactivated",
						"Chain Configuration",

						creatorEmail, "System Admin", Constants.CHAIN_DEACTIVATED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_DEACTIVATED_MAIL_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleName, Constants.CHAIN_DEACTIVE_APPROVER_SUBJECT,
						String.format(Constants.CHAIN_DEACTIVE_APPROVER_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleEmailMap);

			} else {

				approvalChainEntity.setApproval("REJECTED");

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
						"Deactivation Rejected", "Chain Configurations",

						creatorEmail, "System Admin", Constants.CHAIN_DEACTIVE_REJECTED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_DEACTIVE_REJECTED_MAIL_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleName, Constants.CHAIN_DEACTIVE_REJECTION_CONFIRMATION_SUBJECT,
						String.format(Constants.CHAIN_DEACTIVE_REJECTION_CONFIRMATION_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleEmailMap);
			}
		}

		// Activation Request

		if (request.getStatus() != null && "ACTIVE".equalsIgnoreCase(request.getStatus())) {

			if (!approvalChainEntity.getCreatedBy().equalsIgnoreCase(userName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only creator can request activation");
			}

			approvalChainEntity.setApproval("IN_PROGRESS");
			approvalChainEntity.setActivateComments(request.getActivateComments());
			approvalChainEntity.setRequestType("Chain-Active");

			Map<Integer, List<String>> roleEmailMap = new HashMap<>();

			Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase("Administrator").getRoleId();

			List<Integer> userIds = assignRolesRepository.findByRoleId(adminRoleId).stream()
					.map(AssignRolesEntity::getUserId).toList();

			List<String> adminEmails = userRepository.findByUserIdIn(userIds).stream().map(UserEntity::getEmail)
					.filter(Objects::nonNull).distinct().collect(Collectors.toList());

			roleEmailMap.put(adminRoleId, adminEmails);

			sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
					"Activation Request Raised", "Chain Configurations",

					creatorEmail, roleName, Constants.CHAIN_ACTIVATION_REQUEST_MAIL_SUBJECT,
					String.format(Constants.CHAIN_ACTIVATION_REQUEST_MAIL_BODY, approvalChainEntity.getId(),
							approvalChainEntity.getFunctionalityName()),

					"Administrator", Constants.CHAIN_ACTIVATION_REQUEST_APPROVER_SUBJECT,
					String.format(Constants.CHAIN_ACTIVATION_REQUEST_APPROVER_BODY, approvalChainEntity.getId(),
							approvalChainEntity.getFunctionalityName()),

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

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW", "Chain Activated",
						"Chain Configurations",

						creatorEmail, "System Admin", Constants.CHAIN_ACTIVATED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_ACTIVATED_MAIL_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleName, Constants.CHAIN_ACTIVATE_APPROVER_SUBJECT,
						String.format(Constants.CHAIN_ACTIVATE_APPROVER_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleEmailMap);

			} else {

				approvalChainEntity.setApproval("Rejected");

				Map<Integer, List<String>> roleEmailMap = new HashMap<>();

				Integer adminRoleId = rolesRepository.findByRoleNameIgnoreCase(roleName).getRoleId();

				roleEmailMap.put(adminRoleId, List.of(approverEmail));

				sendWorkflowNotification(approvalChainEntity.getId().toString(), "CHAIN_WORKFLOW",
						"Activation Rejected", "Chain Configurations",

						creatorEmail, "System Admin", Constants.CHAIN_ACTIVATION_REJECTED_MAIL_SUBJECT,
						String.format(Constants.CHAIN_ACTIVATION_REJECTED_MAIL_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleName, Constants.CHAIN_ACTIVATE_REJECTION_CONFIRMATION_SUBJECT,
						String.format(Constants.CHAIN_ACTIVATE_REJECTION_CONFIRMATION_BODY, approvalChainEntity.getId(),
								approvalChainEntity.getFunctionalityName()),

						roleEmailMap);
			}
		}

		approvalChainEntity.setUpdatedBy(userName);
		approvalChainEntity.setUpdatedAt(LocalDate.now());

		approvalChainRepository.save(approvalChainEntity);

		return ApiResponse.success("Approval Chain Updated Successfully");
	}

}

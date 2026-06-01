package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.ChildLinkCommentsEntity;
import com.hms.service.entity.FunctionalityEntity;
import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewRoundEntity;
import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.ApprovalsChildRepository;
import com.hms.service.repository.ChildLinkCommentsRepository;
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.repository.InterviewPlanRepository;
import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.InterviewRoundRequest;
import com.hms.service.request.LevelConfig;
import com.hms.service.request.UpdateInterviewPlanRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.IInterviewPlanService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InterviewPlanServiceImpl implements IInterviewPlanService {

	@Autowired
	private InterviewPlanRepository interviewPlanRepository;

	@Autowired
	private ChildLinkCommentsRepository childLinkCommentsRepository;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private ApprovalChainRepository approvalChainRepository;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private JwtService jwtService;

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
			entity.setApprovalStatus("InProgress");
			entity.setStatus("InProgress");
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

	@Override
	public ApiResponse<?> updateInterviewPlan(UpdateInterviewPlanRequest request, HttpServletRequest httpRequest) {

		log.info("InterviewPlanServiceImpl :: updateInterviewPlan");

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

		ChildLinkCommentsEntity commentsEntity = new ChildLinkCommentsEntity();

		// JWT DETAILS

		String authHeader = httpRequest.getHeader("Authorization");

		String token = authHeader.substring(7);

		String userName = jwtService.extractUsernameFromClaims(token);

		Long userId = jwtService.extractUserId(token);

		String roleName = jwtService.extractRole(token);

		// COMMON DETAILS

		String planName = interviewPlanEntity.getPlanName();

		String description = interviewPlanEntity.getDescription();

		String createdBy = interviewPlanEntity.getCreatedBy();

		Integer planId = interviewPlanEntity.getId();

		// APPROVE / REJECT

		if (request.getApproval() != null) {

			if (!"Administrator".equalsIgnoreCase(roleName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only Administrator can approve/reject");
			}

			String approval = request.getApproval().trim().toUpperCase();

			// APPROVE

			if ("APPROVED".equals(approval)) {

				interviewPlanEntity.setStatus("ACTIVE");

				interviewPlanEntity.setApprovalStatus("Approved");

				commentsEntity.setPlanId(planId);

				commentsEntity.setAction("Approve");

				commentsEntity.setComments(request.getComments());

				commentsEntity.setCreatedBy(userName);

				commentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				// MAIL & NOTIFICATION

//	            sendWorkflowNotification(
//	                    interviewPlanEntity.getId().toString(),
//	                    "INTERVIEW_PLAN_WORKFLOW",
//	                    "Interview Plan approved successfully",
//	                    "Interview Plan",
//
//	                    createdBy,
//	                    roleName,
//	                    1,
//
//	                    "Interview Plan Approved",
//
//	                    String.format(
//	                            "Interview Plan %s has been approved by %s",
//	                            planName,
//	                            userName),
//
//	                    roleName,
//	                    "Interview Plan approved",
//
//	                    "Interview Plan Approval Confirmation",
//
//	                    String.format(
//	                            "You approved Interview Plan %s",
//	                            planName),
//
//	                    new HashMap<>());
//
			}

			// REJECT

			else if ("REJECTED".equals(approval)) {

				interviewPlanEntity.setApprovalStatus("Rejected");

				commentsEntity.setPlanId(planId);

				commentsEntity.setAction("Reject");

				commentsEntity.setComments(request.getComments());

				commentsEntity.setCreatedBy(userName);

				commentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

				// MAIL & NOTIFICATION

//	            sendWorkflowNotification(
//	                    interviewPlanEntity.getId().toString(),
//	                    "INTERVIEW_PLAN_WORKFLOW",
//	                    "Interview Plan rejected",
//	                    "Interview Plan",
//
//	                    createdBy,
//	                    roleName,
//	                    1,
//
//	                    "Interview Plan Rejected",
//
//	                    String.format(
//	                            "Interview Plan %s rejected by %s",
//	                            planName,
//	                            userName),
//
//	                    roleName,
//	                    "Interview Plan rejected",
//
//	                    "Interview Plan Rejection Confirmation",
//
//	                    String.format(
//	                            "You rejected Interview Plan %s",
//	                            planName),
//
//	                    new HashMap<>());
			}

			else {

				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid approval value");
			}
		}

		// DEACTIVATION REQUEST

		if (request.getStatus() != null && "DEACTIVE".equalsIgnoreCase(request.getStatus())) {

			if (!interviewPlanEntity.getUserId().equals(userId.intValue())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only creator can request deactivation");
			}

			interviewPlanEntity.setApprovalStatus("In_Progress");

			interviewPlanEntity.setRequestType("Plan-Deactive");

			interviewPlanEntity.setDeactiveApproval(false);

			commentsEntity.setPlanId(planId);

			commentsEntity.setAction("Deactive");

			commentsEntity.setDescription(request.getDescription());

			commentsEntity.setCreatedBy(userName);

			commentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

//	        sendWorkflowNotification(
//	                interviewPlanEntity.getId().toString(),
//	                "INTERVIEW_PLAN_WORKFLOW",
//	                "Interview Plan deactivation request submitted",
//	                "Interview Plan",
//
//	                createdBy,
//	                "Administrator",
//	                1,
//
//	                "Interview Plan Deactivation Request",
//
//	                String.format(
//	                        "Deactivation requested for Interview Plan %s",
//	                        planName),
//
//	                "Administrator",
//	                "Approval pending",
//
//	                "Interview Plan Deactivation Approval",
//
//	                String.format(
//	                        "Please review deactivation request for %s",
//	                        planName),
//
//	                new HashMap<>());
		}

		// DEACTIVATION APPROVAL

		if (request.getDeactiveApproval() != null) {

			if (!"Administrator".equalsIgnoreCase(roleName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only Administrator can process deactivation");
			}

			// APPROVED

			if (Boolean.TRUE.equals(request.getDeactiveApproval())) {

				interviewPlanEntity.setStatus("DEACTIVE");

				interviewPlanEntity.setApprovalStatus("Approved");

				interviewPlanEntity.setDeactiveApproval(true);

				interviewPlanEntity.setActiveApproval(false);

				commentsEntity.setPlanId(planId);

				commentsEntity.setAction("Approve");

				commentsEntity.setComments(request.getComments());

				commentsEntity.setCreatedBy(userName);

				commentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

//	            sendWorkflowNotification(
//	                    interviewPlanEntity.getId().toString(),
//	                    "INTERVIEW_PLAN_WORKFLOW",
//	                    "Interview Plan deactivated successfully",
//	                    "Interview Plan",
//
//	                    createdBy,
//	                    roleName,
//	                    1,
//
//	                    "Interview Plan Deactivated",
//
//	                    String.format(
//	                            "Interview Plan %s deactivated",
//	                            planName),
//
//	                    roleName,
//	                    "Deactivation approved",
//
//	                    "Interview Plan Deactivation Confirmation",
//
//	                    String.format(
//	                            "You approved deactivation for %s",
//	                            planName),
//
//	                    new HashMap<>());
			}

			// REJECTED

			else {

				interviewPlanEntity.setApprovalStatus("REJECTED");

				interviewPlanEntity.setDeactiveApproval(false);

				commentsEntity.setPlanId(planId);

				commentsEntity.setAction("Reject");

				commentsEntity.setComments(request.getComments());

				commentsEntity.setCreatedBy(userName);

				commentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

//	            sendWorkflowNotification(
//	                    interviewPlanEntity.getId().toString(),
//	                    "INTERVIEW_PLAN_WORKFLOW",
//	                    "Interview Plan deactivation rejected",
//	                    "Interview Plan",
//
//	                    createdBy,
//	                    roleName,
//	                    1,
//
//	                    "Interview Plan Deactivation Rejected",
//
//	                    String.format(
//	                            "Interview Plan deactivation rejected for %s",
//	                            planName),
//
//	                    roleName,
//	                    "Deactivation rejected",
//
//	                    "Interview Plan Deactivation Rejection",
//
//	                    String.format(
//	                            "You rejected deactivation for %s",
//	                            planName),
//
//	                    new HashMap<>());
			}
		}

		// ACTIVATION REQUEST

		if (request.getStatus() != null && "ACTIVE".equalsIgnoreCase(request.getStatus())) {

			if (!interviewPlanEntity.getUserId().equals(userId.intValue())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only creator can request activation");
			}

			interviewPlanEntity.setApprovalStatus("IN_PROGRESS");

			interviewPlanEntity.setRequestType("Plan-Active");

			commentsEntity.setPlanId(planId);

			commentsEntity.setAction("Active");

			commentsEntity.setDescription(request.getDescription());

			commentsEntity.setCreatedBy(userName);

			commentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

//	        sendWorkflowNotification(
//	                interviewPlanEntity.getId().toString(),
//	                "INTERVIEW_PLAN_WORKFLOW",
//	                "Interview Plan activation request submitted",
//	                "Interview Plan",
//
//	                createdBy,
//	                "Administrator",
//	                1,
//
//	                "Interview Plan Activation Request",
//
//	                String.format(
//	                        "Activation requested for Interview Plan %s",
//	                        planName),
//
//	                "Administrator",
//	                "Activation approval pending",
//
//	                "Interview Plan Activation Approval",
//
//	                String.format(
//	                        "Please review activation request for %s",
//	                        planName),
//
//	                new HashMap<>());
		}

		// ACTIVATION APPROVAL

		if (request.getActiveApproval() != null) {

			if (!"Administrator".equalsIgnoreCase(roleName)) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Only Administrator can process activation");
			}

			// APPROVED

			if (Boolean.TRUE.equals(request.getActiveApproval())) {

				interviewPlanEntity.setStatus("ACTIVE");

				interviewPlanEntity.setApprovalStatus("Approved");

				interviewPlanEntity.setActiveApproval(true);

				interviewPlanEntity.setDeactiveApproval(false);

				commentsEntity.setPlanId(planId);

				commentsEntity.setAction("Approve");

				commentsEntity.setComments(request.getComments());

				commentsEntity.setCreatedBy(userName);

				commentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

//	            sendWorkflowNotification(
//	                    interviewPlanEntity.getId().toString(),
//	                    "INTERVIEW_PLAN_WORKFLOW",
//	                    "Interview Plan activated successfully",
//	                    "Interview Plan",
//
//	                    createdBy,
//	                    roleName,
//	                    1,
//
//	                    "Interview Plan Activated",
//
//	                    String.format(
//	                            "Interview Plan %s activated",
//	                            planName),
//
//	                    roleName,
//	                    "Activation approved",
//
//	                    "Interview Plan Activation Confirmation",
//
//	                    String.format(
//	                            "You approved activation for %s",
//	                            planName),
//
//	                    new HashMap<>());
			}

			// REJECTED

			else {

				interviewPlanEntity.setApprovalStatus("Rejected");

				interviewPlanEntity.setActiveApproval(false);

				commentsEntity.setPlanId(planId);

				commentsEntity.setAction("Reject");

				commentsEntity.setComments(request.getComments());

				commentsEntity.setCreatedBy(userName);

				commentsEntity.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

//	            sendWorkflowNotification(
//	                    interviewPlanEntity.getId().toString(),
//	                    "INTERVIEW_PLAN_WORKFLOW",
//	                    "Interview Plan activation rejected",
//	                    "Interview Plan",
//
//	                    createdBy,
//	                    roleName,
//	                    1,
//
//	                    "Interview Plan Activation Rejected",
//
//	                    String.format(
//	                            "Interview Plan activation rejected for %s",
//	                            planName),
//
//	                    roleName,
//	                    "Activation rejected",
//
//	                    "Interview Plan Activation Rejection",
//
//	                    String.format(
//	                            "You rejected activation for %s",
//	                            planName),
//
//	                    new HashMap<>());
			}
		}

		interviewPlanEntity.setUpdatedBy(userName);

		interviewPlanEntity.setUpdatedAt(LocalDateTime.now());

		interviewPlanRepository.save(interviewPlanEntity);

		childLinkCommentsRepository.save(commentsEntity);

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

			map.put("createdBy", plan.getCreatedBy());

			map.put("createdOn", plan.getCreatedOn());

			map.put("rounds", plan.getRounds() != null ? plan.getRounds().size() : 0);

			return map;

		}).toList();

		Map<String, Long> counts = request.buildInterviewPlanCounts(interviewPlanRepository);

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("interviewPlans", plans);

		response.put("currentPage", pageResult.getNumber());

		response.put("totalPages", pageResult.getTotalPages());

		response.put("totalElements", pageResult.getTotalElements());

		response.put("counts", counts);

		log.info("InterviewPlanServiceImpl :: Exit getInterviewPlans");

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview plans fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getInterviewPlanCounts() {

		log.info("InterviewPlanServiceImpl :: Inside getInterviewPlanCounts");

		long allPlans = interviewPlanRepository.count();

		long activePlans = interviewPlanRepository.countByStatus("Active");

		long inactivePlans = interviewPlanRepository.countByStatus("Deactive");

		long inProgressPlans = interviewPlanRepository.countByStatus("InProgress");

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("allPlans", allPlans);

		response.put("activePlans", activePlans);

		response.put("inactivePlans", inactivePlans);

		response.put("inProgressPlans", inProgressPlans);

		log.info("InterviewPlanServiceImpl :: Exit getInterviewPlanCounts");

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview plan counts fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getInterviewPlanApprovals(SpecificationFilterRequest request) {

		try {

			String authHeader = httpServletRequest.getHeader("Authorization");

			String userName = "";
			Long roleId = null;

			if (authHeader != null && authHeader.startsWith("Bearer ")) {

			    String token = authHeader.substring(7);

			    userName = jwtService.extractUsernameFromClaims(token);
			    roleId = jwtService.extractRoleId(token);
			}

			if (roleId == null) {

			    return ApiResponse.failure(
			            ResponseCode.FAILURE,
			            "Role not found in token");
			}

			Integer  functionalityId=
			        functionalityRepository
			                .findByFunctionalityName("Interview Plan").get().getId();
			

			if (functionalityId == null) {

			    return ApiResponse.failure(
			            ResponseCode.FAILURE,
			            "Interview Plan functionality not configured");
			}

			ApprovalChainEntity approvalChain =
			        approvalChainRepository
			                .findByFunctionality(functionalityId);
		

			if (approvalChain == null) {

			    return ApiResponse.failure(
			            ResponseCode.FAILURE,
			            "Approval chain not found");
			}

			boolean roleExists = false;

			for (LevelConfig level : approvalChain.getLevelConfig()) {

			    if (level.getRoleId() != null
			            && level.getRoleId().longValue() == roleId.longValue()) {

			        roleExists = true;
			        break;
			    }
			}

			if (!roleExists) {

			    return ApiResponse.failure(
			            ResponseCode.FAILURE,
			            "You are not authorized");
			}

			Pageable pageable =
			        PageRequest.of(
			                request.getPage(),
			                request.getSize(),
			                Sort.by(
			                        Sort.Direction.fromString(request.getDirection()),
			                        request.getSortBy()
			                )
			        );

			Page<InterviewPlanEntity> page =
			        interviewPlanRepository.findAll(
			                request.buildInterviewPlanApprovalSpecification(),
			                pageable);

			List<Map<String, Object>> content =
			        page.getContent()
			                .stream()
			                .map(plan -> {

			                    Map<String, Object> map =
			                            new LinkedHashMap<>();

			                    map.put("id", plan.getId());
			                    map.put("planName", plan.getPlanName());
			                    map.put("requestedBy", plan.getCreatedBy());
			                    map.put("requestedOn", plan.getCreatedOn());
			                    map.put("status", plan.getApprovalStatus());

			                    map.put(
			                            "rounds",
			                            plan.getRounds() == null
			                                    ? 0
			                                    : plan.getRounds().size());

			                    return map;
			                })
			                .toList();

			Map<String, Object> response =
			        new LinkedHashMap<>();

			response.put("currentPage", page.getNumber());
			response.put("totalPages", page.getTotalPages());
			response.put("size", page.getSize());
			response.put("totalElements", page.getTotalElements());
			response.put("content", content);

			return ApiResponse.success(
			        ResponseCode.SUCCESS,
			        "Interview Plans fetched successfully",
			        response);
		}
		catch (Exception e) {

	        e.printStackTrace();

	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                e.getMessage());
	    }
	
	}
}

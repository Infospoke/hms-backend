package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.dto.NotificationEvent;
import com.hms.service.dto.RoundAssignmentDto;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewRoundEntity;
import com.hms.service.entity.InterviewerAssignmentEntity;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.InterviewCurrentStageRepository;
import com.hms.service.repository.InterviewPlanRepository;
import com.hms.service.repository.InterviewRoundRepository;
import com.hms.service.repository.InterviewerAssignmentRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.AssignInterviewerRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateInterviewAssignmentRequest;
import com.hms.service.response.InterviewerAssignmentCountResponse;
import com.hms.service.service.IInterviewerAssignmentService;
import com.hms.service.service.INotificationService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InterviewerAssignmentServiceImpl implements IInterviewerAssignmentService {

	@Autowired
	private InterviewerAssignmentRepository interviewerAssignmentRepository;

	@Autowired
	private InterviewRoundRepository interviewRoundRepository;

	@Autowired
	private InterviewPlanRepository interviewPlanRepository;

	@Autowired
	private InterviewCurrentStageRepository interviewCurrentStageRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private INotificationService notificationService;

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private AssignRolesRepository assignRolesRepository;

	@Override
	@Transactional
	public ApiResponse<?> assignInterviewers(AssignInterviewerRequest request) {

		String authHeader = httpServletRequest.getHeader("Authorization");

		String userName = "";
		Long userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userName = jwtService.extractUsernameFromClaims(token);
			userId = jwtService.extractUserId(token);
		}

		InterviewPlanEntity plan = interviewPlanRepository.findById(request.getPlanId())
				.orElseThrow(() -> new RuntimeException("Interview plan not found"));

		CreateJobDetailsEntity job = createJobDetailsRepository.findById(request.getJobId())
				.orElseThrow(() -> new RuntimeException("Job not found"));

		String deptName = departmentsRepository.findById(job.getDepartmentId())
				.map(DepartmentsEntity::getDepartmentName).orElse(null);

		Integer makerRoleId = assignRolesRepository.findByUserId(userId.intValue())
				.orElseThrow(() -> new RuntimeException("Role not found")).getRoleId();

		String makerRoleName = rolesRepository.findById(makerRoleId)
				.orElseThrow(() -> new RuntimeException("Role not found")).getRoleName();

		List<NotificationEvent> checkerEvents = new ArrayList<>();

		List<String> interviewerNames = new ArrayList<>();

		boolean anyReassignment = false;

		for (RoundAssignmentDto dto : request.getAssignments()) {

			InterviewRoundEntity round = interviewRoundRepository
					.findByInterviewPlan_IdAndStageTypeId(request.getPlanId(), dto.getStageTypeId());

			if (round == null) {
				throw new RuntimeException("Stage not found");
			}

			boolean isReassignment = false;

			List<InterviewerAssignmentEntity> history = interviewerAssignmentRepository
					.findByJobIdAndStageTypeIdOrderByIdDesc(request.getJobId(), round.getStageTypeId());

			if (!history.isEmpty()) {

				InterviewerAssignmentEntity latest = history.get(0);

				if ("PENDING".equalsIgnoreCase(latest.getStatus())) {
					throw new RuntimeException("Round already assigned and pending response");
				}

				if ("ACCEPTED".equalsIgnoreCase(latest.getStatus())) {
					throw new RuntimeException("Round already accepted");
				}

				if ("REJECTED".equalsIgnoreCase(latest.getStatus())) {
					isReassignment = true;
					anyReassignment = true;
				}
			}

			InterviewerAssignmentEntity entity = new InterviewerAssignmentEntity();

			entity.setJobId(request.getJobId());

			entity.setPlanId(request.getPlanId());

			entity.setStageName(round.getStageName());

			entity.setStageTypeId(round.getStageTypeId());

			entity.setInterviewerUserId(dto.getInterviewerUserId());

			entity.setInterviewerName(dto.getInterviewerName());

			entity.setRoleName(dto.getRoleName());

			entity.setStatus("PENDING");

			entity.setComments(null);

			entity.setRespondedAt(null);

			entity.setCreatedBy(userName);

			entity.setCreatedAt(LocalDateTime.now());

			entity.setUserId(userId);

			entity.setJobTitle(job.getJobTitle());

			entity.setPlanName(plan.getPlanName());

			entity.setDeptName(deptName);

			interviewerAssignmentRepository.save(entity);

			interviewerNames.add(dto.getInterviewerName());

			NotificationEvent checkerEvent = new NotificationEvent();

			checkerEvent.setProcessId(request.getJobId().toString());

			checkerEvent.setMakerRoleId(makerRoleId);
			checkerEvent.setMakerRoleName(makerRoleName);

			checkerEvent.setCheckerRoleName(dto.getRoleName());

			checkerEvent.setDeptName(deptName);

			checkerEvent.setTriggeredAt(LocalDateTime.now());

			if (isReassignment) {

				checkerEvent.setType("INTERVIEWER_REASSIGNMENT");

				checkerEvent.setCheckerNotificationTitle("Interview Reassigned");

				checkerEvent.setCheckerMessage(userName + " (" + makerRoleName + ") reassigned you as interviewer for '"
						+ "'  Job title '" + job.getJobTitle() + "'.");

			} else {

				checkerEvent.setType("INTERVIEWER_ASSIGNMENT");

				checkerEvent.setCheckerNotificationTitle("Interview Assignment");

				checkerEvent.setCheckerMessage(userName + " (" + makerRoleName + ") assigned you as interviewer for'"
						+ "' Job title '" + job.getJobTitle() + "'.");
			}

			Integer checkerRoleId = rolesRepository.findByRoleNameIgnoreCase(dto.getRoleName()).getRoleId();

			String interviewerEmail = userRepository.findByUserId(dto.getInterviewerUserId())
					.orElseThrow(() -> new RuntimeException("User not found")).getEmail();

			Map<Integer, List<String>> roleEmailMap = new HashMap<>();

			roleEmailMap.put(checkerRoleId, List.of(interviewerEmail));

			checkerEvent.setRoleEmailMap(roleEmailMap);

			checkerEvents.add(checkerEvent);

		}

		NotificationEvent makerEvent = new NotificationEvent();

		makerEvent.setProcessId(request.getJobId().toString());

		makerEvent.setMakerRoleId(makerRoleId);

		makerEvent.setMakerRoleName(makerRoleName);

		makerEvent.setDeptName(deptName);

		makerEvent.setTriggeredAt(LocalDateTime.now());

		if (anyReassignment) {

			makerEvent.setType("INTERVIEWER_REASSIGNMENT");

			makerEvent.setMakerNotificationTitle("Interviewer Reassigned");

			makerEvent.setMakerMessage("You reassigned interviewers " + String.join(", ", interviewerNames)
					+ "' for Job title '" + job.getJobTitle() + "'.");

		} else {

			makerEvent.setType("INTERVIEWER_ASSIGNMENT");

			makerEvent.setMakerNotificationTitle("Interviewer Assigned");

			makerEvent.setMakerMessage("You assigned interviewers " + String.join(", ", interviewerNames)
					+ "' for Job title '" + job.getJobTitle() + "'.");
		}

		// Call the new notification method
		notificationService.callInterviewerAssignmentNotification(makerEvent, checkerEvents);

		return ApiResponse.success(ResponseCode.SUCCESS, "Interviewers assigned successfully", "Success");
	}

	@Override
	public ApiResponse<?> getAssignmentDetails(Integer jobId) {

		List<InterviewerAssignmentEntity> assignments = interviewerAssignmentRepository.findByJobIdOrderByIdAsc(jobId);

		if (assignments.isEmpty()) {
			throw new RuntimeException("Assignments not found");
		}

		Map<String, Object> response = buildAssignmentDetailsResponse(assignments);

		return ApiResponse.success(ResponseCode.SUCCESS, "Assignment fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getAssignments(SpecificationFilterRequest request) {

		Sort sort = "DESC".equalsIgnoreCase(request.getDirection()) ? Sort.by(request.getSortBy()).descending()
				: Sort.by(request.getSortBy()).ascending();

		List<CreateJobDetailsEntity> jobs = createJobDetailsRepository
				.findAll(request.buildJobAssignmentSpecification(), sort);

		List<Map<String, Object>> filteredContent = new ArrayList<>();

		String search = request.getFilter("search");
		String deptFilter = request.getFilter("deptName");
		String planFilter = request.getFilter("planName");

		for (CreateJobDetailsEntity job : jobs) {

			InterviewPlanEntity plan = interviewPlanRepository.findById(job.getPlanId()).orElse(null);

			if (plan == null) {
				continue;
			}

			String deptName = departmentsRepository.findById(job.getDepartmentId())
					.map(DepartmentsEntity::getDepartmentName).orElse("");

			if (deptFilter != null && !deptFilter.isBlank() && !deptName.equalsIgnoreCase(deptFilter)) {
				continue;
			}

			if (planFilter != null && !planFilter.isBlank() && !plan.getPlanName().equalsIgnoreCase(planFilter)) {
				continue;
			}

			if (search != null && !search.isBlank()) {

				String value = search.toLowerCase();

				boolean matches = (job.getJobTitle() != null && job.getJobTitle().toLowerCase().contains(value))
						|| (deptName != null && deptName.toLowerCase().contains(value))
						|| (plan.getPlanName() != null && plan.getPlanName().toLowerCase().contains(value));

				if (!matches) {
					continue;
				}
			}

			List<InterviewRoundEntity> rounds = interviewRoundRepository
					.findByInterviewPlan_IdOrderByRoundOrderAsc(plan.getId());

			List<Map<String, Object>> assignmentStatus = new ArrayList<>();

			for (InterviewRoundEntity round : rounds) {

				InterviewerAssignmentEntity assignment = interviewerAssignmentRepository
						.findTopByJobIdAndStageTypeIdOrderByIdDesc(job.getJobId(), round.getStageTypeId()).orElse(null);

				Map<String, Object> roundMap = new LinkedHashMap<>();

				roundMap.put("roundId", round.getId());
				roundMap.put("roundName", round.getStageName());
				roundMap.put("roundType", round.getStageType());
				roundMap.put("roundTypeId", round.getStageTypeId());
				roundMap.put("stageTypeId", round.getStageTypeId());

				roundMap.put("status", assignment != null ? assignment.getStatus() : "NOT_SENT");

				assignmentStatus.add(roundMap);
			}

			Map<String, Object> row = new LinkedHashMap<>();

			row.put("jobId", job.getJobId());
			row.put("jobTitle", job.getJobTitle());
			row.put("deptName", deptName);
			row.put("planId", job.getPlanId());
			row.put("planName", plan.getPlanName());
			row.put("rounds", rounds.size());
			row.put("createdAt", job.getCreatedAt());
			row.put("assignmentStatus", assignmentStatus);

			filteredContent.add(row);
		}

		int currentPage = request.getPage();
		int pageSize = request.getSize();

		int totalElements = filteredContent.size();

		int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);

		int start = currentPage * pageSize;
		int end = Math.min(start + pageSize, totalElements);

		List<Map<String, Object>> pagedContent;

		if (start >= totalElements) {
			pagedContent = Collections.emptyList();
		} else {
			pagedContent = filteredContent.subList(start, end);
		}

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("content", pagedContent);
		response.put("currentPage", currentPage);
		response.put("pageSize", pageSize);
		response.put("totalPages", totalPages);
		response.put("totalElements", totalElements);

		return ApiResponse.success(ResponseCode.SUCCESS, "Assignments fetched successfully", response);
	}

	private Map<String, Object> buildAssignmentDetailsResponse(List<InterviewerAssignmentEntity> assignments) {

		Map<Integer, List<InterviewerAssignmentEntity>> roundWise = assignments.stream().collect(Collectors
				.groupingBy(InterviewerAssignmentEntity::getStageTypeId, LinkedHashMap::new, Collectors.toList()));

		List<Map<String, Object>> rounds = new ArrayList<>();

		for (Map.Entry<Integer, List<InterviewerAssignmentEntity>> entry : roundWise.entrySet()) {

			Integer stageTypeId = entry.getKey();
			List<InterviewerAssignmentEntity> history = entry.getValue();

			InterviewerAssignmentEntity latest = history.get(history.size() - 1);

			InterviewRoundEntity round = interviewRoundRepository
					.findByInterviewPlan_IdAndStageTypeId(latest.getPlanId(), latest.getStageTypeId());

			Map<String, Object> roundResponse = new LinkedHashMap<>();
			roundResponse.put("stageName", round != null ? round.getStageName() : null);
			roundResponse.put("stageType", round != null ? round.getStageType() : null);
			roundResponse.put("stageTypeId", stageTypeId);
			roundResponse.put("currentStatus", latest.getStatus());

			List<Map<String, Object>> assignmentHistory = new ArrayList<>();

			for (InterviewerAssignmentEntity assignment : history) {

				Map<String, Object> historyMap = new LinkedHashMap<>();
				historyMap.put("assignmentId", assignment.getId());
				historyMap.put("interviewerUserId", assignment.getInterviewerUserId());
				historyMap.put("interviewerName", assignment.getInterviewerName());
				historyMap.put("roleName", assignment.getRoleName());
				historyMap.put("status", assignment.getStatus());
				historyMap.put("comments", assignment.getComments());
				historyMap.put("respondedAt", assignment.getRespondedAt());

				assignmentHistory.add(historyMap);
			}

			roundResponse.put("assignmentHistory", assignmentHistory);
			rounds.add(roundResponse);
		}

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("rounds", rounds);

		return response;
	}

	@Override
	public ApiResponse<?> getInterviewerCounts() {

		log.info("DashboardServiceImpl :: Inside getInterviewerCounts");

		String authHeader = httpServletRequest.getHeader("Authorization");

		Integer userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token).intValue();
		}

		LocalDate today = LocalDate.now();

		long todaysInterviews = interviewCurrentStageRepository.countByInterviewerIdAndInterviewDate(userId, today);

		long assignedInterviews = interviewerAssignmentRepository
				.countByInterviewerUserIdAndRespondedAtIsNull(userId.longValue());

		long toSchedule = interviewCurrentStageRepository.countByInterviewerIdAndToScheduleFalse(userId);

		long upcomingInterview = interviewCurrentStageRepository
				.countByInterviewerIdAndToScheduleTrueAndInterviewCompletedFalse(userId);

		long feedbackInterview = interviewCurrentStageRepository
				.countByInterviewerIdAndInterviewCompletedTrueAndFeedbackFalse(userId);

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("todaysInterviews", todaysInterviews);

		response.put("assignedInterviewRequests", assignedInterviews);

		response.put("toSchedule", toSchedule);

		response.put("upcoming", upcomingInterview);

		response.put("Feedback", feedbackInterview);

		log.info("DashboardServiceImpl :: Exit getInterviewerCounts");

		return ApiResponse.success(ResponseCode.SUCCESS, "Dashboard counts fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getInterviewAssignmentDetails(Integer id) {

		log.info("InterviewerAssignmentServiceImpl :: Inside getInterviewAssignmentDetails");

		InterviewerAssignmentEntity assignment = interviewerAssignmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Interview Assignment Not Found"));
		
		log.info("Assignment Id : {}", assignment.getId());
		log.info("Plan Id : {}", assignment.getPlanId());
		log.info("Stage Type Id : {}", assignment.getStageTypeId());

		InterviewRoundEntity round = interviewRoundRepository
				.findByInterviewPlan_IdAndStageTypeId(assignment.getPlanId(), assignment.getStageTypeId());

		if (round == null) {
		    throw new ResourceNotFoundException("Interview stage Not Found");
		}

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("jobTitle", assignment.getJobTitle());

		response.put("deptName", assignment.getDeptName());

		response.put("interviewType", round.getStageType());

		response.put("interviewMode", round.getInterviewMode());

		response.put("assignedOn", assignment.getCreatedAt());

		response.put("assignedBy", assignment.getCreatedBy());

		response.put("roleName", assignment.getRoleName());

		if ("PENDING".equalsIgnoreCase(assignment.getStatus())) {

			LocalDate responseDueDate = assignment.getCreatedAt().toLocalDate().plusDays(6);

			long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), responseDueDate);

			response.put("responseDue", responseDueDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + " ("
					+ remainingDays + " days left)");

		}

		log.info("InterviewerAssignmentServiceImpl :: Exit getInterviewAssignmentDetails");

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview assignment details fetched successfully", response);
	}

	private String calculatePriority(LocalDateTime createdAt) {

		LocalDate dueDate = createdAt.toLocalDate().plusDays(6);

		long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

		if (remainingDays <= 1) {
			return "HIGH";
		} else if (remainingDays <= 3) {
			return "MEDIUM";
		} else {
			return "LOW";
		}
	}

	@Override
	public ApiResponse<?> getAllAssignedInterviewRequests(SpecificationFilterRequest request) {

		log.info("InterviewerAssignmentServiceImpl :: Inside getAssignedInterviewRequests");

		String authHeader = httpServletRequest.getHeader("Authorization");

		Integer userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token).intValue();

			log.info("Logged In UserId : {}", userId);
		}

		Specification<InterviewerAssignmentEntity> spec = request.buildInterviewAssignmentSpecification(userId);

		Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Page<InterviewerAssignmentEntity> assignmentPage = interviewerAssignmentRepository.findAll(spec, pageable);

		log.info("Total Assignments Found : {}", assignmentPage.getContent().size());
		;

		assignmentPage.getContent()
				.forEach(a -> log.info("DB Assignment -> Id={}, InterviewerUserId={}, JobId={}, JobTitle={}", a.getId(),
						a.getInterviewerUserId(), a.getJobId(), a.getJobTitle()));

		List<Map<String, Object>> responseList = new ArrayList<>();

		for (InterviewerAssignmentEntity assignment : assignmentPage.getContent()) {

			log.info("Assignment Id = {}", assignment.getId());

			String calculatedPriority = calculatePriority(assignment.getCreatedAt());

			String priorityFilter = request.getFilter("priority");

			if (priorityFilter != null && !priorityFilter.isBlank() && !"ALL".equalsIgnoreCase(priorityFilter)
					&& !calculatedPriority.equalsIgnoreCase(priorityFilter)) {
				continue;
			}

			Map<String, Object> map = new LinkedHashMap<>();

			map.put("assignmentId", assignment.getId());
			map.put("jobTitle", assignment.getJobTitle());
			map.put("department", assignment.getDeptName());
			map.put("round", assignment.getStageName());
			map.put("requestedOn", assignment.getCreatedAt());
			map.put("status", assignment.getStatus());
			map.put("jobId", assignment.getJobId());
			map.put("priority", calculatedPriority);

			responseList.add(map);
		}

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("content", responseList);

		response.put("currentPage", assignmentPage.getNumber());

		response.put("totalPages", assignmentPage.getTotalPages());

		response.put("totalElements", assignmentPage.getTotalElements());

		response.put("pageSize", assignmentPage.getSize());

		log.info("InterviewerAssignmentServiceImpl :: Exit getAssignedInterviewRequests");

		return ApiResponse.success(ResponseCode.SUCCESS, "Assigned Interview Requests fetched successfully", response);
	}

	@Override
	public ApiResponse<?> updateInterviewAssignment(UpdateInterviewAssignmentRequest request) {

		log.info("InterviewerAssignmentServiceImpl :: Inside updateInterviewAssignment");

		String authHeader = httpServletRequest.getHeader("Authorization");

		Integer userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token).intValue();
		}

		InterviewerAssignmentEntity assignment = interviewerAssignmentRepository.findById(request.getId())
				.orElseThrow(() -> new RuntimeException("Interview Assignment Not Found"));

		if (!assignment.getInterviewerUserId().equals(userId.longValue())) {

			throw new RuntimeException("You are not authorized to update this assignment");
		}

		assignment.setStatus(request.getStatus());

		assignment.setComments(request.getComments());

		assignment.setRespondedAt(LocalDateTime.now());

		interviewerAssignmentRepository.save(assignment);

		Integer makerUserId = userId;

		AssignRolesEntity makerRole = assignRolesRepository.findByUserId(makerUserId)
				.orElseThrow(() -> new RuntimeException("Maker role not found"));

		Integer makerRoleId = makerRole.getRoleId();

		String makerRoleName = rolesRepository.findById(makerRoleId)
				.orElseThrow(() -> new RuntimeException("Role not found")).getRoleName();

		// Assigner (Checker)
		Integer checkerUserId = assignment.getUserId().intValue();
		AssignRolesEntity checkerRole = assignRolesRepository.findByUserId(checkerUserId)
				.orElseThrow(() -> new RuntimeException("Checker role not found"));

		Integer checkerRoleId = checkerRole.getRoleId();

		String checkerRoleName = rolesRepository.findById(checkerRoleId)
				.orElseThrow(() -> new RuntimeException("Role not found")).getRoleName();

		// Checker email map (required by NotificationServiceImpl)
		String checkerEmail = userRepository.findByUserId(checkerUserId)
				.orElseThrow(() -> new RuntimeException("Checker not found")).getEmail();

		Map<Integer, List<String>> roleEmailMap = new HashMap<>();
		roleEmailMap.put(checkerRoleId, List.of(checkerEmail));

		NotificationEvent event = new NotificationEvent();

		event.setProcessId(assignment.getJobId() + "-" + LocalDateTime.now().toString());

		event.setMakerRoleId(makerRoleId);
		event.setMakerRoleName(makerRoleName);
		event.setCheckerRoleName(checkerRoleName);

		event.setDeptName(assignment.getDeptName());

		event.setRoleEmailMap(roleEmailMap);

		event.setTriggeredAt(LocalDateTime.now());

		if ("ACCEPTED".equalsIgnoreCase(request.getStatus())) {

			event.setType("INTERVIEW_ASSIGNMENT_ACCEPTED");

			event.setMakerNotificationTitle("Interview Assignment Accepted");
			event.setCheckerNotificationTitle("Interview Assignment Accepted");

			event.setMakerMessage("You accepted the interview assignment.");

			event.setCheckerMessage(assignment.getInterviewerName()
					+ " accepted the interview assignment for Job title '" + assignment.getJobTitle() + "'.");
		}

		else if ("REJECTED".equalsIgnoreCase(request.getStatus())) {

			event.setType("INTERVIEW_ASSIGNMENT_REJECTED");

			event.setMakerNotificationTitle("Interview Assignment Rejected");
			event.setCheckerNotificationTitle("Interview Assignment Rejected");

			event.setMakerMessage("You rejected the interview assignment.");

			event.setCheckerMessage(
					assignment.getInterviewerName() + " rejected the interview assignment for Job title'"
							+ assignment.getJobTitle() + "'. Please reassign another interviewer.");
		}

		notificationService.callNotification(event);

		log.info("InterviewerAssignmentServiceImpl :: Exit updateInterviewAssignment");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Interview assignment updated successfully");
	}

	@Override
	public ApiResponse<?> getInterviewersAssignmentCounts() {

		log.info("InterviewerAssignmentServiceImpl :: Inside the getInterviewersAssignmentCounts");

		String authHeader = httpServletRequest.getHeader("Authorization");

		Long userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userId = jwtService.extractUserId(token);
		}

		List<Object[]> statusCounts = interviewerAssignmentRepository.getStatusCounts(userId);

		int totalAssignments = 0;
		int acceptedCount = 0;
		int rejectedCount = 0;
		int pendingCount = 0;

		for (Object[] row : statusCounts) {

			String status = (String) row[0];
			int count = ((Long) row[1]).intValue();

			totalAssignments += count;

			if ("Accepted".equalsIgnoreCase(status)) {
				acceptedCount = count;
			} else if ("Rejected".equalsIgnoreCase(status)) {
				rejectedCount = count;
			} else if ("Pending".equalsIgnoreCase(status)) {
				pendingCount = count;
			}
		}

		InterviewerAssignmentCountResponse response = new InterviewerAssignmentCountResponse();

		response.setTotalAssignments(totalAssignments);
		response.setAcceptedCount(acceptedCount);
		response.setRejectedCount(rejectedCount);
		response.setPendingCount(pendingCount);

		log.info("InterviewerAssignmentServiceImpl :: Exit from the getInterviewersAssignmentCounts");

		return new ApiResponse<>(ResponseCode.SUCCESS, "Assignment counts fetched successfully", response);
	}

}
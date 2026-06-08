package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hms.service.dto.RoundAssignmentDto;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewRoundEntity;
import com.hms.service.entity.InterviewerAssignmentEntity;

import com.hms.service.repository.InterviewFeedbackRepository;

import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.InterviewPlanRepository;
import com.hms.service.repository.InterviewRoundRepository;
import com.hms.service.repository.InterviewScheduleRepository;
import com.hms.service.repository.InterviewUpcomingRepository;
import com.hms.service.repository.InterviewerAssignmentRepository;
import com.hms.service.request.AssignInterviewerRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.IInterviewerAssignmentService;
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
	private InterviewScheduleRepository interviewScheduleRepository;

	@Autowired
	private InterviewUpcomingRepository interviewUpcomingRepository;

	@Autowired
	private InterviewFeedbackRepository interviewFeedbackRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private DepartmentsRepository departmentsRepository;

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

		for (RoundAssignmentDto dto : request.getAssignments()) {

			InterviewRoundEntity round = interviewRoundRepository.findById(dto.getRoundId())
					.orElseThrow(() -> new RuntimeException("Round not found"));

			List<InterviewerAssignmentEntity> history = interviewerAssignmentRepository
					.findByJobIdAndRoundIdOrderByIdDesc(request.getJobId(), round.getId());

			if (!history.isEmpty()) {

				InterviewerAssignmentEntity latest = history.get(0);

				if ("PENDING".equalsIgnoreCase(latest.getStatus())) {

					throw new RuntimeException("Round already assigned and pending response");
				}

				if ("ACCEPTED".equalsIgnoreCase(latest.getStatus())) {

					throw new RuntimeException("Round already accepted");
				}
			}

			InterviewerAssignmentEntity entity = new InterviewerAssignmentEntity();

			entity.setJobId(request.getJobId());

			entity.setPlanId(request.getPlanId());

			entity.setRoundId(round.getId());

			entity.setStageName(round.getStageName());

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
		}

		return ApiResponse.success(ResponseCode.SUCCESS, "Success", "Interviewers assigned successfully");
	}

	@Override
	public ApiResponse<?> getAssignmentDetails(Integer jobId) {

		List<InterviewerAssignmentEntity> assignments = interviewerAssignmentRepository.findByJobId(jobId);

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

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Page<CreateJobDetailsEntity> page = createJobDetailsRepository
				.findAll(request.buildJobAssignmentSpecification(), pageable);

		List<Map<String, Object>> content = new ArrayList<>();

		String search = request.getFilter("search");

		String deptFilter = request.getFilter("deptName");

		String planFilter = request.getFilter("planName");

		for (CreateJobDetailsEntity job : page.getContent()) {

			InterviewPlanEntity plan = interviewPlanRepository.findById(job.getPlanId()).orElse(null);

			if (plan == null) {
				continue;
			}

			String deptName = departmentsRepository.findById(job.getDepartmentId())
					.map(DepartmentsEntity::getDepartmentName).orElse("");

			if (deptFilter != null && !deptFilter.equalsIgnoreCase(deptName)) {

				continue;
			}

			if (planFilter != null && !planFilter.equalsIgnoreCase(plan.getPlanName())) {

				continue;
			}

			if (search != null) {

				String value = search.toLowerCase();

				boolean matches = job.getJobTitle().toLowerCase().contains(value)
						|| deptName.toLowerCase().contains(value) || plan.getPlanName().toLowerCase().contains(value);

				if (!matches) {
					continue;
				}
			}

			List<InterviewRoundEntity> rounds = interviewRoundRepository
					.findByInterviewPlan_IdOrderByRoundOrderAsc(plan.getId());

			List<Map<String, Object>> assignmentStatus = new ArrayList<>();

			for (InterviewRoundEntity round : rounds) {
				InterviewerAssignmentEntity assignment = interviewerAssignmentRepository
						.findTopByJobIdAndRoundIdOrderByIdDesc(job.getJobId(), round.getId()).orElse(null);

				Map<String, Object> roundMap = new LinkedHashMap<>();

				roundMap.put("roundId", round.getId());
				
				roundMap.put("roundName", round.getStageName());
				
				roundMap.put("roundType", round.getStageType());
				
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

			content.add(row);
		}

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("content", content);

		response.put("currentPage", page.getNumber());

		response.put("totalPages", page.getTotalPages());

		response.put("totalElements", content.size());

		return ApiResponse.success(ResponseCode.SUCCESS, "Assignments fetched successfully", response);
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

		long assignedInterviews = interviewerAssignmentRepository.countByInterviewerUserId(userId);

		long toSchedule = interviewScheduleRepository.countByUserId(userId);

		long upcomingInterview = interviewUpcomingRepository.countByUserId(userId);

		long feedbackInterview = interviewFeedbackRepository.countByUserId(userId);

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("assignedInterviewRequests", assignedInterviews);

		response.put("toSchedule", toSchedule);

		response.put("upcoming", upcomingInterview);

		response.put("Feedback", feedbackInterview);

		log.info("DashboardServiceImpl :: Exit getInterviewerCounts");

		return ApiResponse.success(ResponseCode.SUCCESS, "Dashboard counts fetched successfully", response);
	}

	private Map<String, Object> buildAssignmentDetailsResponse(List<InterviewerAssignmentEntity> assignments) {

		Map<Long, List<InterviewerAssignmentEntity>> roundWise = assignments.stream().collect(Collectors
				.groupingBy(InterviewerAssignmentEntity::getRoundId, LinkedHashMap::new, Collectors.toList()));

		List<Integer> roundIds = roundWise.keySet().stream().map(Long::intValue).toList();

		Map<Long, InterviewRoundEntity> roundMap = interviewRoundRepository.findByIdIn(roundIds).stream()
				.collect(Collectors.toMap(r -> Long.valueOf(r.getId()), r -> r));

		List<Map<String, Object>> rounds = new ArrayList<>();

		for (Map.Entry<Long, List<InterviewerAssignmentEntity>> entry : roundWise.entrySet()) {

			Long roundId = entry.getKey();

			List<InterviewerAssignmentEntity> history = entry.getValue();

			InterviewRoundEntity round = roundMap.get(roundId);

			InterviewerAssignmentEntity latest = history.get(history.size() - 1);

			Map<String, Object> roundResponse = new LinkedHashMap<>();

			roundResponse.put("roundId", roundId);

			roundResponse.put("stageName", round != null ? round.getStageName() : null);

			roundResponse.put("stageType", round != null ? round.getStageType() : null);

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
	public ApiResponse<?> getInterviewAssignmentDetails(Integer id) {

		log.info("InterviewerAssignmentServiceImpl :: Inside getInterviewAssignmentDetails");

		InterviewerAssignmentEntity assignment = interviewerAssignmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Interview Assignment Not Found"));

		InterviewRoundEntity round = interviewRoundRepository.findById(assignment.getRoundId())
				.orElseThrow(() -> new ResourceNotFoundException("Interview Round Not Found"));

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("jobTitle", assignment.getJobTitle());
		response.put("deptName", assignment.getDeptName());
		response.put("interviewType", round.getStageType());
		response.put("interviewType", assignment.getStageName());
		response.put("interviewMode", round.getInterviewMode());
		response.put("assignedOn", assignment.getCreatedAt());
		response.put("assignedBy", assignment.getCreatedBy());
		response.put("roleName", assignment.getRoleName());

		if ("PENDING".equalsIgnoreCase(assignment.getStatus())) {

			long daysPending = java.time.temporal.ChronoUnit.DAYS.between(assignment.getCreatedAt().toLocalDate(),
					java.time.LocalDate.now());

			response.put("responseDue", daysPending + " days pending");
		}

		log.info("InterviewerAssignmentServiceImpl :: Exit getInterviewAssignmentDetails");

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview assignment details fetched successfully", response);
	}
}
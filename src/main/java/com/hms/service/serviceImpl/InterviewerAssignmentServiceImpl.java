package com.hms.service.serviceImpl;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

		for (RoundAssignmentDto dto : request.getAssignments()) {

			InterviewRoundEntity round = interviewRoundRepository.findById(dto.getRoundId())
					.orElseThrow(() -> new RuntimeException("Round not found"));

			InterviewerAssignmentEntity entity = new InterviewerAssignmentEntity();

			entity.setJobId(request.getJobId());

			entity.setPlanId(request.getPlanId());

			entity.setRoundId(round.getId());

			entity.setStageName(round.getStageName());

			entity.setInterviewerUserId(dto.getInterviewerUserId());

			entity.setInterviewerName(dto.getInterviewerName());

			entity.setRoleName(dto.getRoleName());

			entity.setStatus("PENDING");

			entity.setCreatedAt(LocalDateTime.now());

			entity.setCreatedBy(userName);

			entity.setUserId(userId);

			entity.setJobTitle(job.getJobTitle());

			entity.setPlanName(plan.getPlanName());

			entity.setDeptName(departmentsRepository.findById(job.getDepartmentId())
					.map(DepartmentsEntity::getDepartmentName).orElse(null));

			interviewerAssignmentRepository.save(entity);
		}

		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Interviewers assigned successfully");
	}

	@Override
	public ApiResponse<?> getAssignmentDetails(Integer planId) {

		List<InterviewerAssignmentEntity> assignments = interviewerAssignmentRepository.findByPlanId(planId);

		if (assignments.isEmpty()) {

			throw new RuntimeException("Assignments not found");
		}

		return ApiResponse.success(ResponseCode.SUCCESS, "Assignment fetched successfully",
				buildAssignmentResponse(assignments, true));
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
						.findByJobIdAndRoundId(job.getJobId(), round.getId()).orElse(null);

				Map<String, Object> roundMap = new LinkedHashMap<>();

				roundMap.put("roundId", round.getId());

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

		log.info("DashboardServiceImpl :: Inside getDashboardCounts");

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
		
		response.put("toSchedule",toSchedule);
		
		response.put("upcoming",upcomingInterview);
		
		response.put("Feedback",feedbackInterview);

		log.info("DashboardServiceImpl :: Exit getDashboardCounts");

		return ApiResponse.success(ResponseCode.SUCCESS, "Dashboard counts fetched successfully", response);
	}

	private Map<String, Object> buildAssignmentResponse(List<InterviewerAssignmentEntity> assignments,
			boolean detailed) {

		InterviewerAssignmentEntity first = assignments.get(0);

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("jobId", first.getJobId());
		response.put("jobTitle", first.getJobTitle());
		response.put("deptName", first.getDeptName());
		response.put("planId", first.getPlanId());
		response.put("planName", first.getPlanName());

		List<Map<String, Object>> rounds = new ArrayList<>();

		for (InterviewerAssignmentEntity assignment : assignments) {

			Map<String, Object> roundMap = new LinkedHashMap<>();

			roundMap.put("roundId", assignment.getRoundId());

			roundMap.put("status", assignment.getStatus());

			if (detailed) {

				InterviewRoundEntity round = interviewRoundRepository.findById(assignment.getRoundId()).orElse(null);

				roundMap.put("stageName", assignment.getStageName());

				roundMap.put("stageType", round != null ? round.getStageType() : null);

				roundMap.put("interviewerUserId", assignment.getInterviewerUserId());

				roundMap.put("interviewerName", assignment.getInterviewerName());

				roundMap.put("roleName", assignment.getRoleName());

				roundMap.put("comments", assignment.getComments());

				roundMap.put("respondedAt", assignment.getRespondedAt());
			}

			rounds.add(roundMap);
		}

		response.put("rounds", rounds);

		return response;
	}
}
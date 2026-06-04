package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.dto.RoundAssignmentDto;
import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewRoundEntity;
import com.hms.service.entity.InterviewerAssignmentEntity;
import com.hms.service.repository.InterviewFeedbackRepository;
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

	@Override
	public ApiResponse<?> assignInterviewers(AssignInterviewerRequest request) {

		String authHeader = httpServletRequest.getHeader("Authorization");

		String userName = "";

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			userName = jwtService.extractUsernameFromClaims(token);
		}
		InterviewPlanEntity plan = interviewPlanRepository.findById(request.getPlanId())
				.orElseThrow(() -> new RuntimeException("Interview plan not found"));

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

			entity.setCreatedBy(userName);

			entity.setCreatedAt(LocalDateTime.now());

			interviewerAssignmentRepository.save(entity);
		}

		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Interviewrs Assigned successfully");
	}

	@Override
	public ApiResponse<?> getAssignments(SpecificationFilterRequest request) {

		Sort sort = "DESC".equalsIgnoreCase(request.getDirection()) ? Sort.by(request.getSortBy()).descending()
				: Sort.by(request.getSortBy()).ascending();

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Specification<InterviewerAssignmentEntity> spec = request.buildInterviewAssignmentSpecification();

		Page<InterviewerAssignmentEntity> page = interviewerAssignmentRepository.findAll(spec, pageable);

		Map<String, Object> response = new HashMap<>();

		response.put("content", page.getContent());

		response.put("totalElements", page.getTotalElements());

		response.put("totalPages", page.getTotalPages());

		response.put("currentPage", page.getNumber());

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
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
}
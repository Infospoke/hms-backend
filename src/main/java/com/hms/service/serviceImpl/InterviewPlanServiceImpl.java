package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.entity.ChildLinkCommentsEntity;
import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewRoundEntity;
import com.hms.service.repository.ChildLinkCommentsRepository;
import com.hms.service.repository.InterviewPlanRepository;
import com.hms.service.repository.InterviewRoundRepository;
import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.InterviewRoundRequest;
import com.hms.service.response.CommentTimelineResponse;
import com.hms.service.response.InterviewPlanResponse;
import com.hms.service.response.InterviewRoundsResponse;
import com.hms.service.service.IInterviewPlanService;
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
	private JwtService jwtService;

	@Autowired
	private InterviewRoundRepository interviewRoundRepository;

	@Autowired
	private ChildLinkCommentsRepository childLinkCommentsRepository;

	@Override
	public ApiResponse<?> createInterviewPlan(InterviewPlanRequest request, HttpServletRequest httpRequest) {

		log.info("InterviewPlanServiceImpl :: Inside the createInterviewPlan method");

		try {

			String authHeader = httpRequest.getHeader("Authorization");
			String token = authHeader.substring(7);
			String username = jwtService.extractUsernameFromClaims(token);

			InterviewPlanEntity entity = new InterviewPlanEntity();
			entity.setPlanName(request.getPlanName());
			entity.setDescription(request.getDescription());
			entity.setApprovalStatus("InProgress");
			entity.setStatus(null);
			entity.setCreatedBy(username);
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
	public ApiResponse<?> getInterviewPlanDetailsById(Integer id) {

		log.info("InterviewPlanServiceImpl :: getInterviewPlanDetailsById");

		Optional<InterviewPlanEntity> optionalPlan = interviewPlanRepository.findById(id);

		if (optionalPlan.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Failed To fetch details");
		}

		InterviewPlanEntity interviewPlan = optionalPlan.get();

		List<InterviewRoundEntity> rounds = interviewRoundRepository.findByInterviewPlan_IdOrderByRoundOrderAsc(id);

		List<InterviewRoundsResponse> roundsResponse = rounds.stream()
				.map(round -> new InterviewRoundsResponse(round.getRoundOrder(),
						round.getStageName(),
						round.getStageType(),
						round.getInterviewMode(),
						round.getMandatory()))
				.toList();

		List<ChildLinkCommentsEntity> comments = childLinkCommentsRepository.findByPlanIdOrderByCreatedAtAsc(id);

		List<CommentTimelineResponse> timelineResponse = comments.stream()
				.map(comment -> new CommentTimelineResponse(comment.getAction(), 
						comment.getComments(),
						comment.getDescription(), 
						comment.getCreatedBy(),
						comment.getCreatedAt()))
				.toList();

		// final response
		InterviewPlanResponse response = new InterviewPlanResponse(interviewPlan.getPlanName(),
				interviewPlan.getDescription(),
				interviewPlan.getStatus(), 
				interviewPlan.getCreatedBy(),
				interviewPlan.getCreatedOn(), 
				roundsResponse, timelineResponse);

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}
}

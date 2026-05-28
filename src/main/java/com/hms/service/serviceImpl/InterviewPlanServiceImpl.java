package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewRoundEntity;
import com.hms.service.repository.InterviewPlanRepository;
import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.InterviewRoundRequest;
import com.hms.service.request.SpecificationFilterRequest;
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

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("interviewPlans", plans);

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

		long activePlans = interviewPlanRepository.countByStatus("Active");

		long inactivePlans = interviewPlanRepository.countByStatus("Deactive");
		
		long inProgressPlans =
		        interviewPlanRepository.countByStatusIsNull();
		

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("allPlans", allPlans);

		response.put("activePlans", activePlans);

		response.put("inactivePlans", inactivePlans);
		
		response.put("inProgressPlans", inProgressPlans);

		log.info("InterviewPlanServiceImpl :: Exit getInterviewPlanCounts");

		return ApiResponse.success(ResponseCode.SUCCESS, "Interview plan counts fetched successfully", response);
	}
}

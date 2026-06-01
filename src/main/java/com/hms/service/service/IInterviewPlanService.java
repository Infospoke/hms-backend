package com.hms.service.service;

import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.UpdateInterviewPlanRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.wrappers.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface IInterviewPlanService {

	ApiResponse<?> createInterviewPlan(@Valid InterviewPlanRequest request, HttpServletRequest httpRequest);

	ApiResponse<?> updateInterviewPlan(@Valid UpdateInterviewPlanRequest request, HttpServletRequest httpRequest);

	ApiResponse<?> getInterviewPlans(SpecificationFilterRequest request);
	
	ApiResponse<?> getInterviewPlanCounts();

}

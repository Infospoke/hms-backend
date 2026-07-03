package com.hms.service.service;

import com.hms.service.request.InterviewCompleteRequest;
import com.hms.service.request.InterviewFeedbackRequest;
import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.InterviewScheduleRequest;
import com.hms.service.request.RescheduleInterviewRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateInterviewCompletionStatusRequest;
import com.hms.service.request.UpdateInterviewPlanRequest;
import com.hms.service.response.InterviewDashboardResponse;

import com.hms.service.wrappers.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface IInterviewPlanService {

	ApiResponse<?> createInterviewPlan(@Valid InterviewPlanRequest request, HttpServletRequest httpRequest);

	ApiResponse<?> getInterviewPlanDetailsById(Integer id);

	ApiResponse<?> updateInterviewPlan(@Valid UpdateInterviewPlanRequest request, HttpServletRequest httpRequest);

	ApiResponse<?> getInterviewPlans(SpecificationFilterRequest request);

	ApiResponse<?> getInterviewPlanCounts();

	ApiResponse<?> getInterviewPlanApprovals(SpecificationFilterRequest request);

	ApiResponse<?> interviewFeedback(InterviewFeedbackRequest request);

	ApiResponse<?> scheduleInterview(InterviewScheduleRequest request);

	ApiResponse<?> getTodayInterviews(SpecificationFilterRequest request);

	ApiResponse<?> getFeedbackList(SpecificationFilterRequest request);

	ApiResponse<?> getInterviewDetails(Integer applicationId);

	ApiResponse<?> getScheduleList(SpecificationFilterRequest request);

	ApiResponse<?> getInterviewProgressDetailsById(Integer applicationId);

	ApiResponse<?> getAllAIInterviews(SpecificationFilterRequest request);

	ApiResponse<?> candidateOverview(Integer applicantId);

	ApiResponse<?> updateInterviewFeedback(InterviewFeedbackRequest interviewFeedbackRequest);

	ApiResponse<?> getInterviewProgressList(SpecificationFilterRequest request);
	
	ApiResponse<InterviewDashboardResponse> getInterviewProgressCount();

	ApiResponse<?> getInterviewUpcomingList(SpecificationFilterRequest request);

	ApiResponse<?> getInterviewSummary(Integer applicationId);

	ApiResponse<?> getInterviewScheduleDetailsById(Integer scheduleId);

	ApiResponse<?> rescheduleInterview(RescheduleInterviewRequest request);

	ApiResponse<?> updateInterviewCompletionStatus(UpdateInterviewCompletionStatusRequest request);

	ApiResponse<?> interviewComplete(InterviewCompleteRequest request);





}

package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.ApplicantFeedBackRequest;
import com.hms.service.request.InterviewCompleteRequest;
import com.hms.service.request.InterviewFeedbackRequest;
import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.InterviewScheduleRequest;
import com.hms.service.request.RescheduleInterviewRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateInterviewCompletionStatusRequest;
import com.hms.service.request.UpdateInterviewPlanRequest;
import com.hms.service.response.InterviewDashboardResponse;
import com.hms.service.service.IInterviewPlanService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/hms/interview-plan")
public class InterviewPlanController {
	@Autowired
	private IInterviewPlanService interviewPlanService;

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<?>> createInterviewPlan(@Valid @RequestBody InterviewPlanRequest request,
			HttpServletRequest httpRequest) {
		ApiResponse<?> response = interviewPlanService.createInterviewPlan(request, httpRequest);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/interview-plan-details-by-id/{id}")
	public ResponseEntity<ApiResponse<?>> getInterviewPlanDetailsById(@PathVariable("id") Integer id) {
		ApiResponse<?> response = interviewPlanService.getInterviewPlanDetailsById(id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/update")
	public ResponseEntity<ApiResponse<?>> updateInterviewPlan(@Valid @RequestBody UpdateInterviewPlanRequest request,
			HttpServletRequest httpRequest) {
		ApiResponse<?> response = interviewPlanService.updateInterviewPlan(request, httpRequest);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/list")
	public ResponseEntity<ApiResponse<?>> getInterviewPlans(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = interviewPlanService.getInterviewPlans(request);

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@GetMapping("/counts")
	public ResponseEntity<ApiResponse<?>> getInterviewPlanCounts() {
		ApiResponse<?> response = interviewPlanService.getInterviewPlanCounts();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping("/interview-plan-approvals")
	public ResponseEntity<ApiResponse<?>> getInterviewPlanApprovals(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = interviewPlanService.getInterviewPlanApprovals(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/interview-feedback")
	public ResponseEntity<ApiResponse<?>> interviewFeedback(@RequestBody InterviewFeedbackRequest request) {
		ApiResponse<?> response = interviewPlanService.interviewFeedback(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/update-interview-feedback")
	public ResponseEntity<ApiResponse<?>> updateInterviewFeedback(
			@RequestBody InterviewFeedbackRequest interviewFeedbackRequest) {
		ApiResponse<?> response = interviewPlanService.updateInterviewFeedback(interviewFeedbackRequest);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/interview-schedule")
	public ResponseEntity<ApiResponse<?>> scheduleInterview(@RequestBody InterviewScheduleRequest request) {
		ApiResponse<?> response = interviewPlanService.scheduleInterview(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/get-today-interviews")
	public ResponseEntity<ApiResponse<?>> getTodayInterviews(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = interviewPlanService.getTodayInterviews(request);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping("/today-interview-details")
	public ResponseEntity<ApiResponse<?>> getInterviewDetails(@RequestBody ApplicantFeedBackRequest request) {
		ApiResponse<?> response = interviewPlanService.getInterviewDetails(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/feedback-list")
	public ResponseEntity<ApiResponse<?>> getFeedbackList(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = interviewPlanService.getFeedbackList(request);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping("/get-to-be-schedule-list")
	public ResponseEntity<ApiResponse<?>> getScheduleList(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = interviewPlanService.getScheduleList(request);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@GetMapping("get-interview-schedule-details-by-id/{scheduleId}")
	public ResponseEntity<ApiResponse<?>> getInterviewScheduleDetailsById(
			@PathVariable("scheduleId") Integer scheduleId) {
		ApiResponse<?> response = interviewPlanService.getInterviewScheduleDetailsById(scheduleId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/interview-progress-details/{applicationId}")
	public ResponseEntity<ApiResponse<?>> getInterviewProgressDetailsById(
			@PathVariable("applicationId") Integer applicationId) {
		ApiResponse<?> response = interviewPlanService.getInterviewProgressDetailsById(applicationId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/get-all-ai-interviews")
	public ResponseEntity<ApiResponse<?>> getAllAIInterviews(@RequestBody SpecificationFilterRequest request) {

		ApiResponse<?> response = interviewPlanService.getAllAIInterviews(request);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/candidate-overview/{applicationId}")
	public ResponseEntity<ApiResponse<?>> candidateOverview(@PathVariable("applicationId") Integer applicationId) {
		ApiResponse<?> response = interviewPlanService.candidateOverview(applicationId);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/progress-list")
	public ResponseEntity<ApiResponse<?>> getInterviewProgressList(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = interviewPlanService.getInterviewProgressList(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/progress-count")
	public ResponseEntity<ApiResponse<InterviewDashboardResponse>> getInterviewProgressCount() {
		return ResponseEntity.ok(interviewPlanService.getInterviewProgressCount());

	}

	@PostMapping("/interview-upcoming-list")
	public ResponseEntity<ApiResponse<?>> getInterviewUpcomingList(@RequestBody SpecificationFilterRequest request) {
		return ResponseEntity.ok(interviewPlanService.getInterviewUpcomingList(request));
	}

	@GetMapping("/interview-summary/{applicationId}")
	public ResponseEntity<ApiResponse<?>> getInterviewSummary(@PathVariable("applicationId") Integer applicationId) {
		return ResponseEntity.ok(interviewPlanService.getInterviewSummary(applicationId));
	}

	@PostMapping("/rescedule-interview")
	public ResponseEntity<ApiResponse<?>> resceduleInterview(@RequestBody RescheduleInterviewRequest request) {
		ApiResponse<?> response = interviewPlanService.rescheduleInterview(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/update-interview-completion-status")
	public ResponseEntity<ApiResponse<?>> updateInterviewCompletionStatus(@RequestBody UpdateInterviewCompletionStatusRequest request) {
		ApiResponse<?> response = interviewPlanService.updateInterviewCompletionStatus(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/interview-complete")
	public ResponseEntity<ApiResponse<?>> interviewComplete(@RequestBody InterviewCompleteRequest request) {
		ApiResponse<?> response = interviewPlanService.interviewComplete(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/applicant-feedback-by-id")
	public ResponseEntity<ApiResponse<?>> getApplicantFeedbackById(@RequestBody ApplicantFeedBackRequest request) {
		ApiResponse<?> response = interviewPlanService.getApplicantFeedbackById(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

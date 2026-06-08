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

import com.hms.service.request.InterviewFeedbackRequest;
import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.InterviewScheduleRequest;
import com.hms.service.request.UpdateInterviewPlanRequest;
import com.hms.service.request.SpecificationFilterRequest;
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
	public ResponseEntity<ApiResponse<?>> getInterviewPlanDetailsById(@PathVariable("id")Integer id){
		ApiResponse<?> response =interviewPlanService.getInterviewPlanDetailsById(id);
		return new ResponseEntity<>(response,HttpStatus.OK);
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
	public ResponseEntity<ApiResponse<?>>interviewFeedback(@RequestBody InterviewFeedbackRequest request) {
		ApiResponse<?> response = interviewPlanService.interviewFeedback(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/interview-schedule")
	public ResponseEntity<ApiResponse<?>> scheduleInterview(@RequestBody InterviewScheduleRequest request){
		ApiResponse<?> response=interviewPlanService.scheduleInterview(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
		
	}
	
	@PostMapping("/get-today-interviews")
	public ResponseEntity<ApiResponse<?>> getTodayInterviews(@RequestBody SpecificationFilterRequest request){
		ApiResponse<?> response=interviewPlanService.getTodayInterviews(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
		
	}
	

	@GetMapping("/get-interview-details/{applicationId}")
	public ResponseEntity<ApiResponse<?>> getInterviewDetails(@PathVariable("applicationId") Integer applicationId) {

		ApiResponse<?> response = interviewPlanService.getInterviewDetails(applicationId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	

	@PostMapping("/feedback-list")
	public ResponseEntity<ApiResponse<?>> getFeedbackList(@RequestBody SpecificationFilterRequest request){
		ApiResponse<?> response=interviewPlanService.getFeedbackList(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
		
	}
	
	

	
	
}

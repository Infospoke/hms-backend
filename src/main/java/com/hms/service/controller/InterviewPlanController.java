package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.InterviewPlanRequest;
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
}

package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.InterviewPlanRequest;
import com.hms.service.request.UpdateInterviewPlanRequest;
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

	@PutMapping("/update")
	public ResponseEntity<ApiResponse<?>> updateInterviewPlan(@Valid @RequestBody UpdateInterviewPlanRequest request,
			HttpServletRequest httpRequest) {

		ApiResponse<?> response = interviewPlanService.updateInterviewPlan(request, httpRequest);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
}

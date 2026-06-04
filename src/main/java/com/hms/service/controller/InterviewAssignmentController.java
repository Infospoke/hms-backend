package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.AssignInterviewerRequest;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.IInterviewerAssignmentService;
import com.hms.service.wrappers.ApiResponse;

@RestController
@RequestMapping("/hms/interviewer-assignment")

public class InterviewAssignmentController {

	@Autowired
	private IInterviewerAssignmentService iInterviewerAssignmentService;

	@PostMapping("/assign")
	public ResponseEntity<ApiResponse<?>> assignInterviewers(

			@RequestBody AssignInterviewerRequest request) {

		return ResponseEntity.ok(iInterviewerAssignmentService.assignInterviewers(request));
	}

	@PostMapping("/list")
	public ResponseEntity<ApiResponse<?>> getAssignments(

			@RequestBody SpecificationFilterRequest request) {

		return ResponseEntity.ok(iInterviewerAssignmentService.getAssignments(request));
	}
}
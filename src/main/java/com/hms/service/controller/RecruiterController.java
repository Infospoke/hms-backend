package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.FilterRequest;
import com.hms.service.request.RecuriterAssignmentRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateRecruitersAssignmentRequest;
import com.hms.service.service.IRecruiterService;
import com.hms.service.wrappers.ApiResponse;

@RestController
@RequestMapping("/hms/recruiter")
public class RecruiterController {

	@Autowired
	private IRecruiterService recruiterService;

	@GetMapping("/counts")
	public ResponseEntity<ApiResponse<?>> getRecruiterCardsCounts() {
		ApiResponse<?> response = recruiterService.getRecruiterCardsCounts();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/job-assignment-list")
	public ResponseEntity<ApiResponse<?>> getAllRecruiterAssignmentList(
			@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = recruiterService.getAllRecruiterAssignmentList(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/job-assignment-summary/{jobId}")
	public ResponseEntity<ApiResponse<?>> getRecruiterAssignmentSummary(@PathVariable("jobId") Integer jobId) {

		ApiResponse<?> response = recruiterService.getRecruiterAssignmentSummary(jobId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/assigned-recruiters/{jobId}")
	public ResponseEntity<ApiResponse<?>> getAssignedRecruiterUserIds(@PathVariable("jobId") Integer jobId) {
		ApiResponse<?> response = recruiterService.getAssignedRecruiterUserIds(jobId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/job-assignment-details-list/{jobId}")
	public ResponseEntity<ApiResponse<?>> getRecruiterAssignmentDetailsList(@PathVariable("jobId") Integer jobId,
			@RequestBody FilterRequest request) {
		ApiResponse<?> response = recruiterService.getRecruiterAssignmentDetailsList(jobId, request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/my-job-assignments-count")
	public ResponseEntity<ApiResponse<?>> getMyJobAssignmentsCounts() {
		ApiResponse<?> response = recruiterService.getMyJobAssignmentsCounts();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/my-job-assignments")
	public ResponseEntity<ApiResponse<?>> getMyJobAssignments(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = recruiterService.getMyJobAssignments(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/update-recruiter-assignment")
	public ResponseEntity<ApiResponse<?>> updateRecruiterAssignment(
			@RequestBody UpdateRecruitersAssignmentRequest request) {
		ApiResponse<?> response = recruiterService.updateRecruiterAssignment(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	@PostMapping("/assign-more-recruiters")
	public ResponseEntity<ApiResponse<?>>AssignMoreRecruiters(@RequestBody RecuriterAssignmentRequest request) {
		ApiResponse<?> response=recruiterService.saveRecruiterAssignments(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	

}

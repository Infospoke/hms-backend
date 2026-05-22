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
import com.hms.service.request.SpecificationFilterRequest;
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

	@GetMapping("/job-assignment-summary/{srId}")
	public ResponseEntity<ApiResponse<?>> getRecruiterAssignmentSummary(@PathVariable("srId") String srId) {

		ApiResponse<?> response = recruiterService.getRecruiterAssignmentSummary(srId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/job-assignment-details-list/{srId}")
	public ResponseEntity<ApiResponse<?>> getRecruiterAssignmentDetailsList(@PathVariable("srId") String srId,@RequestBody FilterRequest request) {
		ApiResponse<?> response = recruiterService.getRecruiterAssignmentDetailsList(srId, request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/my-job-assignments-count")
	public ResponseEntity<ApiResponse<?>> getMyJobAssignmentsCounts() {
		ApiResponse<?> response = recruiterService.getMyJobAssignmentsCounts();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@PostMapping("/my-job-assignments")
	public ResponseEntity<ApiResponse<?>> getMyJobAssignments(@RequestBody SpecificationFilterRequest request){
		ApiResponse<?> response=recruiterService.getMyJobAssignments(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
}

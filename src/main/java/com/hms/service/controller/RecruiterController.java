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
	public ResponseEntity<ApiResponse<?>> getAllRecruiterAssignmentList(@RequestBody SpecificationFilterRequest request) {

		ApiResponse<?> response = recruiterService.getAllRecruiterAssignmentList(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/job-assignment-details/{jobId}")
	public ResponseEntity<ApiResponse<?>> getRecruiterAssignmentDetails(@PathVariable("jobId") Integer jobId) {

		ApiResponse<?> response = recruiterService.getRecruiterAssignmentDetails(jobId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

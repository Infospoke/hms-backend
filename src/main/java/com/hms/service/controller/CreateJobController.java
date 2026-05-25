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

import com.hms.service.request.CreateJobRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.ICreateJobService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

@RequestMapping("/hms/create-job")
@RestController

public class CreateJobController {
	@Autowired
	private ICreateJobService iCreateJobService;

	@PostMapping("/new-job")
	public ResponseEntity<ApiResponse<?>> createJob(@Valid @RequestBody CreateJobRequest request) {
		ApiResponse<?> response = iCreateJobService.createJob(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("details/{srId}")
	public ResponseEntity<ApiResponse<?>> getJobDetails(@PathVariable("srId") String srId) {

		ApiResponse<?> response = iCreateJobService.getJobDetails(srId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/get-recruiters")
	public ResponseEntity<ApiResponse<?>> getRecruiters(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = iCreateJobService.getRecruiters(request);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	@PostMapping("/get-job-details/{srId}")
	public ResponseEntity<ApiResponse<?>> getJobCreationDetails(@PathVariable String srId) {

		ApiResponse<?> response = iCreateJobService.getJobCreationDetails(srId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	}



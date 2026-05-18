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
import com.hms.service.service.ICreateJobService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

@RequestMapping("/hms/create-job")
@RestController

public class CreateJobController {
	@Autowired
	private ICreateJobService iCreateJobService;

	@PostMapping("/job/{srId}")
	public ResponseEntity<ApiResponse<?>> createJobFromSr(@PathVariable("srId") String srId,
			@Valid @RequestBody CreateJobRequest request) {
		ApiResponse<?> response = iCreateJobService.createJobFromSr(srId, request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("details/{srId}")
	public ResponseEntity<ApiResponse<?>> getCreateJobDetails(@PathVariable("srId") String srId) {

		ApiResponse<?> response = iCreateJobService.getCreateJobDetails(srId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

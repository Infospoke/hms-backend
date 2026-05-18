package com.hms.service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.service.ICreateJobService;
import com.hms.service.wrappers.ApiResponse;

@RequestMapping("/hms/create-job")
@RestController

public class CreateJobController {
	@Autowired
	private ICreateJobService iCreateJobService;

	@PostMapping("/new-job/{srId}")
	public ResponseEntity<ApiResponse<?>> createJob(@PathVariable("srId") String srId,@RequestParam(name = "additionalNotes", required = false) String additionalNotes) {
		ApiResponse<?> response = iCreateJobService.createJobFromSr(srId,additionalNotes);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.request.CandidateInterviewRequest;
import com.hms.service.service.ICandidateService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/hms/candidate")

@Validated
public class CandidateController {

	@Autowired
	private ICandidateService iCandidateService;

	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<?>> registerCandidate(@Valid @ModelAttribute CandidateCreationRequest request) {

		ApiResponse<?> response = iCandidateService.createCandidate(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/interviews")
	public ResponseEntity<ApiResponse<?>> getCandidateInterviews(@RequestBody CandidateInterviewRequest request) {
		ApiResponse<?> response = iCandidateService.getCandidateInterviews(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
}
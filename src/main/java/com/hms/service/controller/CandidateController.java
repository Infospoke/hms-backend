package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.request.CandidateCreationRequest;

import com.hms.service.request.LoginRequest;

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
	public ResponseEntity<ApiResponse<?>> createCandidate(@RequestPart("data") @Valid CandidateCreationRequest request,
			@RequestPart("resume") MultipartFile resume,
			@RequestPart(value = "additionalFile", required = false) MultipartFile additionalFile) {
		ApiResponse<?> response = iCandidateService.createCandidate(request, resume, additionalFile);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}


	@PostMapping("/login")
	public ResponseEntity<ApiResponse<?>> login(@RequestBody @Valid LoginRequest request) {
		ApiResponse<?> response = iCandidateService.login(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse<?>> forgotPassword(@RequestBody @Valid LoginRequest request) {
		ApiResponse<?> response = iCandidateService.forgotPassword(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("Authorization") String token) {
		ApiResponse<?> response = iCandidateService.logout(token);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/interviews")
	public ResponseEntity<ApiResponse<?>> getCandidateInterviews(@RequestBody CandidateInterviewRequest request) {
		ApiResponse<?> response = iCandidateService.getCandidateInterviews(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
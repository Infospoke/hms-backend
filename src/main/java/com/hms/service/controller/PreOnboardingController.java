package com.hms.service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.request.DashboardRequest;
import com.hms.service.service.IPreOnBoardingService;
import com.hms.service.wrappers.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/hms/pre-onboarding")
@Slf4j
public class PreOnboardingController {

	@Autowired
	private IPreOnBoardingService preOnBoardingService;


	@PostMapping(value = "/add-preonboarding", consumes = "multipart/form-data")
	public ResponseEntity<ApiResponse<?>> addPreOnBoarding(
	        @RequestParam Map<String, MultipartFile> files,
	        @RequestParam("data") String data) {
	    ApiResponse<?> response = preOnBoardingService.addPreOnBoarding(files, data);
	    return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
 
@GetMapping("/get-all-preonboardings")
	public ResponseEntity<ApiResponse<?>> getAllPreOnBoardingList() {
	    ApiResponse<?> response = preOnBoardingService.getAllPreOnBoardingList();
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
 
@GetMapping("/get-preonboarding-candidate-by-id/{id}")
	public ResponseEntity<ApiResponse<?>> getPreOnBoardingCandidateDetailsById(
	        @PathVariable("id") Integer id) {
	    ApiResponse<?> response = preOnBoardingService.getPreOnBoardingCandidateDetailsById(id);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
 
@PutMapping(value = "/update-preonboarding", consumes = "multipart/form-data")
	public ResponseEntity<ApiResponse<?>> updatePreOnBoarding(@RequestParam Map<String, MultipartFile> files,
	        @RequestParam("data") String data) {
	    ApiResponse<?> response = preOnBoardingService.updatePreOnBoarding(files, data);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
 
	@DeleteMapping("/delete-preonboarding-candidate-by-candidateid/{candidateId}")
	public ResponseEntity<ApiResponse<?>> deletePreOnBoardingCandidateById(
	        @PathVariable("candidateId") Integer candidateId) {
	    ApiResponse<?> response = preOnBoardingService.deletePreOnBoardingCandidateById(candidateId);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
 
	
	@PostMapping("/get-onboarding-dashboard-details")
	public ResponseEntity<ApiResponse<?>> getOnboardingDashboardDetails(
	        @RequestBody DashboardRequest request) {
 
	    ApiResponse<?> response = preOnBoardingService.getOnboardingDashboardDetails(request);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
 
@GetMapping("/view-document")
	public ResponseEntity<byte[]> viewPreOnboardingDocument(@RequestParam("key") String key) {
 
		return preOnBoardingService.viewDocument(key);
	}
 

}
package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.enums.FilterApplicantEnum;
import com.hms.service.request.JobApplicationRequest;
import com.hms.service.response.JobApplicantsResponse;
import com.hms.service.response.JobsCountryResponse;
import com.hms.service.service.IJobService;
import com.hms.service.wrappers.ApiResponse;
import java.util.List;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/hms/jobs")
public class JobsController {
	@Autowired
	private IJobService iJobService;


	@PostMapping(value = "/application", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<?>> applyJob(@RequestPart("data") @Valid JobApplicationRequest request,
			@RequestPart("cv") MultipartFile cv,
			@RequestPart(value = "additionalFile", required = false) MultipartFile additionalFile) {

		ApiResponse<?> response= iJobService.jobApplication(request, cv, additionalFile);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/get-all-jobs-applicants")
	public ResponseEntity<ApiResponse<?>> getAllJobApplicants() {
		ApiResponse<?> response = iJobService.getAllJobApplicants();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/get-all-jobs-applicant")
	public ResponseEntity<ApiResponse<?>> getAllJobApplicants(@RequestParam("jobId") Integer jobId,
			@RequestParam("filter") FilterApplicantEnum filter) {
 
		ApiResponse<?> response  = iJobService.getAllJobApplicants(jobId, filter);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
 
	
	@GetMapping("/get-applicant-details-by-id/{id}")
	public ResponseEntity<ApiResponse<JobApplicantsResponse>> getApplicantDetailsById(@PathVariable("id") Integer id) {
 
		ApiResponse<JobApplicantsResponse> response = iJobService.getApplicantDetailsById(id);
 
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
 

	
	@GetMapping("/get-all-jobs-dashboard-counts")
	public ResponseEntity<ApiResponse<?>> getAllJobsDashboardCounts() {
 
		ApiResponse<?> response = iJobService.getAllJobsDashboardCounts();
 
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/get-all-jobs-by-country")
	public ResponseEntity<?> getAllJobsByCountry(
			@RequestParam("jobCountry") String jobCountry) {
		ApiResponse<?> response = iJobService.getAllJobsByCountry(jobCountry);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

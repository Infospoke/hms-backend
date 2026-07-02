package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.enums.FilterApplicantEnum;
import com.hms.service.response.JobApplicantsResponse;
import com.hms.service.service.IJobService;
import com.hms.service.wrappers.ApiResponse;



@RestController
@RequestMapping("/hms/jobs")
public class JobsController {
	@Autowired
	private IJobService iJobService;

 
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

}

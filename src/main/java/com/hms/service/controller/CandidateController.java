package com.hms.service.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.response.JobTitleResponse;
import com.hms.service.service.CandidateService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/hms/candidate")
public class CandidateController {

	@Autowired
	private CandidateService candidateService;
	

	 @PostMapping(value = "/add-candidate", consumes = "multipart/form-data")
	 public ResponseEntity<ApiResponse<?>> addCandidate(
	         @RequestPart(value = "request") CandidateCreationRequest request,
	         @RequestPart(value = "offerLetter", required = false) MultipartFile offerLetter) {

	     ApiResponse<?> response = candidateService.addCandidate(request, offerLetter);

	     return new ResponseEntity<>(response, HttpStatus.CREATED);
	 }

	 @GetMapping("/get-all-candidates")
	 public ResponseEntity<ApiResponse<?>> getAllCandidates() {

	  ApiResponse<?> response = candidateService.getAllCandidates();

	  return new ResponseEntity<>(response, HttpStatus.OK);
	 }

	 @GetMapping("/get-candidate-by-id/{id}")
	 public ResponseEntity<ApiResponse<?>> getCandidateById(@PathVariable("id") int id) {

	     ApiResponse<?> response = candidateService.getCandidateById(id);

	     return new ResponseEntity<>(response, HttpStatus.OK);
	 }
	 @PutMapping(value="/update-candidate",consumes="multipart/form-data")
	 public ResponseEntity<ApiResponse<?>> updateCandidatebyId(
	         @RequestParam Map<String, MultipartFile> files,
	         @RequestParam("data") String data) {

	     ApiResponse<?> response = candidateService.updateCandidate(files, data);

	     return new ResponseEntity<>(response, HttpStatus.OK);
	 }

	 @GetMapping("/get-jobs-by-country/{country}")
	 public ResponseEntity<ApiResponse<JobTitleResponse>> jobsByCountry(@PathVariable("country") String country) {

	     ApiResponse<JobTitleResponse> response = candidateService.jobsByCountry(country);

	     return new ResponseEntity<>(response, HttpStatus.OK);
	 }
	 
	 @GetMapping("/validate-api")
	 public ResponseEntity<String> validateApi()
	 {
	     return new ResponseEntity<>(Constants.VALID_TOKEN, HttpStatus.OK);
	 }

	 @GetMapping("/download/{type}")
	 public void downloadOfferLetter(
	         @PathVariable String type,
	         @RequestParam(required = false, defaultValue = "download") String action,
	         @RequestParam LocalDateTime issueDate,
	         HttpServletResponse response) {

	     candidateService.downloadOfferLetter(issueDate, type, action, response);
	 }

}

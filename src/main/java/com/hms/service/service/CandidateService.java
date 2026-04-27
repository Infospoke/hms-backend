package com.hms.service.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.response.JobTitleResponse;
import com.hms.service.wrappers.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface CandidateService {

	ApiResponse<?> addCandidate(CandidateCreationRequest request, MultipartFile offerLetter);
	 
	ApiResponse<?> getAllCandidates();
 
	ApiResponse<?> getCandidateById(int id);
 
	ApiResponse<?> updateCandidate(Map<String, MultipartFile> files, String data);
 
	ApiResponse<JobTitleResponse> jobsByCountry(String country);
 
	void downloadOfferLetter(LocalDateTime issueDate, String type, String action, HttpServletResponse response);
}

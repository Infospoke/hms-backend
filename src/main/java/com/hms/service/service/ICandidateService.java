package com.hms.service.service;

import org.springframework.web.multipart.MultipartFile;
import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.request.CandidateInterviewRequest;
import com.hms.service.wrappers.ApiResponse;

public interface ICandidateService {
	
	ApiResponse<?> getCandidateInterviews(CandidateInterviewRequest request);

	ApiResponse<?> createCandidate(CandidateCreationRequest request, MultipartFile resume, MultipartFile additionalFile);


}
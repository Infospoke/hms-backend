package com.hms.service.service;


import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.request.CandidateInterviewRequest;
import com.hms.service.wrappers.ApiResponse;

public interface ICandidateService {

	ApiResponse<?> createCandidate(CandidateCreationRequest request);
	
	ApiResponse<?> getCandidateInterviews(CandidateInterviewRequest request);

}
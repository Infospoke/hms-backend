package com.hms.service.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.request.NegotiationFieldRequest;
import com.hms.service.request.LoginRequest;
import com.hms.service.response.LoginResponse;
import com.hms.service.wrappers.ApiResponse;

public interface ICandidateService {

	ApiResponse<?> getCandidateInterviews();

	ApiResponse<?> createCandidate(CandidateCreationRequest request, MultipartFile resume,
			MultipartFile additionalFile);

	ApiResponse<LoginResponse> login(LoginRequest request);

	ApiResponse<?> forgotPassword(LoginRequest request);

	ApiResponse<?> logout(String token);


	ApiResponse<?> candidateOffers();


	ApiResponse<?> negotiateOffer(NegotiationFieldRequest request, List<MultipartFile> files);

	ApiResponse<?> getMyApplications();

	String generateCandidateId();

}
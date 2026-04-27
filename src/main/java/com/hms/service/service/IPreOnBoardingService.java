package com.hms.service.service;


import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.request.DashboardRequest;
import com.hms.service.wrappers.ApiResponse;


public interface IPreOnBoardingService {

	ApiResponse<?> addPreOnBoarding(Map<String, MultipartFile> files, String data);

	

	ApiResponse<?> getPreOnBoardingCandidateDetailsById(Integer candidateId);

	ApiResponse<?> getAllPreOnBoardingList();

	ApiResponse<?> updatePreOnBoarding(Map<String, MultipartFile> files, String data);

	

	ResponseEntity<byte[]> viewDocument(String key);

	ApiResponse<?> deletePreOnBoardingCandidateById(Integer candidateId);



	ApiResponse<?> getOnboardingDashboardDetails(DashboardRequest request);

}

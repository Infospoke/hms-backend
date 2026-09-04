package com.hms.service.service;

import org.springframework.web.multipart.MultipartFile;

import com.hms.service.enums.FilterApplicantEnum;
import com.hms.service.request.JobApplicationRequest;
import com.hms.service.response.JobApplicantsResponse;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;


public interface IJobService {
	


    ApiResponse<?> getAllJobsDashboardCounts();

	ApiResponse<?> getAllJobApplicants(Integer jobId, FilterApplicantEnum filter);

	ApiResponse<JobApplicantsResponse> getApplicantDetailsById(Integer id);

	ApiResponse<?> getAllJobApplicants();

	ApiResponse<?> jobApplication(@Valid JobApplicationRequest request, MultipartFile cv, MultipartFile additionalFile);


	ApiResponse<?> getAllJobsByCountry(String country);
	 

}

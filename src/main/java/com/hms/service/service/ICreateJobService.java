package com.hms.service.service;

import com.hms.service.request.CreateJobRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateJobDetailsRequest;
import com.hms.service.wrappers.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface ICreateJobService {

	ApiResponse<?> getJobDetails(String srId);

	ApiResponse<?> createJob(CreateJobRequest request);

	ApiResponse<?> getRecruiters(SpecificationFilterRequest request);

	ApiResponse<?> getJobCreationDetails(Integer jobId);

	void downloadFile(Integer appId, String type, String action, HttpServletResponse response);

	ApiResponse<?> updateJobDetailsById(UpdateJobDetailsRequest request);

	ApiResponse<?> getAllJobs(SpecificationFilterRequest request);




}

package com.hms.service.service;

import com.hms.service.enums.FilterApplicantEnum;
import com.hms.service.request.JobRequest;

import com.hms.service.response.JobApplicantsResponse;
import com.hms.service.wrappers.ApiResponse;


public interface IJobService {
	
	ApiResponse<?> addJob(JobRequest request);
	
	ApiResponse<?> deleteJob(Integer jobId);
	
	ApiResponse<?> getJobDetailsById(Integer id);
	
	ApiResponse<?> updateJobDetailsById(JobRequest request);
	
	ApiResponse<?> getAllSkills();

	ApiResponse<?> getAllJobs(Boolean isOpen);

    ApiResponse<?> getAllJobsDashboardCounts();

	ApiResponse<?> getAllJobApplicants(Integer jobId, FilterApplicantEnum filter);

	ApiResponse<JobApplicantsResponse> getApplicantDetailsById(Integer id);

	ApiResponse<?> getAllJobApplicants();

	 

}

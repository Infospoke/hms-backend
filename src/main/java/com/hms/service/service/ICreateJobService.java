package com.hms.service.service;

import com.hms.service.request.CreateJobRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.wrappers.ApiResponse;

public interface ICreateJobService {

	ApiResponse<?> getJobDetails(String srId);

	ApiResponse<?> createJob(CreateJobRequest request);

	ApiResponse<?> getRecruiters(SpecificationFilterRequest request);

}

package com.hms.service.service;

import com.hms.service.request.CreateJobRequest;
import com.hms.service.wrappers.ApiResponse;

public interface ICreateJobService {

	ApiResponse<?> createJobFromSr(String srId, CreateJobRequest request);

	ApiResponse<?> getCreateJobDetails(String srId);

}

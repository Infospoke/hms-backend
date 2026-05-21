package com.hms.service.service;

import com.hms.service.request.FilterRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IRecruiterService {

	ApiResponse<?> getRecruiterCardsCounts();
	ApiResponse<?> getAllRecruiterAssignmentList(SpecificationFilterRequest request);
	ApiResponse<?> getMyJobAssignmentsCounts();
	ApiResponse<?> getRecruiterAssignmentDetails(Integer jobId, FilterRequest request);

}

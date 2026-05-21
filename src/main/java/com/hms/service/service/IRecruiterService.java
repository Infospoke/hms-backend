package com.hms.service.service;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IRecruiterService {

	ApiResponse<?> getRecruiterCardsCounts();
	ApiResponse<?> getAllRecruiterAssignmentList(SpecificationFilterRequest request);
	ApiResponse<?> getRecruiterAssignmentDetails(Integer jobId);
	ApiResponse<?> getMyJobAssignmentsCounts();

}

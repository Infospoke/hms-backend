package com.hms.service.service;

import com.hms.service.request.FilterRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateRecruitersAssignmentRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IRecruiterService {

	ApiResponse<?> getRecruiterCardsCounts();
	ApiResponse<?> getAllRecruiterAssignmentList(SpecificationFilterRequest request);
	ApiResponse<?> getMyJobAssignmentsCounts();
	ApiResponse<?> getMyJobAssignments(SpecificationFilterRequest request);
	ApiResponse<?> getRecruiterAssignmentSummary(String srId);
	ApiResponse<?> getRecruiterAssignmentDetailsList(String srId, FilterRequest request);
	ApiResponse<?> updateRecruiterAssignment(UpdateRecruitersAssignmentRequest request);


}

package com.hms.service.service;

import com.hms.service.request.FilterRequest;
import com.hms.service.request.RecuriterAssignmentRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateRecruitersAssignmentRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IRecruiterService {

	ApiResponse<?> getRecruiterCardsCounts();
	ApiResponse<?> getAllRecruiterAssignmentList(SpecificationFilterRequest request);
	ApiResponse<?> getMyJobAssignmentsCounts();
	ApiResponse<?> getMyJobAssignments(SpecificationFilterRequest request);

	ApiResponse<?> getRecruiterAssignmentSummary(Integer jobId);
	ApiResponse<?> getRecruiterAssignmentDetailsList(Integer jobId, FilterRequest request);
	ApiResponse<?> updateRecruiterAssignment(UpdateRecruitersAssignmentRequest request);
	
	ApiResponse<?> saveRecruiterAssignments(RecuriterAssignmentRequest request);



}

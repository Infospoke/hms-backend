package com.hms.service.service;


import com.hms.service.request.AssignInterviewerRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IInterviewerAssignmentService {

	ApiResponse<?> assignInterviewers(AssignInterviewerRequest request);

	ApiResponse<?> getAssignments(SpecificationFilterRequest request);

	ApiResponse<?> getInterviewerCounts();

	ApiResponse<?> getAssignmentDetails(Integer jobId);

	
}
package com.hms.service.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.request.ListingRequest;
import com.hms.service.request.SRFilterRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.StaffingRequisitionRequest;
import com.hms.service.request.UpdateSrRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IStaffingRequisitionService {

	ApiResponse<?> newStaffingRequisition(StaffingRequisitionRequest request, MultipartFile file);

	ApiResponse<?> getBySrId(String srId);

	ApiResponse<?> srApproval(UpdateSrRequest request);

	ApiResponse<?> getSrCounts();


	ApiResponse<?> assignedSrsForApprovals(SpecificationFilterRequest request);
	
	ApiResponse<?> getAllApprovedServiceRequests(SpecificationFilterRequest request);

	ApiResponse<?> getAllSrListCount();

	ApiResponse<?> getAllSrList(SpecificationFilterRequest request);
    

}

package com.hms.service.service;

import org.springframework.web.multipart.MultipartFile;

import com.hms.service.request.SRFilterRequest;
import com.hms.service.request.StaffingRequisitionRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IStaffingRequisitionService {

	ApiResponse<?> newStaffingRequisition(StaffingRequisitionRequest request, MultipartFile file);

	ApiResponse<?> getBySrId(String srId);

	ApiResponse<?> getAll(SRFilterRequest request);

}

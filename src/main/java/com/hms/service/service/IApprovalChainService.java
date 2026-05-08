package com.hms.service.service;

import com.hms.service.request.ApprovalChainRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateApprovalChainRequest;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

public interface IApprovalChainService {

	ApiResponse<?> getApprovalChainCounts();
	
	ApiResponse<?> getApprovalChainById(Integer id);

	ApiResponse<?> createApprovalChain(@Valid ApprovalChainRequest request);

	ApiResponse<?> updateApprovalChain(UpdateApprovalChainRequest request);

	ApiResponse<?> getApprovalChainsList(SpecificationFilterRequest request);

}

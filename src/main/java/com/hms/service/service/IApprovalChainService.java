package com.hms.service.service;

import com.hms.service.request.FilterRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IApprovalChainService {

	ApiResponse<?> getApprovalChainCounts();

	ApiResponse<?> getApprovalChainsList(FilterRequest request);
	
	ApiResponse<?> getApprovalChainById(Integer id);
}

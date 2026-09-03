package com.hms.service.service;

import com.hms.service.request.ClientRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

public interface IClientService {

	ApiResponse<?> createClient(@Valid ClientRequest request);
	
	ApiResponse<?>  getClientList(
	            SpecificationFilterRequest request);

	ApiResponse<?> getClientById(Integer id);

}

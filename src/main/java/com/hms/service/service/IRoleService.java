package com.hms.service.service;

import com.hms.service.request.RolesRequest;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

public interface IRoleService {

	ApiResponse<?> addRole(@Valid RolesRequest request);





	

}

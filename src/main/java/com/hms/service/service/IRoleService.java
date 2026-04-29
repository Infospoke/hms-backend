package com.hms.service.service;

import com.hms.service.request.RolesRequest;
import com.hms.service.request.UpdatePermissionRequest;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

public interface IRoleService {

	ApiResponse<?> addRolePermissions(@Valid RolesRequest request);

	ApiResponse<?> getAllRolePermissions();

	ApiResponse<?> updateRolePermissions(@Valid UpdatePermissionRequest request);



	ApiResponse<?> usersByRoleId(Integer roleId);






	

}

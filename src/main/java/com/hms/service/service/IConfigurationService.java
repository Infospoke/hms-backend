package com.hms.service.service;

import java.util.List;

import com.hms.service.request.RolesByDepartmentIdsRequest;
import com.hms.service.response.DropDownResponse;
import com.hms.service.wrappers.ApiResponse;

public interface IConfigurationService {
	
	ApiResponse<List<?>> getAllBusinessUnits();

    ApiResponse<List<?>> getDepartmentsByBusinessUnit(Integer businessUnitId);

    ApiResponse<List<?>> getRolesByDepartment(Integer departmentId);
    
    ApiResponse<List<?>> getEmploymentTypes();
    
    ApiResponse<List<?>> getUserTypes();

	ApiResponse<?> getAllModules();
	
	ApiResponse<List<?>> getSeniorityLevels();

	ApiResponse<List<?>> getTravelRequirements();

	ApiResponse<?> getJr();

	ApiResponse<?> getAllFunctionalities();

	ApiResponse<List<?>> getAllDepartments();

	ApiResponse<List<?>> getAllFunctionality();
	
	ApiResponse<List<?>> getUsersByRole();

	ApiResponse<List<?>> getAiOptions();

	ApiResponse<List<?>> getRolesByDepartments(RolesByDepartmentIdsRequest request);

	ApiResponse<List<?>> getUsersWithCreatePermission();
	
	

}

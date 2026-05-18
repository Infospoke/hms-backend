package com.hms.service.service;

import java.util.List;
import com.hms.service.response.DropDownResponse;
import com.hms.service.wrappers.ApiResponse;

public interface IConfigurationService {
	
	ApiResponse<List<DropDownResponse>> getAllBusinessUnits();

    ApiResponse<List<DropDownResponse>> getDepartmentsByBusinessUnit(Integer businessUnitId);

    ApiResponse<List<DropDownResponse>> getRolesByDepartment(Integer departmentId);
    
    ApiResponse<List<DropDownResponse>> getEmploymentTypes();
    
    ApiResponse<List<DropDownResponse>> getUserTypes();

	ApiResponse<?> getAllModules();
	
	ApiResponse<List<DropDownResponse>> getSeniorityLevels();

	ApiResponse<List<DropDownResponse>> getTravelRequirements();

	ApiResponse<?> getJr();

	ApiResponse<?> getAllFunctionalities();

	ApiResponse<List<DropDownResponse>> getAllDepartments();

	ApiResponse<List<DropDownResponse>> getAllFunctionality();
	
	ApiResponse<List<DropDownResponse>> getUsersByRole();
	
	

}

package com.hms.service.service;

import java.util.List;

import com.hms.service.response.UserDropDownResponse;
import com.hms.service.wrappers.ApiResponse;

public interface IConfigurationService {
	
	ApiResponse<List<UserDropDownResponse>> getAllBusinessUnits();

    ApiResponse<List<UserDropDownResponse>> getDepartmentsByBusinessUnit(Integer businessUnitId);

    ApiResponse<List<UserDropDownResponse>> getRolesByDepartment(Integer departmentId);
    
    ApiResponse<List<UserDropDownResponse>> getEmploymentTypes();
    
    ApiResponse<List<UserDropDownResponse>> getUserTypes();

	ApiResponse<?> getAllModules();

}

package com.hms.service.service;

import java.util.List;

import com.hms.service.response.UserDropDownResponse;

public interface IUserService {

    List<UserDropDownResponse> getAllBusinessUnits();

    List<UserDropDownResponse> getDepartmentsByBusinessUnit(Integer businessUnitId);

    List<UserDropDownResponse> getRolesByDepartment(Integer departmentId);
}
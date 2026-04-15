package com.hms.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.response.UserDropDownResponse;
import com.hms.service.service.IUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class UserController {

	@Autowired
    private  IUserService iUserService;

    
    @GetMapping("/business-units")
    public List<UserDropDownResponse> getBusinessUnits() {
        return iUserService.getAllBusinessUnits();
    }


    @GetMapping("/departments/{businessUnitId}")
    public List<UserDropDownResponse> getDepartments(@PathVariable Integer businessUnitId) {
        return iUserService.getDepartmentsByBusinessUnit(businessUnitId);
    }

  
    @GetMapping("/roles/{departmentId}")
    public List<UserDropDownResponse> getRoles(@PathVariable Integer departmentId) {
        return iUserService.getRolesByDepartment(departmentId);
    }
}
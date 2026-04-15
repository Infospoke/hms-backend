package com.hms.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.repository.BusinessUnitRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.response.UserDropDownResponse;
import com.hms.service.service.IUserService;


@Service

public class UserServiceImpl implements IUserService {

	@Autowired
    private  BusinessUnitRepository businessUnitRepository;
	
	@Autowired
    private DepartmentsRepository departmentsRepository;
	
	@Autowired
    private RolesRepository rolesRepository;

    @Override
    public List<UserDropDownResponse> getAllBusinessUnits() {
        return businessUnitRepository.findAll()
                .stream()
                .map(bu -> new UserDropDownResponse(bu.getId(), bu.getBusinessName()))
                .toList();
    }

    @Override
    public List<UserDropDownResponse> getDepartmentsByBusinessUnit(Integer businessUnitId) {
        return departmentsRepository.findByBusinessUnitId(businessUnitId)
                .stream()
                .map(dep -> new UserDropDownResponse(dep.getId(), dep.getDepartmentName()))
                .toList();
    }

    @Override
    public List<UserDropDownResponse> getRolesByDepartment(Integer departmentId) {
        return rolesRepository.findByDepartmentId(departmentId)
                .stream()
                .map(role -> new UserDropDownResponse(role.getId(), role.getRoleName()))
                .toList();
    }
}
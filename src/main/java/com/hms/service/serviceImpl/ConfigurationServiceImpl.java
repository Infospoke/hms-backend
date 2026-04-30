package com.hms.service.serviceImpl;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.dto.JrResponseDto;
import com.hms.service.entity.ModuleEntity;
import com.hms.service.repository.BusinessUnitRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.EmployementTypeRepository;
import com.hms.service.repository.ModuleRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.SeniorityLevelRepository;
import com.hms.service.repository.TravelRequirementRepository;
import com.hms.service.repository.UserTypeRepository;
import com.hms.service.response.JrResponse;
import com.hms.service.response.ModuleResponse;
import com.hms.service.response.UserDropDownResponse;
import com.hms.service.service.IConfigurationService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConfigurationServiceImpl implements IConfigurationService { 
	
	@Autowired
	private BusinessUnitRepository businessUnitRepository;

	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private EmployementTypeRepository employementTypeRepository;
	
	@Autowired
	private UserTypeRepository userTypeRepository;
	
	@Autowired
	private ModuleRepository moduleRepository;
	
	@Autowired
	private SeniorityLevelRepository seniorityLevelRepository;
	
	@Autowired
	private TravelRequirementRepository travelRequirementRepository;
	
	@Autowired
	private PositionBasicsRepository positionBasicsRepository;
	

	
	@Override
	public ApiResponse<List<UserDropDownResponse>> getAllBusinessUnits() {
		 log.info("ConfigurationServiceImpl::Inside the getAllBusinessUnits method"); 

		List<UserDropDownResponse> response = businessUnitRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
				.map(bu -> new UserDropDownResponse(bu.getId(), bu.getBusinessName())).toList();
		
		 log.info("ConfigurationServiceImpl::Exit from the getAllBusinessUnits method"); 
		 
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.BUSINESS_UNITS_FETCHED_SUCCESSFULLY, response);
	}
	
	

	@Override
	public ApiResponse<List<UserDropDownResponse>> getDepartmentsByBusinessUnit(Integer businessUnitId) {
		 log.info("ConfigurationServiceImpl::Inside the getDepartmentsByBusinessUnit method"); 
		 
		List<UserDropDownResponse> response = departmentsRepository.findByBusinessUnitId(businessUnitId, Sort.by("id")).stream()
				.map(dep -> new UserDropDownResponse(dep.getId(), dep.getDepartmentName())).toList();
		
		 log.info("ConfigurationServiceImpl::Exit from the getAllBusinessUnits method"); 
		 
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.DEPARTMENTS_FETCHED_SUCCESSFULLY, response);
	}

	@Override
	public ApiResponse<List<UserDropDownResponse>> getRolesByDepartment(Integer departmentId) {
		 log.info("ConfigurationServiceImpl::Inside the getRolesByDepartment method"); 
		 
		List<UserDropDownResponse> response = rolesRepository.findByDepartmentId(departmentId,Sort.by("id")).stream()
				.map(role -> new UserDropDownResponse(role.getId(), role.getRoleName())).toList();
		
		log.info("ConfigurationServiceImpl::Exit from the getRolesByDepartment method"); 
		
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.ROLES_FETCHED_SUCCESSFULLY, response);

	}

	@Override
	public ApiResponse<List<UserDropDownResponse>> getEmploymentTypes() {
		log.info("ConfigurationServiceImpl::Inside the getEmploymentTypes method"); 
		
		List<UserDropDownResponse> response = employementTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
				.map(emp -> new UserDropDownResponse(emp.getId(), emp.getEmployementType())).toList();
		
		log.info("ConfigurationServiceImpl::Exit from the getEmploymentTypes method"); 
		
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.EMPLOYMENT_TYPE_FETCHED_SUCCESSFULLY, response);
	}


	@Override
	public ApiResponse<List<UserDropDownResponse>> getUserTypes() {
		log.info("ConfigurationServiceImpl::Inside the getUserTypes method"); 
	    List<UserDropDownResponse> response = userTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
	            .stream()
	            .map(user -> new UserDropDownResponse(
	                    user.getId(),
	                    user.getUserType()
	            ))
	            .toList();
	    log.info("ConfigurationServiceImpl::Exit from the getUserTypes method"); 
	    return ApiResponse.success(ResponseCode.SUCCESS, Constants.USER_TYPES_FETCHED_SUCCESSFULLY, response);
	}
	
	@Override
	public ApiResponse<?> getAllModules() {

	    log.info("ConfigurationServiceImpl: Inside getAllModules method");

	    List<ModuleEntity> moduleEntity = moduleRepository.findAll();

	    if (moduleEntity.isEmpty()) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.NO_MODULES_FOUND
	        );
	    }

	    Map<Integer, List<ModuleEntity>> grouped =
	            moduleEntity.stream().collect(Collectors.groupingBy(
	                    m -> m.getParentId() == null ? 0 : m.getParentId()
	            ));

	    List<ModuleEntity> parents = grouped.getOrDefault(0, Collections.emptyList());

	    List<ModuleResponse> moduleList = parents.stream()
	            .sorted(Comparator.comparing(ModuleEntity::getModuleId)) 
	            .map(parent -> {

	                ModuleResponse parentRes = new ModuleResponse();
	                parentRes.setModuleId(parent.getModuleId());
	                parentRes.setModuleName(parent.getModuleName());

	            
	                List<ModuleResponse> subModules =
	                        grouped.getOrDefault(parent.getModuleId(), Collections.emptyList())
	                                .stream()
	                                .sorted(Comparator.comparing(ModuleEntity::getModuleId)) 
	                                .map(child -> {
	                                    ModuleResponse sub = new ModuleResponse();
	                                    sub.setModuleId(child.getModuleId());
	                                    sub.setModuleName(child.getModuleName());
	                                    return sub; 
	                                })
	                                .toList();

	                parentRes.setSubModules(subModules);

	                return parentRes;

	            })
	            .toList();

	    log.info("ConfigurationServiceImpl: Exit from getAllModules method");

	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            Constants.MODULE_FETCH_SUCCESS,
	            moduleList
	    );
	}



	@Override
	public ApiResponse<List<UserDropDownResponse>> getSeniorityLevels() {
	    log.info("ConfigurationServiceImpl::Inside the getSeniorityLevels method");
	    List<UserDropDownResponse> response = seniorityLevelRepository.findAll(Sort.by("id"))
	            .stream()
	            .map(level -> new UserDropDownResponse(
	                    level.getId(),
	                    level.getSeniorityLevel() 
	            )).toList();
	    log.info("ConfigurationServiceImpl::Exit from getSeniorityLevels method");
	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            Constants.SENIORITY_FETCHED_SUCCESSFULLY,
	            response
	    );
	    
	}

	@Override
	public ApiResponse<List<UserDropDownResponse>> getTravelRequirements() {
	    log.info("ConfigurationServiceImpl::Inside getTravelRequirements method");
	    List<UserDropDownResponse> response = travelRequirementRepository.findAll(Sort.by("id"))
	            .stream()
	            .map(travel -> new UserDropDownResponse(
	                    travel.getId(),
	                    travel.getTravelRequirement()
	            )).toList();
	    log.info("ConfigurationServiceImpl::Exit from getTravelRequirements method");
	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            Constants.TRAVEL_REQUIREMENTS_FETCHED_SUCCESSFULLY,
	            response
	    );
	}



	@Override
	public ApiResponse<?> getJr() {

	    log.info("ConfigurationServiceImpl :: getJr");

	    List<JrResponse> responseList = positionBasicsRepository.findApprovedJrDetails();
       
        List<JrResponseDto> responsesList = responseList.stream()
        	    .map(p -> new JrResponseDto(p.getSrId(), p.getJobTitle()))
        	    .collect(Collectors.toList());
        if (responsesList.isEmpty()) {
            return ApiResponse.success(ResponseCode.SUCCESS, "success", Collections.emptyList());
        }

	    return ApiResponse.success(ResponseCode.SUCCESS, "success",responsesList);
	}
}

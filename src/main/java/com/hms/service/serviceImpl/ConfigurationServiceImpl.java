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
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.repository.ModuleRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.SeniorityLevelRepository;
import com.hms.service.repository.TravelRequirementRepository;
import com.hms.service.repository.UserTypeRepository;
import com.hms.service.response.DropDownResponse;
import com.hms.service.response.JrResponse;
import com.hms.service.response.ModuleResponse;
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
	
	@Autowired
	private FunctionalityRepository functionalityRepository;
	

	
	@Override
	public ApiResponse<List<DropDownResponse>> getAllBusinessUnits() {
		 log.info("ConfigurationServiceImpl::Inside the getAllBusinessUnits method"); 

		List<DropDownResponse> response = businessUnitRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
				.map(bu -> new DropDownResponse(bu.getId(), bu.getBusinessName())).toList();
		
		 log.info("ConfigurationServiceImpl::Exit from the getAllBusinessUnits method"); 
		 
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.BUSINESS_UNITS_FETCHED_SUCCESSFULLY, response);
	}
	
	@Override
	public ApiResponse<List<DropDownResponse>> getAllDepartments() {
		 log.info("ConfigurationServiceImpl::Inside the getAllDepartments method"); 

		List<DropDownResponse> response = departmentsRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
				.map(bu -> new DropDownResponse(bu.getId(), bu.getDepartmentName())).toList();
		
		 log.info("ConfigurationServiceImpl::Exit from the getAllBusinessUnits method"); 
		 
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.DEPARTMENTS_FETCHED_SUCCESSFULLY, response);
	}
	
	

	@Override
	public ApiResponse<List<DropDownResponse>> getDepartmentsByBusinessUnit(Integer businessUnitId) {
		 log.info("ConfigurationServiceImpl::Inside the getDepartmentsByBusinessUnit method"); 
		 
		List<DropDownResponse> response = departmentsRepository.findByBusinessUnitId(businessUnitId, Sort.by("id")).stream()
				.map(dep -> new DropDownResponse(dep.getId(), dep.getDepartmentName())).toList();
		
		 log.info("ConfigurationServiceImpl::Exit from the getAllBusinessUnits method"); 
		 
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.DEPARTMENTS_FETCHED_SUCCESSFULLY, response);
	}

	@Override
	public ApiResponse<List<DropDownResponse>> getRolesByDepartment(Integer departmentId) {
		 log.info("ConfigurationServiceImpl::Inside the getRolesByDepartment method"); 
		 
		List<DropDownResponse> response = rolesRepository.findByDepartmentId(departmentId,Sort.by("id")).stream()
				.map(role -> new DropDownResponse(role.getId(), role.getRoleName())).toList();
		
		log.info("ConfigurationServiceImpl::Exit from the getRolesByDepartment method"); 
		
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.ROLES_FETCHED_SUCCESSFULLY, response);

	}

	@Override
	public ApiResponse<List<DropDownResponse>> getEmploymentTypes() {
		log.info("ConfigurationServiceImpl::Inside the getEmploymentTypes method"); 
		
		List<DropDownResponse> response = employementTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
				.map(emp -> new DropDownResponse(emp.getId(), emp.getEmployementType())).toList();
		
		log.info("ConfigurationServiceImpl::Exit from the getEmploymentTypes method"); 
		
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.EMPLOYMENT_TYPE_FETCHED_SUCCESSFULLY, response);
	}


	@Override
	public ApiResponse<List<DropDownResponse>> getUserTypes() {
		log.info("ConfigurationServiceImpl::Inside the getUserTypes method"); 
	    List<DropDownResponse> response = userTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
	            .stream()
	            .map(user -> new DropDownResponse(
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
	public ApiResponse<List<DropDownResponse>> getSeniorityLevels() {
	    log.info("ConfigurationServiceImpl::Inside the getSeniorityLevels method");
	    List<DropDownResponse> response = seniorityLevelRepository.findAll(Sort.by("id"))
	            .stream()
	            .map(level -> new DropDownResponse(
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
	public ApiResponse<List<DropDownResponse>> getTravelRequirements() {
	    log.info("ConfigurationServiceImpl::Inside getTravelRequirements method");
	    List<DropDownResponse> response = travelRequirementRepository.findAll(Sort.by("id"))
	            .stream()
	            .map(travel -> new DropDownResponse(
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
	
	@Override
	public ApiResponse<?> getAllFunctionalities() {

		log.info("ConfigurationServiceImpl::Inside getAllFunctionalities method");

		List<DropDownResponse> response = functionalityRepository.findByIsChaincreatedFalse().stream()
				.map(func -> new DropDownResponse(func.getId(), func.getFunctionalityName())).toList();
		log.info("ConfigurationServiceImpl::Exit from getAllFunctionalities method");

		return ApiResponse.success(ResponseCode.SUCCESS, "Functionalities fetched successfully", response);
	}
	
	
	@Override
	public ApiResponse<List<DropDownResponse>> getAllFunctionality() {
		 log.info("ConfigurationServiceImpl::Inside the getAllFunctionality method"); 

		List<DropDownResponse> response = functionalityRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
				.map(fu -> new DropDownResponse(fu.getId(), fu.getFunctionalityName())).toList();
		
		 log.info("ConfigurationServiceImpl::Exit from the getAllFunctionality method"); 
		 
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.DEPARTMENTS_FETCHED_SUCCESSFULLY, response);
	}
	
}

package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.entity.PermissionEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.repository.ModuleRepository;
import com.hms.service.repository.PermissionRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.request.ModulePermissionRequest;
import com.hms.service.request.PermissionRequest;
import com.hms.service.request.RolesRequest;
import com.hms.service.request.UpdatePermissionRequest;
import com.hms.service.response.ModulePermissionResponse;
import com.hms.service.response.RolePermissionResponse;
import com.hms.service.service.IRoleService;
import com.hms.service.utils.SequenceGenerator;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RolesServiceImpl implements IRoleService {

	@Autowired
	private RolesRepository roleInfoRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private ModuleRepository moduleRepository;

	@Autowired
	private SequenceGenerator sequenceGenerator;

	@Override
	public ApiResponse<?> addRolePermissions(@Valid RolesRequest request) {

		log.info("RoleInfoServiceImpl::Inside the addRole method");

		RolesEntity roleInfoEntity = roleInfoRepository.findByRoleNameIgnoreCase(request.getRoleName());
		if (roleInfoEntity != null) {
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.ROLE_NAME_ALREADY_EXISTS);

		}

		RolesEntity entity = new RolesEntity();
		entity.setRoleName(request.getRoleName());
		entity.setRoleId(sequenceGenerator.generateRoleId());
		entity.setDescription(request.getDescription());
		entity.setCreatedDate(LocalDateTime.now());
		entity.setBusinessUnit(request.getBusinessUnit());
		entity.setDepartmentName(request.getDepartmentName());

		RolesEntity savedRole = roleInfoRepository.save(entity);

		PermissionRequest permReq = request.getPermission();

		List<PermissionEntity> permissionList = new ArrayList<>();

		for (ModulePermissionRequest module : request.getPermission().getModules()) {

			PermissionEntity permissionEntity = new PermissionEntity();

			permissionEntity.setPermissionId(sequenceGenerator.generatePermissionId());

			permissionEntity.setRoleId(savedRole.getRoleId());
			permissionEntity.setModuleId(module.getModuleId());

			permissionEntity.setCreate(module.getCreate());
			permissionEntity.setView(module.getView());
			permissionEntity.setEdit(module.getEdit());
			permissionEntity.setDelete(module.getDelete());

			permissionEntity.setCreatedBy(permReq.getCreatedBy());
			permissionEntity.setCreatedDate(LocalDateTime.now());

			permissionList.add(permissionEntity);
		}

		permissionRepository.saveAll(permissionList);
		log.info("RoleInfoServiceImpl::Exit from the the addRole method");

		return ApiResponse.success(Constants.ROLE_ADDED_SUCCESSFULLY);
	}

	@Override
	public ApiResponse<?> getAllRolePermissions() {

		log.info("RoleInfoServiceImpl::Inside the getAllRolePermissions method");

		List<Object[]> results = permissionRepository.fetchPermissionDetails();

		Map<Integer, RolePermissionResponse> roleMap = new HashMap<>();

		for (Object[] row : results) {

			Integer roleId = (Integer) row[0];
			String roleName = (String) row[1];
			Integer moduleId = (Integer) row[2];
			String moduleName = (String) row[3];
			Boolean create = (Boolean) row[4];
			Boolean view = (Boolean) row[5];
			Boolean edit = (Boolean) row[6];
			Boolean delete = (Boolean) row[7];

			RolePermissionResponse role = roleMap.get(roleId);
			if (role == null) {
				role = new RolePermissionResponse();
				role.setRoleId(roleId);
				role.setRoleName(roleName);
				role.setModules(new ArrayList<>());
				roleMap.put(roleId, role);
			}

			ModulePermissionResponse module = new ModulePermissionResponse();
			module.setModuleId(moduleId);
			module.setModuleName(moduleName);
			module.setCreate(create);
			module.setView(view);
			module.setEdit(edit);
			module.setDelete(delete);

			role.getModules().add(module);
			role.setTotalModules(role.getModules().size());
		}

		List<RolePermissionResponse> rolesList = new ArrayList<>(roleMap.values());
		log.info("RoleInfoServiceImpl::Exit from the getAllRolePermissions method");
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.ROLE_PERMISSION_DETAILS_FETCHED_SUCCESSFULLY,rolesList);
	}

	@Override
	public ApiResponse<?> updateRolePermissions(@Valid UpdatePermissionRequest request) {

		log.info("RoleInfoServiceImpl::Inside the updateRolePermissions method");

		Integer roleId = request.getRoleId();
		if(roleId==null) {
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.ROLE_REQUIRED);
		}

		List<PermissionEntity> existingPermissions = permissionRepository.findByRoleId(roleId);

		Map<Integer, PermissionEntity> existingPermissionEntity = new HashMap<>();

		for (PermissionEntity entity : existingPermissions) {
			existingPermissionEntity.put(entity.getModuleId(), entity);
		}

		List<PermissionEntity> permissionEntityList = new ArrayList<>();

		PermissionRequest permReq = request.getPermission();

		for (ModulePermissionRequest module : permReq.getModules()) {

			PermissionEntity permissionEntity;

			if (existingPermissionEntity.containsKey(module.getModuleId())) {

				permissionEntity = existingPermissionEntity.get(module.getModuleId());

				permissionEntity.setCreate(module.getCreate());
				permissionEntity.setView(module.getView());
				permissionEntity.setEdit(module.getEdit());
				permissionEntity.setDelete(module.getDelete());

				permissionEntity.setUpdatedBy(permReq.getUpdatedBy());
				permissionEntity.setUpdatedDate(LocalDateTime.now());

			} else {

				permissionEntity = new PermissionEntity();

				permissionEntity.setPermissionId(sequenceGenerator.generatePermissionId());
				permissionEntity.setRoleId(roleId);
				permissionEntity.setModuleId(module.getModuleId());

				permissionEntity.setCreate(module.getCreate());
				permissionEntity.setView(module.getView());
				permissionEntity.setEdit(module.getEdit());
				permissionEntity.setDelete(module.getDelete());

				permissionEntity.setCreatedBy(permReq.getCreatedBy());
				permissionEntity.setCreatedDate(LocalDateTime.now());
			}

			permissionEntityList.add(permissionEntity);
		}

		permissionRepository.saveAll(permissionEntityList);
 
		log.info("RoleInfoServiceImpl::Exit from the the updateRolePermissions method");
		
		return ApiResponse.success(Constants.ROLE_PERMISSION_UPDATED_SUCCESSFULLY);
	}
}

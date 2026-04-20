package com.hms.service.serviceImpl;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.entity.PermissionEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.repository.PermissionRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.request.ModulePermissionRequest;
import com.hms.service.request.PermissionRequest;
import com.hms.service.request.RolesRequest;
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
	private SequenceGenerator sequenceGenerator;

	@Override
	public ApiResponse<?> addRole(@Valid RolesRequest request) {

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
		entity.setBusinessUnitId(request.getBusinessUnitId());
		entity.setDepartmentId(request.getDepartmentId());

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

		return ApiResponse.success(Constants.ROLE_ADDED_SUCCESSFULLY);
	}

}

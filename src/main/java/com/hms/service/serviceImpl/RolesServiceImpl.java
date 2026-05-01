package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.ModuleEntity;
import com.hms.service.entity.PermissionEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.BusinessUnitRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.ModuleRepository;
import com.hms.service.repository.PermissionRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.ModulePermissionRequest;
import com.hms.service.request.PermissionRequest;
import com.hms.service.request.RolesRequest;
import com.hms.service.request.UpdatePermissionRequest;
import com.hms.service.response.ModulePermissionResponse;
import com.hms.service.response.RolePermissionMatrixResponse;
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
	private BusinessUnitRepository businessUnitRepository;

	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private SequenceGenerator sequenceGenerator;

	@Autowired
	private AssignRolesRepository assignRolesRepository;

	

	@Autowired
	private UserRepository userRepository;

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
		entity.setCreatedDate(LocalDate.now());
		if (businessUnitRepository.existsById(request.getBusinessUnitId())) {
			entity.setBusinessUnitId(request.getBusinessUnitId());
		} else {
			log.info("BusinessUnit Id is required");
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(Constants.INVALID_BUSINESS_UNIT_ID));
		}
		if (departmentsRepository.existsById(request.getDepartmentId())) {
			entity.setDepartmentId(request.getDepartmentId());
		} else {
			log.info("Department Id is required");
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(Constants.INVALID_DEPARTMENT_ID));
		}

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
			permissionEntity.setExport(module.getExport());

			permissionEntity.setCreatedBy(permReq.getCreatedBy());
			permissionEntity.setCreatedDate(LocalDate.now());

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
		return ApiResponse.success(ResponseCode.SUCCESS, Constants.ROLE_PERMISSION_DETAILS_FETCHED_SUCCESSFULLY,
				rolesList);
	}

	@Override
	public ApiResponse<?> updateRolePermissions(@Valid UpdatePermissionRequest request) {

		log.info("RoleInfoServiceImpl::Inside the updateRolePermissions method");

		Integer roleId = request.getRoleId();
		if (roleId == null) {
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
				permissionEntity.setExport(module.getExport());

				permissionEntity.setUpdatedBy(permReq.getUpdatedBy());
				permissionEntity.setUpdatedDate(LocalDate.now());

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
				permissionEntity.setCreatedDate(LocalDate.now());
			}

			permissionEntityList.add(permissionEntity);
		}

		permissionRepository.saveAll(permissionEntityList);

		log.info("RoleInfoServiceImpl::Exit from the the updateRolePermissions method");

		return ApiResponse.success(Constants.ROLE_PERMISSION_UPDATED_SUCCESSFULLY);
	}

	@Override
	public ApiResponse<?> usersByRoleId(Integer roleId) {
		log.info("RoleInfoServiceImpl:: Inside the usersByRoleId");
		List<AssignRolesEntity> assignRolesEntity = assignRolesRepository.findByRoleId(roleId);

		if (assignRolesEntity == null || assignRolesEntity.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "No users are assigned for this role"

			);
		}

		List<Integer> userIds = assignRolesEntity.stream().map(AssignRolesEntity::getUserId).toList();
		System.out.println(userIds);
		List<UserEntity> users = userRepository.findByIdIn(userIds);

		List<Map<String, String>> userDetails = users.stream().map(user -> {
			Map<String, String> map = new HashMap<>();
			map.put("username", user.getUsername());
			map.put("email", user.getEmail());
			return map;
		}).toList();

		System.out.println(userDetails);

		log.info("RoleInfoServiceImpl::Exit from the usersByRoleId");

		return ApiResponse.success(ResponseCode.SUCCESS, "Usernames fetched successfully", userDetails);
	}

	@Override
	public ApiResponse<?> getRolePermissionMatrix() {

		log.info("RoleInfoServiceImpl::Inside getRolePermissionMatrix");

		List<RolesEntity> roles = roleInfoRepository.findAll();

		if (roles == null || roles.isEmpty()) {
			return ApiResponse.success(ResponseCode.SUCCESS, "No roles found", List.of());
		}

		List<RolePermissionMatrixResponse> response = roles.stream().map(role -> {

			List<AssignRolesEntity> assignedUsers = assignRolesRepository.findByRoleId(role.getRoleId());

			long userCount = 0;

			if (assignedUsers != null && !assignedUsers.isEmpty()) {
				userCount = assignedUsers.stream().map(AssignRolesEntity::getUserId).distinct().count();
			}

			return new RolePermissionMatrixResponse(role.getRoleId(), role.getRoleName(), role.getDescription(),
					userCount);

		}).toList();

		log.info("RoleInfoServiceImpl::Exit from getRolePermissionMatrix");

		return ApiResponse.success(ResponseCode.SUCCESS, "Role data fetched successfully", response);
	}
}

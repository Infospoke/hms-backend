
package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.RolesRequest;
import com.hms.service.request.UpdatePermissionRequest;
import com.hms.service.service.IRoleService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/hms/role")
public class RolesController {

	@Autowired
	private IRoleService iRoleService;

	@PostMapping("/add-role")
	public ResponseEntity<ApiResponse<?>> addRolePermissions(@Valid @RequestBody RolesRequest request) {
		ApiResponse<?> response = iRoleService.addRolePermissions(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/get-all-role-permissions")
	public ResponseEntity<ApiResponse<?>> getAllRolePermissions() {

		ApiResponse<?> response = iRoleService.getAllRolePermissions();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/usernames-by-roleid/{roleId}")
	public ResponseEntity<ApiResponse<?>> usersByRoleId(@PathVariable("roleId") Integer roleId){
		ApiResponse<?> response = iRoleService.usersByRoleId(roleId);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

	@PutMapping("/update-role-permissions")
	public ResponseEntity<ApiResponse<?>> updateRolePermissions(@Valid @RequestBody UpdatePermissionRequest request) {
		ApiResponse<?> response = iRoleService.updateRolePermissions(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

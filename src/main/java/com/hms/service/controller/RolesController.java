

	package com.hms.service.controller;


	import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.RolesRequest;
import com.hms.service.service.IRoleService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

	@RestController
	@RequestMapping("/hms/role")
	public class RolesController {

		@Autowired
		private IRoleService iRoleService;

		@PostMapping("/add-role")
		public ResponseEntity<ApiResponse<?>> addRole(@Valid@RequestBody RolesRequest request) {
			ApiResponse<?> response = iRoleService.addRole(request);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}

	
		
	}





 



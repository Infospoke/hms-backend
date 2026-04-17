package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.ModuleRequest;
import com.hms.service.service.IModuleService;
import com.hms.service.wrappers.ApiResponse;
@RestController
@RequestMapping("/hms/module")
public class ModuleController {
	
	@Autowired
	private IModuleService imoduleService;
	
	
	@PostMapping("/add-module")
	public ResponseEntity<ApiResponse<?>> addModule(@RequestBody ModuleRequest request) {
	    ApiResponse<?> response = imoduleService.addModule(request);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	


}

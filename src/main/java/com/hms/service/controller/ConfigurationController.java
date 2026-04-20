package com.hms.service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hms.service.response.UserDropDownResponse;
import com.hms.service.service.IConfigurationService;
import com.hms.service.wrappers.ApiResponse;

@RestController
@RequestMapping("/hms/configurations")
public class ConfigurationController {

	private final IConfigurationService iConfigurationService;

	public ConfigurationController(IConfigurationService iConfigurationService) {
		this.iConfigurationService = iConfigurationService;
	}

	@GetMapping("/business-units")
	public ResponseEntity<ApiResponse<List<UserDropDownResponse>>> getBusinessUnits() {

		ApiResponse<List<UserDropDownResponse>> response = iConfigurationService.getAllBusinessUnits();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/departments/{businessUnitId}")
	public ResponseEntity<ApiResponse<List<UserDropDownResponse>>> getDepartments(
			@PathVariable("businessUnitId") Integer businessUnitId) {

		ApiResponse<List<UserDropDownResponse>> response = iConfigurationService
				.getDepartmentsByBusinessUnit(businessUnitId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/roles/{departmentId}")
	public ResponseEntity<ApiResponse<List<UserDropDownResponse>>> getRoles(
			@PathVariable("departmentId") Integer departmentId) {
		ApiResponse<List<UserDropDownResponse>> response = iConfigurationService.getRolesByDepartment(departmentId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/employment-types")
	public ResponseEntity<ApiResponse<List<UserDropDownResponse>>> getEmploymentTypes() {

		ApiResponse<List<UserDropDownResponse>> response = iConfigurationService.getEmploymentTypes();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/user-types")
	public ResponseEntity<ApiResponse<List<UserDropDownResponse>>> getUserTypes() {

		ApiResponse<List<UserDropDownResponse>> response = iConfigurationService.getUserTypes();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get-all-modules")
	public ResponseEntity<ApiResponse<?>> getAllModules() {

		ApiResponse<?> response = iConfigurationService.getAllModules();

		return ResponseEntity.ok(response);

	}

	@GetMapping("/position-basic-seniority-levels")
	public ResponseEntity<ApiResponse<List<UserDropDownResponse>>> getSeniorityLevels() {
		ApiResponse<List<UserDropDownResponse>> response = iConfigurationService.getSeniorityLevels();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/travel-requirements")
	public ResponseEntity<ApiResponse<List<UserDropDownResponse>>> getTravelRequirements() {

		ApiResponse<List<UserDropDownResponse>> response = iConfigurationService.getTravelRequirements();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
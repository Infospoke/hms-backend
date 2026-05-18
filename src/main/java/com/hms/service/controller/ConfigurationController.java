package com.hms.service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.response.DropDownResponse;
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
	public ResponseEntity<ApiResponse<List<DropDownResponse>>> getBusinessUnits() {

		ApiResponse<List<DropDownResponse>> response = iConfigurationService.getAllBusinessUnits();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/departments")
	public ResponseEntity<ApiResponse<List<DropDownResponse>>> getDepartments() {

		ApiResponse<List<DropDownResponse>> response = iConfigurationService.getAllDepartments();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/departments/{businessUnitId}")
	public ResponseEntity<ApiResponse<List<DropDownResponse>>> getDepartments(
			@PathVariable("businessUnitId") Integer businessUnitId) {

		ApiResponse<List<DropDownResponse>> response = iConfigurationService
				.getDepartmentsByBusinessUnit(businessUnitId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/roles/{departmentId}")
	public ResponseEntity<ApiResponse<List<DropDownResponse>>> getRoles(
			@PathVariable("departmentId") Integer departmentId) {
		ApiResponse<List<DropDownResponse>> response = iConfigurationService.getRolesByDepartment(departmentId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/employment-types")
	public ResponseEntity<ApiResponse<List<DropDownResponse>>> getEmploymentTypes() {

		ApiResponse<List<DropDownResponse>> response = iConfigurationService.getEmploymentTypes();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/user-types")
	public ResponseEntity<ApiResponse<List<DropDownResponse>>> getUserTypes() {

		ApiResponse<List<DropDownResponse>> response = iConfigurationService.getUserTypes();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get-all-modules")
	public ResponseEntity<ApiResponse<?>> getAllModules() {

		ApiResponse<?> response = iConfigurationService.getAllModules();

		return ResponseEntity.ok(response);

	}

	@GetMapping("/get-all-functionalities")
	public ResponseEntity<ApiResponse<?>> getAllFunctionalities() {

		ApiResponse<?> response = iConfigurationService.getAllFunctionalities();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/position-basic-seniority-levels")
	public ResponseEntity<ApiResponse<List<DropDownResponse>>> getSeniorityLevels() {
		ApiResponse<List<DropDownResponse>> response = iConfigurationService.getSeniorityLevels();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/travel-requirements")
	public ResponseEntity<ApiResponse<List<DropDownResponse>>> getTravelRequirements() {

		ApiResponse<List<DropDownResponse>> response = iConfigurationService.getTravelRequirements();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get-jr")
	public ResponseEntity<ApiResponse<?>> getJr() {
		ApiResponse<?> response = iConfigurationService.getJr();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@GetMapping("/functionality")
	public ResponseEntity<ApiResponse<List<DropDownResponse>>> getFunctionality() {

		ApiResponse<List<DropDownResponse>> response = iConfigurationService.getAllFunctionality();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/role-users")
	public ResponseEntity<ApiResponse<List<DropDownResponse>>> getUsersByRole() {
		ApiResponse<List<DropDownResponse>> response = iConfigurationService.getUsersByRole();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
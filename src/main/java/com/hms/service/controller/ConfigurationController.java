package com.hms.service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.RolesByDepartmentIdsRequest;
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
	public ResponseEntity<ApiResponse<List<?>>> getBusinessUnits() {

		ApiResponse<List<?>> response = iConfigurationService.getAllBusinessUnits();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/departments")
	public ResponseEntity<ApiResponse<List<?>>> getDepartments() {

		ApiResponse<List<?>> response = iConfigurationService.getAllDepartments();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/departments/{businessUnitId}")
	public ResponseEntity<ApiResponse<List<?>>> getDepartments(@PathVariable("businessUnitId") Integer businessUnitId) {

		ApiResponse<List<?>> response = iConfigurationService
				.getDepartmentsByBusinessUnit(businessUnitId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/roles/{departmentId}")
	public ResponseEntity<ApiResponse<List<?>>> getRoles(@PathVariable("departmentId") Integer departmentId) {
		ApiResponse<List<?>> response = iConfigurationService.getRolesByDepartment(departmentId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/employment-types")
	public ResponseEntity<ApiResponse<List<?>>> getEmploymentTypes() {

		ApiResponse<List<?>> response = iConfigurationService.getEmploymentTypes();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/user-types")
	public ResponseEntity<ApiResponse<List<?>>> getUserTypes() {

		ApiResponse<List<?>> response = iConfigurationService.getUserTypes();

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
	public ResponseEntity<ApiResponse<List<?>>> getSeniorityLevels() {
		ApiResponse<List<?>> response = iConfigurationService.getSeniorityLevels();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/travel-requirements")
	public ResponseEntity<ApiResponse<List<?>>> getTravelRequirements() {

		ApiResponse<List<?>> response = iConfigurationService.getTravelRequirements();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get-jr")
	public ResponseEntity<ApiResponse<?>> getJr() {
		ApiResponse<?> response = iConfigurationService.getJr();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@GetMapping("/functionality")
	public ResponseEntity<ApiResponse<List<?>>> getFunctionality() {

		ApiResponse<List<?>> response = iConfigurationService.getAllFunctionality();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/role-users")
	public ResponseEntity<ApiResponse<List<?>>> getUsersByRole() {
		ApiResponse<List<?>> response = iConfigurationService.getUsersByRole();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get-ai-options")
	public ResponseEntity<ApiResponse<List<?>>> getAiOtions() {
		ApiResponse<List<?>> response = iConfigurationService.getAiOptions();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/roles/by-departments")
	public ResponseEntity<ApiResponse<List<?>>> getRolesByDepartments(
			@RequestBody RolesByDepartmentIdsRequest request) {

		ApiResponse<List<?>> response = iConfigurationService.getRolesByDepartments(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/create-permission-users")
	public ResponseEntity<ApiResponse<List<?>>> getUsersWithCreatePermission() {

		ApiResponse<List<?>> response = iConfigurationService.getUsersWithCreatePermission();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/interview-plans")
	public ResponseEntity<ApiResponse<List<?>>> getInterviewPlans() {

		ApiResponse<List<?>> response = iConfigurationService.getInterviewPlans();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/jobs")
	public ResponseEntity<ApiResponse<List<?>>> getJobs() {

		ApiResponse<List<?>> response = iConfigurationService.getJobs();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}



}
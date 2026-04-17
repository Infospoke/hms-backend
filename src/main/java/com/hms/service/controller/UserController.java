package com.hms.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.enums.UserStatus;
import com.hms.service.request.UserCreationRequest;
import com.hms.service.request.UserStatusRequest;
import com.hms.service.response.UserResponse;
import com.hms.service.service.IUserService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/hms/user")

public class UserController {

	@Autowired
	private IUserService iUserService;

	@PostMapping("/create")
	public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody UserCreationRequest request) {
		ApiResponse<?> response = iUserService.createUser(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/list")
	public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers(@RequestParam(name = "page") int page,@RequestParam(name = "size") int size) {
		ApiResponse<List<UserResponse>> response = iUserService.getAllUsers(page, size);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/status")
	public ResponseEntity<ApiResponse<String>> updateStatus(@RequestBody UserStatusRequest request) {
		ApiResponse<String> response = iUserService.updateUserStatus(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/count/total")
	public ResponseEntity<ApiResponse<Long>> getTotal() {
		ApiResponse<Long> response = iUserService.getTotalUsers();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/count/role/{roleId}")
	public ResponseEntity<ApiResponse<Long>> getByRole(@PathVariable("roleId") Integer roleId) {
		ApiResponse<Long> response = iUserService.getUsersByRole(roleId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	 @GetMapping("/count/status")
	    public ResponseEntity<ApiResponse<Long>> getByStatus(
	            @RequestParam("status") UserStatus status) {

	        ApiResponse<Long> response = iUserService.getUsersByStatus(status);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }

}

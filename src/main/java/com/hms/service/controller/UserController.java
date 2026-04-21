package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hms.service.request.UpdateUserRequest;
import com.hms.service.request.UserCreationRequest;
import com.hms.service.request.UserFilterRequest;
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

	@PostMapping("/list")
    public ApiResponse<?> getUsers(@RequestBody UserFilterRequest request) {
        return iUserService.getUsers(request);
    }

	 
	 @PutMapping("/update/{id}")
	 public ResponseEntity<ApiResponse<String>> updateUser(
			 @PathVariable("id") Integer id,
	         @RequestBody UpdateUserRequest request) {
		 
		 ApiResponse<String> response = iUserService.updateUser(id, request);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	 
	 }
	 
}

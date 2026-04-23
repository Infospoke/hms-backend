package com.hms.service.service;

import com.hms.service.request.LoginRequest;
import com.hms.service.request.ResetPasswordRequest;
import com.hms.service.request.UpdateUserRequest;
import com.hms.service.request.UserCreationRequest;
import com.hms.service.request.UserFilterRequest;
import com.hms.service.response.LoginResponse;

import com.hms.service.wrappers.ApiResponse;


public interface IUserService {
    
    ApiResponse<?> createUser(UserCreationRequest request);

    ApiResponse<String> updateUser(Integer id, UpdateUserRequest request);

	ApiResponse<LoginResponse> login(LoginRequest request, String channel);

	boolean validateToken(String token);

	ApiResponse<?> getUsers(UserFilterRequest request);

	ApiResponse<?> resetPassword(ResetPasswordRequest request, String channel);
	
    
}

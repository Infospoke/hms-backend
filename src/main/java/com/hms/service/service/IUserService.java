package com.hms.service.service;

import com.hms.service.request.FilterRequest;
import com.hms.service.request.LoginRequest;
import com.hms.service.request.ResetPasswordRequest;
import com.hms.service.request.UpdateUserRequest;
import com.hms.service.request.UserCreationRequest;
import com.hms.service.response.LoginResponse;
import com.hms.service.response.UserUpdationResponse;
import com.hms.service.wrappers.ApiResponse;


public interface IUserService {
    
    ApiResponse<?> createUser(UserCreationRequest request);

	ApiResponse<LoginResponse> login(LoginRequest request, String channel);

	boolean validateToken(String token);

	ApiResponse<?> getUserCounts();

	ApiResponse<?> getUsersList(FilterRequest request);

	ApiResponse<?> updateUser(Integer id, UpdateUserRequest request);

	ApiResponse<UserUpdationResponse> getUserById(Integer id);

	ApiResponse<?> forgotPassword(String email);

	
    
}

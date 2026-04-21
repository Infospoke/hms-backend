package com.hms.service.service;

import java.util.List;

import com.hms.service.enums.UserStatus;
import com.hms.service.request.LoginRequest;
import com.hms.service.request.UpdateUserRequest;
import com.hms.service.request.UserCreationRequest;
import com.hms.service.response.LoginResponse;
import com.hms.service.response.UserResponse;
import com.hms.service.wrappers.ApiResponse;


public interface IUserService {
    
    ApiResponse<?> createUser(UserCreationRequest request);

    ApiResponse<Long> getTotalUsers();

    ApiResponse<Long> getUsersByRole(Integer roleId);

	ApiResponse<List<UserResponse>> getAllUsers(int page, int size);

	ApiResponse<Long> getUsersByStatus(UserStatus status);

	ApiResponse<String> updateUser(Integer id, UpdateUserRequest request);

	ApiResponse<LoginResponse> login(LoginRequest request, String channel);

	boolean validateToken(String token);
    
}

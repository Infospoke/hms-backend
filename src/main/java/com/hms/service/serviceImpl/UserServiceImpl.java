package com.hms.service.serviceImpl;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.entity.UserEntity;
import com.hms.service.enums.UserStatus;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.UserCreationRequest;
import com.hms.service.request.UserStatusRequest;
import com.hms.service.response.UserResponse;
import com.hms.service.service.IUserService;
import com.hms.service.utils.PasswordGenerator;
import com.hms.service.utils.SequenceGenerator;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SequenceGenerator sequenceGenerator;
	
	@Override
	public ApiResponse<?> createUser(UserCreationRequest request) {

		log.info("UserServiceImpl::Inside the createUser method");
		 if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
		        return ApiResponse.failure(ResponseCode.FAILURE, Constants.EMPLOYEE_ID_ALREADY_EXISTS);
		    }

		    if (userRepository.existsByEmail(request.getEmail())) {
		        return ApiResponse.failure(ResponseCode.FAILURE, Constants.EMAIL_ALREADY_EXISTS);
		    }

		
		    LocalDate dob;
		    try {
		        dob = LocalDate.parse(request.getDateOfBirth());
		    } catch (Exception e) {
		        return ApiResponse.failure(ResponseCode.FAILURE,Constants.INVALID_DOB_FORMAT);
		    }

		    if (dob.isAfter(LocalDate.now().minusYears(18))) {
		        return ApiResponse.failure(ResponseCode.FAILURE, Constants.USER_AGE_MUST_BE_ABOVE_18);
		    }

		    if (request.getAlternateContact() != null &&
		    		request.getAlternateContact().equals(request.getMobileNumber())) {

		        return ApiResponse.failure(ResponseCode.FAILURE, Constants.ALTERNATIVE_NUMBER_MUST_BE_DIFFERENT);
		    }


	   
	    String rawPassword = PasswordGenerator.generatePassword(Constants.PASSWORD_LENGTH);
	    String rawPin = PasswordGenerator.generatePin(Constants.PIN_LENGTH);

	    String encryptedPassword = passwordEncoder.encode(rawPassword);
	    String encryptedPin = passwordEncoder.encode(rawPin);

	    UserEntity user = new UserEntity();

	    user.setUserTypeId(request.getUserTypeId());
	    user.setFirstName(request.getFirstName());
	    user.setLastName(request.getLastName());
	    user.setEmail(request.getEmail());
	    user.setEmployeeId(request.getEmployeeId());
	    user.setMobileNumber(request.getMobileNumber());
	    user.setAlternateContact(request.getAlternateContact());

	    user.setDateOfBirth(LocalDate.parse(request.getDateOfBirth())); 
	    user.setEmploymentTypeId(request.getEmploymentTypeId());
	    user.setBusinessUnitId(request.getBusinessUnitId());
	    user.setDepartmentId(request.getDepartmentId());
	    user.setRoleId(request.getRoleId());
	    
	    user.setPassword(encryptedPassword);
	    user.setPin(encryptedPin);

	    user.setStatus(UserStatus.INVITE_PENDING);

	    user.setCreatedBy("ADMIN");
	    user.setCreatedAt(LocalDateTime.now());

	    user.setUserId(sequenceGenerator.generateUserId());

	    userRepository.save(user);

	    System.out.println("Generated Password: " + rawPassword);
	    System.out.println("Generated PIN: " + rawPin);
	    log.info("UserServiceImpl::Exit from the createUser method");
	    return ApiResponse.success(Constants.USER_CREATED_SUCCESSFULLY);
	
	}
	
	
	@Override
	public ApiResponse<List<UserResponse>> getAllUsers(int page, int size) {
		log.info("UserServiceImpl::Inside the getAllUsers method");
	    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

	    Page<UserEntity> users = userRepository.findAll(pageable);

	    List<UserResponse> list = users.getContent().stream()
	            .map(u -> new UserResponse(
	                    u.getId(),
	                    u.getFirstName() + " " + u.getLastName(),
	                    u.getEmail(),
	                    u.getRoleId(),
	                    u.getStatus()
	            ))
	            .toList();

		log.info("UserServiceImpl::Exit from the getAllUsers method");
	    return ApiResponse.success(Constants.USER_FETCHED, list, (int) users.getTotalElements());
	}
	
	
	 @Override
	    public ApiResponse<String> updateUserStatus(UserStatusRequest request) {

		 log.info("UserServiceImpl::Inside the updateUserStatus method"); 
			UserEntity user = userRepository.findById(request.getId())
	                .orElseThrow(() -> new RuntimeException(Constants.USER_NOT_FOUND));

	        user.setStatus(request.getStatus());
	        user.setUpdatedAt(LocalDateTime.now());
	        user.setUpdatedBy(request.getUpdatedBy());
	        userRepository.save(user);
	        log.info("UserServiceImpl::Exit from the updateUserStatus method"); 
	        return ApiResponse.success(Constants.STATUS_UPDATED_SUCCESSFULLY);
	    }

	 @Override
	    public ApiResponse<Long> getTotalUsers() {
	        return ApiResponse.success(ResponseCode.SUCCESS, userRepository.count());
	    }
	 
	 
	 @Override
	    public ApiResponse<Long> getUsersByRole(Integer roleId) {
	        return ApiResponse.success(ResponseCode.SUCCESS, userRepository.countByRoleId(roleId));
	    }
	 
	 
	@Override
	public ApiResponse<Long> getUsersByStatus(UserStatus status) {
		return ApiResponse.success(ResponseCode.SUCCESS, userRepository.countByStatus(status));
	}
	
	
	
	 
}
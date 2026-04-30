package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.ModuleEntity;
import com.hms.service.entity.PasswordHistoryEntity;
import com.hms.service.entity.PermissionEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.enums.ChannelTypes;
import com.hms.service.enums.CredentialType;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.BusinessUnitRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.ModuleRepository;
import com.hms.service.repository.PasswordHistoryRepository;
import com.hms.service.repository.PermissionRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.ChangePasswordRequest;
import com.hms.service.request.FilterRequest;
import com.hms.service.request.LoginRequest;
import com.hms.service.request.UpdateUserRequest;
import com.hms.service.request.UserCreationRequest;
import com.hms.service.response.LoginResponse;
import com.hms.service.response.UserResponse;
import com.hms.service.response.UserUpdationResponse;
import com.hms.service.service.IUserService;
import com.hms.service.utils.JwtService;
import com.hms.service.utils.PasswordGenerator;
import com.hms.service.utils.SequenceGenerator;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RolesRepository rolesRepository;

	@Autowired
	private ModuleRepository moduleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private SequenceGenerator sequenceGenerator;

	@Autowired
	private AssignRolesRepository assignRolesRepository;

	@Autowired
	private BusinessUnitRepository businessUnitRepository;

	@Autowired
	private PasswordHistoryRepository passwordHistoryRepository;

	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private MailServiceImpl mailService;

	@Autowired
	private JwtService jwtService;

	@Value("${spring.mail.username}")
	private String fromEmail;

	private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

	@Override
	@Transactional
	public ApiResponse<?> createUser(UserCreationRequest request) {

		log.info("UserServiceImpl:: Inside the createUser method");

		if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.EMPLOYEE_ID_ALREADY_EXISTS);
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.EMAIL_ALREADY_EXISTS);
		}

		if (request.getAlternateContact() != null 
		        && !request.getAlternateContact().isEmpty()
		        && request.getAlternateContact().equals(request.getMobileNumber())) {

		    return ApiResponse.failure(ResponseCode.FAILURE, 
		            Constants.ALTERNATIVE_NUMBER_MUST_BE_DIFFERENT);
		}
		
		String rawPassword = PasswordGenerator.generatePassword(8);
		String rawPin = PasswordGenerator.generatePin(4);

		log.info("UserServiceImpl:: User login credentials generated");

		UserEntity user = new UserEntity();

		user.setUserTypeId(request.getUserTypeId());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setEmail(request.getEmail());
		user.setEmployeeId(request.getEmployeeId());
		user.setMobileNumber(request.getMobileNumber());
		user.setAlternateContact(request.getAlternateContact());
		String userName = (request.getFirstName() + " " + request.getLastName());
		user.setUsername(userName);
		user.setFirstTimeLogin(true);
		user.setEmploymentTypeId(request.getEmploymentTypeId());
		
		
		if (!businessUnitRepository.existsById(request.getBusinessUnitId())) {
		    return ApiResponse.failure(
		            ResponseCode.FAILURE,
		            "Failure",
		            List.of(Constants.INVALID_BUSINESS_UNIT_ID)
		    );
		}


	
		if (!departmentsRepository.existsById(request.getDepartmentId())) {
		    return ApiResponse.failure(
		            ResponseCode.FAILURE,
		            "Failure",
		            List.of(Constants.INVALID_DEPARTMENT_ID)
		    );
		}


		if (!rolesRepository.existsById(request.getRoleId())) {
		    return ApiResponse.failure(
		            ResponseCode.FAILURE,
		            "Failure",
		            List.of(Constants.INVALID_ROLE_ID)
		    );
		}


		
		if (!departmentsRepository.existsByIdAndBusinessUnitId(
		        request.getDepartmentId(),
		        request.getBusinessUnitId())) {

		    return ApiResponse.failure(
		            ResponseCode.FAILURE,
		            "Failure",
		            List.of(Constants.INVALID_DEPARTMENT_FOR_BUSINESS_UNIT)
		    );
		}


		if (!rolesRepository.existsByRoleIdAndDepartmentId(
		        request.getRoleId(),
		        request.getDepartmentId())) {

		    return ApiResponse.failure(
		            ResponseCode.FAILURE,
		            "Failure",
		            List.of(Constants.ROLE_NOT_BELONG_TO_DEPARTMENT)
		    );
		}
		
		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setPin(passwordEncoder.encode(rawPin));

		user.setActive(true);
		user.setDeactivated(false);
		user.setUpdatedBy("ADMIN");
		user.setUpdatedAt(LocalDate.now());

		Integer userId = sequenceGenerator.generateUserId();
		user.setUserId(userId);

		userRepository.save(user);

		log.info("UserServiceImpl:: User saved successfully");

		AssignRolesEntity role = new AssignRolesEntity();
		role.setAssignRoleId(sequenceGenerator.generateAssignRoleId());
		role.setUserId(userId);
		role.setRoleId(request.getRoleId());
		role.setAssignedBy("ADMIN");
		role.setAssignedAt(LocalDate.now());

		assignRolesRepository.save(role);

		log.info("UserServiceImpl:: Role assigned to user successfully");

		savePasswordHistory(userId, rawPassword, CredentialType.PASSWORD);
		savePasswordHistory(userId, rawPin, CredentialType.PIN);

		log.info("UserServiceImpl::Password history saved");

		try {
			log.info("UserServiceImpl::Sending mail to user");

			String subject = Constants.USER_CREATED_MAIL_SUBJECT;

			String body = String.format(Constants.USER_CREATED_MAIL_BODY, request.getFirstName(), request.getEmail(),
					rawPassword, rawPin);

			mailService.sendMail(fromEmail, request.getEmail(), null, subject, body, null);

			log.info("UserServiceImpl::Mail sent");

		} catch (Exception e) {
			log.error("UserServiceImpl::Mail failed"+e.getMessage());
		}

		Map<String, Object> data = new HashMap<>();
		data.put("userId", userId);
		data.put("username", userName);

		log.info("UserServiceImpl:: Exit from the createUser method");

		return ApiResponse.success(ResponseCode.SUCCESS, Constants.SUCCESS, data);

	}

	@Override
	public ApiResponse<?> getUserCounts() {

		log.info("UserServiceImpl:: Inside the getUserCounts method");

		Long total = userRepository.getTotalUsers();
		Long active = userRepository.getActiveUsers();
		Long deactivated = userRepository.getDeactivatedUsers();

		List<Object[]> result = userRepository.getUserCountByRole();

		List<Map<String, Object>> roleCounts = result.stream().map(obj -> {
			Map<String, Object> map = new HashMap<>();
			map.put("roleId", obj[0]);
			map.put("roleName", obj[1]);
			map.put("count", obj[2]);
			return map;
		}).toList();

		Map<String, Object> response = new HashMap<>();
		response.put("total", total);
		response.put("active", active);
		response.put("deactivated", deactivated);
		response.put("roleCounts", roleCounts);

		log.info("UserServiceImpl:: Exit from the getUserCounts method");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	public ApiResponse<?> getUsersList(FilterRequest request) {

		log.info("UserServiceImpl:: Inside the getUsersList method");

		if (request.getPage() == null || request.getSize() == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("page and size must be provided"));
		}

		if (request.getPage() < 0 || request.getSize() <= 0) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Invalid page or size values"));
		}

		Sort sort = Sort.by("DESC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
				request.getSortBy() != null ? request.getSortBy() : "userId");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Integer roleId = null;

		if (request.getFilters() != null && request.getFilters().containsKey("roleId")) {
			try {
				roleId = Integer.parseInt(request.getFilters().get("roleId").toString());
			} catch (Exception e) {
				return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("roleId must be a valid number"));
			}
		}

		log.info("Fetching users for roleId: {}", roleId);

		Page<UserResponse> pageResult = userRepository.findUsersByRole(roleId, pageable);

		Map<String, Object> response = new HashMap<>();
		response.put("users", pageResult.getContent());
		response.put("currentPage", pageResult.getNumber());
		response.put("totalPages", pageResult.getTotalPages());
		response.put("totalElements", pageResult.getTotalElements());

		log.info("UserServiceImpl:: Exit from the getUsersList method");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	@Transactional
	public ApiResponse<?> updateUser(Integer id, UpdateUserRequest request) {

		log.info("UserServiceImpl:: Inside the updateUser method - Started for userId: {}", id);

		UserEntity user = userRepository.findByUserId(id).orElseThrow(() -> new RuntimeException("User not found"));

		AssignRolesEntity roleEntity = assignRolesRepository.findByUserId(user.getUserId())
				.orElseThrow(() -> new RuntimeException("Role mapping not found"));

		if (request.getRoleId() != null && !request.getRoleId().equals(roleEntity.getRoleId())) {

			log.info("Role change detected for userId: {}", id);

			roleEntity.setRoleId(request.getRoleId());
			roleEntity.setAssignedBy("ADMIN");
			roleEntity.setAssignedAt(LocalDate.now());

			assignRolesRepository.save(roleEntity);

			log.info("UserServiceImpl::Role updated successfully");
		}

		if (request.getBusinessUnitId() != null) {
			user.setBusinessUnitId(request.getBusinessUnitId());
			log.info("UserServiceImpl::Business Unit updated");
		}

		if (request.getDepartmentId() != null) {
			user.setDepartmentId(request.getDepartmentId());
			log.info("UserServiceImpl::Department updated");
		}

		if (Boolean.TRUE.equals(request.getDeactivate())) {

			user.setActive(false);
			user.setDeactivated(true);

			user.setUpdatedBy("ADMIN");
			user.setUpdatedAt(LocalDate.now());

			userRepository.save(user);

			log.info("UserServiceImpl::User deactivated successfully");

			return ApiResponse.success(ResponseCode.SUCCESS, "User deactivated successfully", null);
		}

		user.setUpdatedBy("ADMIN");
		user.setUpdatedAt(LocalDate.now());

		userRepository.save(user);

		log.info("UserServiceImpl::User updated successfully");

		return ApiResponse.success(ResponseCode.SUCCESS, "User updated successfully", null);
	}

//	@Override
//	public ApiResponse<UserUpdationResponse> getUserById(Integer id) {
//
//		log.info("UserServiceImpl::Inside the getUserById method- Started for userId: {}", id);
//
//		UserEntity user = userRepository.findByUserId(id).orElseThrow(() -> new RuntimeException("User not found"));
//
//		AssignRolesEntity roleEntity = assignRolesRepository.findByUserId(id)
//				.orElseThrow(() -> new RuntimeException("Role mapping not found"));
//
//		RolesEntity role = rolesRepository.findById(roleEntity.getRoleId())
//				.orElseThrow(() -> new RuntimeException("Role not found"));
//
//		UserUpdationResponse response = new UserUpdationResponse(user.getUsername(), user.getEmail(), user.getActive(),
//				role.getRoleName(), roleEntity.getAssignedBy(), roleEntity.getAssignedAt());
//		
//		log.info("UserServiceImpl::Exit from the getUserById method- Started for userId: {}", id);
//		return ApiResponse.success(ResponseCode.SUCCESS, "User details fetched successfully", response);
//	}
	
	@Override
	public ApiResponse<UserUpdationResponse> getUserById(Integer id) {

	    log.info("UserServiceImpl::Inside getUserById - Started for userId: {}", id);

	    UserEntity user = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    AssignRolesEntity roleEntity = assignRolesRepository.findByUserId(user.getUserId())
	            .orElseThrow(() -> new RuntimeException("Role mapping not found"));

	    RolesEntity role = rolesRepository.findById(roleEntity.getRoleId())
	            .orElseThrow(() -> new RuntimeException("Role not found"));

	    UserUpdationResponse response = new UserUpdationResponse();

	   
	    BeanUtils.copyProperties(user, response);

	    response.setRoleId(roleEntity.getRoleId());
	    response.setRoleName(role.getRoleName());
	    response.setAssignedBy(roleEntity.getAssignedBy());
	    response.setAssignedAt(roleEntity.getAssignedAt());

	    log.info("UserServiceImpl::Exit getUserById - Completed for userId: {}", id);

	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            "User details fetched successfully",
	            response
	    );
	}

	@Override
	public ApiResponse<LoginResponse> login(LoginRequest request, String channel) {

		try {
			LOGGER.info("UserManagement::UserServiceImpl::Inside the login method");

			ApiResponse<LoginResponse> validationResponse = validateLogin(request, channel);
			if (validationResponse != null)
				return validationResponse;

			Optional<UserEntity> optionalUser = userRepository.findByEmailAndActiveTrue(request.getEmail());
			if (optionalUser.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "User is deactivated");
			}

			UserEntity user = optionalUser.get();

			log.info("User fetched");

			if (Boolean.TRUE.equals(user.getAccountLocked())) {

				log.info("Account is locked");

				if (Boolean.TRUE.equals(user.getForcePasswordReset())) {
					return ApiResponse.failure(ResponseCode.FAILURE, "Please reset your password");
				}

				if (user.getLockTime() != null && user.getLockTime().plusMinutes(2).isAfter(LocalDateTime.now())) {
					return ApiResponse.failure(ResponseCode.FAILURE, "Account is locked. Try after 2 minutes");
				} else {
					log.info("Unlocking account");
					user.setAccountLocked(false);
					user.setLockTime(null);
					userRepository.save(user);
				}
			}

			if (user.getPasswordUpdatedAt() != null
					&& user.getPasswordUpdatedAt().plusMonths(3).isBefore(LocalDateTime.now())) {

				log.info("Password expired");

				user.setForcePasswordReset(true);
				userRepository.save(user);

				return ApiResponse.failure(ResponseCode.FAILURE, "Password expired. Please reset your password");
			}

			boolean isCredentialsValid;

			if (ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)) {
				log.info("Validating password");
				isCredentialsValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
			} else {
				log.info("Validating pin");
				isCredentialsValid = passwordEncoder.matches(request.getPin(), user.getPin());
			}

			if (!isCredentialsValid) {

				int attempts = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
				attempts++;
				user.setFailedAttempts(attempts);

				log.info("Invalid credentials attempt: {}", attempts);

				if (attempts == 3) {
					user.setAccountLocked(true);
					user.setLockTime(LocalDateTime.now());
					userRepository.save(user);

					return ApiResponse.failure(ResponseCode.FAILURE, "Account locked for 2 minutes");
				}

				if (attempts > 3) {
					user.setAccountLocked(true);
					user.setForcePasswordReset(true);
					userRepository.save(user);

					return ApiResponse.failure(ResponseCode.FAILURE, "Too many attempts. Please reset password");
				}

				userRepository.save(user);
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid credentials");
			}

			log.info("Login successful");

			user.setFailedAttempts(0);
			user.setAccountLocked(false);
			user.setLockTime(null);
			userRepository.save(user);

			Optional<AssignRolesEntity> assignRoleOpt = assignRolesRepository.findByUserId(user.getUserId());
			if (assignRoleOpt.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "No role assigned to this user");
			}

			AssignRolesEntity assignRole = assignRoleOpt.get();

			Optional<RolesEntity> roleOpt = rolesRepository.findByRoleId(assignRole.getRoleId());
			if (roleOpt.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Assigned role not found");
			}

			RolesEntity role = roleOpt.get();

			List<PermissionEntity> permissions = permissionRepository.findByRoleId(assignRole.getRoleId());

			if (permissions == null || permissions.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "No permissions configured for the assigned role");
			}

			Map<Integer, String> moduleMap = moduleRepository.findAll().stream()
					.collect(Collectors.toMap(ModuleEntity::getModuleId, ModuleEntity::getModuleName));

			List<String> permissionsList = new ArrayList<>();

			for (PermissionEntity p : permissions) {

				String moduleName = moduleMap.get(p.getModuleId()).toUpperCase().replace(" ", "_");

				if (Boolean.TRUE.equals(p.getCreate())) {
					permissionsList.add(moduleName + "_CREATE");
				}
				if (Boolean.TRUE.equals(p.getView())) {
					permissionsList.add(moduleName + "_VIEW");
				}
				if (Boolean.TRUE.equals(p.getEdit())) {
					permissionsList.add(moduleName + "_EDIT");
				}
				if (Boolean.TRUE.equals(p.getDelete())) {
					permissionsList.add(moduleName + "_DELETE");
				}
			}

			log.info("Generating token");

			String token = jwtService.generateToken(user.getEmail(), user.getUsername(), role.getRoleName(),
					permissionsList, user.getFirstTimeLogin());

			LoginResponse response = new LoginResponse();
			response.setToken(token);

			return ApiResponse.success(ResponseCode.SUCCESS, "Login Successfull", response);

		} catch (Exception e) {
			log.error("Login failed: {}", e.getMessage());
			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}

	private ApiResponse<LoginResponse> validateLogin(LoginRequest request, String channel) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside the validateLogin method");

		if (request == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid request");
		}

		if (request.getEmail() == null || request.getEmail().isBlank()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Email is required");
		}

		if (channel == null || channel.isBlank()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Channel is required");
		}

		if (!ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)
				&& !ChannelTypes.MOBILE.getChannelName().equalsIgnoreCase(channel)) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid channel");
		}

		if (ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)) {

			if (request.getPassword() == null || request.getPassword().isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Password is required");
			}

		} else {

			if (request.getPin() == null || request.getPin().isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Pin is required");
			}
		}

		return null;
	}

	@Transactional
	@Override
	public ApiResponse<?> forgotPassword(String email ,String channel) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside the forgotPassword method");

		Optional<UserEntity> optionalUser = userRepository.findByEmailAndActiveTrue(email);

		if (optionalUser.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "User is deactivated");
		}

		UserEntity user = optionalUser.get();
		 String rawPassword = null;
		 String rawPin = null;
		if (ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)){
			rawPassword = PasswordGenerator.generatePassword(8);
			String encodedPassword = passwordEncoder.encode(rawPassword);
			savePasswordHistory(user.getUserId(), rawPassword, CredentialType.PASSWORD);
			user.setPassword(encodedPassword);
			user.setPasswordUpdatedAt(LocalDateTime.now());
			user.setFirstTimeLogin(true);
			
		}
		else {
			rawPin = PasswordGenerator.generatePin(4);
			String encodedPin = passwordEncoder.encode(rawPin);
			savePasswordHistory(user.getUserId(), rawPin, CredentialType.PIN);
			user.setPin(encodedPin);
			user.setPinUpdatedAt(LocalDateTime.now());
			user.setFirstTimeLogin(true);
		}
     	user.setFailedAttempts(0);
		user.setAccountLocked(false);
		user.setLockTime(null);
		user.setForcePasswordReset(false);

		userRepository.save(user);

		try {
			  if (rawPassword != null) {
			        sendForgotPasswordMail(user, rawPassword);
			    } else  {
			        sendForgotPinMail(user, rawPin);
		} 
		}catch (Exception e) {
			log.error("Mail failed: {}", e.getMessage());
			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to send email");
		}

		log.info("Forgot password completed");

		return ApiResponse.success(ResponseCode.SUCCESS, "New credentials sent to registered email", null);
	}

	private void sendForgotPasswordMail(UserEntity user, String password) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside the sendForgotPasswordMail method");

		String subject = Constants.FORGOT_PASSWORD_SUBJECT;

		String body = String.format(Constants.FORGOT_PASSWORD_BODY, user.getFirstName(), user.getEmail(), password);

		mailService.sendMail(fromEmail, user.getEmail(), null, subject, body, null);
	}
	
	private void sendForgotPinMail(UserEntity user,String pin) {
		LOGGER.info("UserManagement::UserServiceImpl::Inside the sendForgotPinMail method");
		String subject = Constants.FORGOT_PASSWORD_SUBJECT;
		String body = String.format(Constants.FORGOT_PIN_BODY, user.getFirstName(), user.getEmail(), pin);

		mailService.sendMail(fromEmail, user.getEmail(), null, subject, body, null);

		
	}

	@Override
	public ApiResponse<?> changePassword(ChangePasswordRequest request, String channel) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside the changePassword method");

		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Optional<UserEntity> optionalUser = userRepository.findByEmailAndActiveTrue(email);

		if (optionalUser.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "User is deactivated");
		}

		UserEntity user = optionalUser.get();

		log.info("User fetched");

		if (ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)) {

			log.info("Validating old password");

			if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Old password is incorrect");
			}

			log.info("Checking password history");

			ApiResponse<?> historyResponse = validatePasswordHistory(user.getUserId(), request.getNewPassword(),
					CredentialType.PASSWORD);

			if (historyResponse != null)
				return historyResponse;

			String encodedPassword = passwordEncoder.encode(request.getNewPassword());

			user.setPassword(encodedPassword);
			user.setPasswordUpdatedAt(LocalDateTime.now());

			savePasswordHistory(user.getUserId(), request.getNewPassword(), CredentialType.PASSWORD);

		} else {

			log.info("Validating old pin");

			if (!passwordEncoder.matches(request.getOldPin(), user.getPin())) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Old PIN is incorrect");
			}

			log.info("Checking pin history");

			ApiResponse<?> historyResponse = validatePasswordHistory(user.getUserId(), request.getNewPin(),
					CredentialType.PIN);

			if (historyResponse != null)
				return historyResponse;

			String encodedPin = passwordEncoder.encode(request.getNewPin());

			user.setPin(encodedPin);
			user.setPinUpdatedAt(LocalDateTime.now());

			savePasswordHistory(user.getUserId(), request.getNewPin(), CredentialType.PIN);
		}

		user.setForcePasswordReset(false);
		user.setFirstTimeLogin(false);
		userRepository.save(user);

		log.info("Change password completed");

		return ApiResponse.success("Credentials updated successfully");
	}

	private ApiResponse<?> validatePasswordHistory(Integer userId, String newValue, CredentialType type) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside the validatePasswordHistory method");

		List<PasswordHistoryEntity> historyList = passwordHistoryRepository
				.findTop5ByUserIdAndCredentialTypeOrderByCreatedAtDesc(userId, type);

		for (PasswordHistoryEntity history : historyList) {

			if (passwordEncoder.matches(newValue, history.getCredential())) {
				return ApiResponse.failure(ResponseCode.FAILURE,
						"New " + type.name().toLowerCase() + " must not match last 5");
			}
		}

		return null;
	}

	private void savePasswordHistory(Integer userId, String rawValue, CredentialType type) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside savePasswordHistory");

		PasswordHistoryEntity history = new PasswordHistoryEntity();
		history.setUserId(userId);
		history.setCredential(passwordEncoder.encode(rawValue));
		history.setCredentialType(type);
		history.setCreatedAt(LocalDateTime.now());
		log.info("{} history saved successfully for userId: {}", type, userId);
		passwordHistoryRepository.save(history);
	}

	@Override
	public ApiResponse<?> logout() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated()) {
			return ApiResponse.failure("Invalid or missing token");
		}

		log.info("User logged out: {}", auth.getPrincipal());

		return ApiResponse.success("Logged out successfully");
	}
}
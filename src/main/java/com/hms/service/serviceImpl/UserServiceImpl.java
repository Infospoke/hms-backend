package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

		log.info("Create user started");

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
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.INVALID_DOB_FORMAT);
		}

		if (dob.isAfter(LocalDate.now().minusYears(18))) {
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.USER_AGE_MUST_BE_ABOVE_18);
		}

		if (request.getAlternateContact() != null && request.getAlternateContact().equals(request.getMobileNumber())) {
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.ALTERNATIVE_NUMBER_MUST_BE_DIFFERENT);
		}

		String rawPassword = PasswordGenerator.generatePassword(8);
		String rawPin = PasswordGenerator.generatePin(4);

		log.info("Credentials generated");

		UserEntity user = new UserEntity();

		user.setUserTypeId(request.getUserTypeId());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setEmail(request.getEmail());
		user.setEmployeeId(request.getEmployeeId());
		user.setMobileNumber(request.getMobileNumber());
		user.setAlternateContact(request.getAlternateContact());

		user.setUsername(request.getFirstName());
		user.setDateOfBirth(dob);
		user.setFirstTimeLogin(true);
		user.setEmploymentTypeId(request.getEmploymentTypeId());

		if (businessUnitRepository.existsById(request.getBusinessUnitId())) {
			user.setBusinessUnitId(request.getBusinessUnitId());
		} else {
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(Constants.INVALID_BUSINESS_UNIT_ID));
		}

		if (departmentsRepository.existsById(request.getDepartmentId())) {
			user.setDepartmentId(request.getDepartmentId());
		} else {
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(Constants.INVALID_DEPARTMENT_ID));
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

		log.info("User saved");

		AssignRolesEntity role = new AssignRolesEntity();
		role.setAssignRoleId(sequenceGenerator.generateAssignRoleId());
		role.setUserId(userId);
		role.setRoleId(request.getRoleId());
		role.setAssignedBy("ADMIN");
		role.setAssignedAt(LocalDate.now());

		assignRolesRepository.save(role);

		log.info("Role assigned");

		savePasswordHistory(userId, rawPassword, CredentialType.PASSWORD);
		savePasswordHistory(userId, rawPin, CredentialType.PIN);

		log.info("Password history saved");

		try {
			log.info("Sending mail");

			String subject = Constants.USER_CREATED_MAIL_SUBJECT;

			String body = String.format(Constants.USER_CREATED_MAIL_BODY, request.getFirstName(), request.getEmail(),
					rawPassword, rawPin);

			mailService.sendMail(fromEmail, request.getEmail(), null, subject, body, null);

			log.info("Mail sent");

		} catch (Exception e) {
			log.error("Mail failed");
		}

		Map<String, Object> data = new HashMap<>();
		data.put("userId", userId);
		data.put("username", request.getFirstName());

		log.info("Create user completed");

		return ApiResponse.success(ResponseCode.SUCCESS, Constants.SUCCESS, data);

	}

	@Override
	public ApiResponse<?> getUserCounts() {

		log.info("UserServiceImpl:: Inside getUserCounts");

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

		log.info("UserServiceImpl:: Exit getUserCounts");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	public ApiResponse<?> getUsersList(FilterRequest request) {

		log.info("UserServiceImpl:: Inside getUsersList");

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

		log.info("UserServiceImpl:: Exit getUsersList");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	@Transactional
	public ApiResponse<?> updateUser(Integer id, UpdateUserRequest request) {

		log.info("updateUser - Started for userId: {}", id);

		UserEntity user = userRepository.findByUserId(id).orElseThrow(() -> new RuntimeException("User not found"));

		AssignRolesEntity roleEntity = assignRolesRepository.findByUserId(id)
				.orElseThrow(() -> new RuntimeException("Role mapping not found"));

		if (request.getRoleId() != null && !request.getRoleId().equals(roleEntity.getRoleId())) {

			log.info("Role change detected for userId: {}", id);

			roleEntity.setRoleId(request.getRoleId());
			roleEntity.setAssignedBy("ADMIN");
			roleEntity.setAssignedAt(LocalDate.now());

			assignRolesRepository.save(roleEntity);

			log.info("Role updated successfully");
		}

		if (request.getBusinessUnitId() != null) {
			user.setBusinessUnitId(request.getBusinessUnitId());
			log.info("Business Unit updated");
		}

		if (request.getDepartmentId() != null) {
			user.setDepartmentId(request.getDepartmentId());
			log.info("Department updated");
		}

		if (Boolean.TRUE.equals(request.getDeactivate())) {

			user.setActive(false);
			user.setDeactivated(true);

			user.setUpdatedBy("ADMIN");
			user.setUpdatedAt(LocalDate.now());

			userRepository.save(user);

			log.info("User deactivated successfully");

			return ApiResponse.success(ResponseCode.SUCCESS, "User deactivated successfully", null);
		}

		user.setUpdatedBy("ADMIN");
		user.setUpdatedAt(LocalDate.now());

		userRepository.save(user);

		log.info("User updated successfully");

		return ApiResponse.success(ResponseCode.SUCCESS, "User updated successfully", null);
	}

	@Override
	public ApiResponse<UserUpdationResponse> getUserById(Integer id) {

		log.info("getUserById - Started for userId: {}", id);

		UserEntity user = userRepository.findByUserId(id).orElseThrow(() -> new RuntimeException("User not found"));

		AssignRolesEntity roleEntity = assignRolesRepository.findByUserId(id)
				.orElseThrow(() -> new RuntimeException("Role mapping not found"));

		RolesEntity role = rolesRepository.findById(roleEntity.getRoleId())
				.orElseThrow(() -> new RuntimeException("Role not found"));

		UserUpdationResponse response = new UserUpdationResponse(user.getUsername(), user.getEmail(), user.getActive(),
				role.getRoleName(), roleEntity.getAssignedBy(), roleEntity.getAssignedAt());

		return ApiResponse.success(ResponseCode.SUCCESS, "User details fetched successfully", response);
	}


	@Override
	public ApiResponse<LoginResponse> login(LoginRequest request, String channel) {

		try {
			LOGGER.info("UserManagement::UserServiceImpl::Inside the login method");

			validateLogin(request, channel);

			UserEntity user = userRepository.findByEmailAndActiveTrue(request.getEmail())
					.orElseThrow(() -> new IllegalArgumentException("User is deactivated"));

			log.info("User fetched");

			if (Boolean.TRUE.equals(user.getAccountLocked())) {

				log.info("Account is locked");

				if (Boolean.TRUE.equals(user.getForcePasswordReset())) {
					throw new IllegalArgumentException("Please reset your password");
				}

				if (user.getLockTime() != null && user.getLockTime().plusMinutes(2).isAfter(LocalDateTime.now())) {
					throw new IllegalArgumentException("Account is locked. Try after 2 minutes");
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

				throw new IllegalArgumentException("Password expired. Please reset your password");
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

					throw new IllegalArgumentException("Account locked for 2 minutes");
				}

				if (attempts > 3) {
					user.setAccountLocked(true);
					user.setForcePasswordReset(true);
					userRepository.save(user);

					throw new IllegalArgumentException("Too many attempts. Please reset password");
				}

				userRepository.save(user);
				throw new IllegalArgumentException("Invalid credentials");
			}

			log.info("Login successful");

			user.setFailedAttempts(0);
			user.setAccountLocked(false);
			user.setLockTime(null);
			userRepository.save(user);

			AssignRolesEntity assignRole = assignRolesRepository.findByUserId(user.getUserId())
					.orElseThrow(() -> new IllegalArgumentException("No role assigned to this user"));

			RolesEntity role = rolesRepository.findByRoleId(assignRole.getRoleId())
					.orElseThrow(() -> new IllegalArgumentException("Assigned role not found"));

			List<PermissionEntity> permissions = permissionRepository.findByRoleId(assignRole.getRoleId());

			if (permissions == null || permissions.isEmpty()) {
				throw new IllegalArgumentException("No permissions configured for the assigned role");
			}

			Map<Integer, String> moduleMap = moduleRepository.findAll().stream()
					.collect(Collectors.toMap(ModuleEntity::getModuleId, ModuleEntity::getModuleName));

			List<String> permissionsList = new ArrayList<>();

			for (PermissionEntity p : permissions) {

			    String moduleName = moduleMap.get(p.getModuleId())
			            .toUpperCase()
			            .replace(" ", "_");

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

			String token = jwtService.generateToken(user.getEmail(), user.getUsername(), role.getRoleName(), permissionsList,
					user.getFirstTimeLogin());

			LoginResponse response = new LoginResponse();
			response.setToken(token);

			return ApiResponse.success(ResponseCode.SUCCESS, "Login Successfull", response);

		} catch (Exception e) {
			log.error("Login failed: {}", e.getMessage());
			return ApiResponse.failure(e.getMessage());
		}
	}

	private void validateLogin(LoginRequest request, String channel) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside the validateLogin method");

		if (request == null) {
			throw new IllegalArgumentException("Invalid request");
		}

		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}

		if (channel == null || channel.isBlank()) {
			throw new IllegalArgumentException("Channel is required");
		}

		if (!ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)
				&& !ChannelTypes.MOBILE.getChannelName().equalsIgnoreCase(channel)) {
			throw new IllegalArgumentException("Invalid channel");
		}

		if (ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)) {

			if (request.getPassword() == null || request.getPassword().isBlank()) {
				throw new IllegalArgumentException("Password is required");
			}

		} else {

			if (request.getPin() == null || request.getPin().isBlank()) {
				throw new IllegalArgumentException("Pin is required");
			}
		}
	}

	@Transactional
	@Override
	public ApiResponse<?> forgotPassword(String email) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside the forgotPassword method");

		UserEntity user = userRepository.findByEmailAndActiveTrue(email)
				.orElseThrow(() -> new IllegalArgumentException("User is deactivated"));

		String rawPassword = PasswordGenerator.generatePassword(8);
		String rawPin = PasswordGenerator.generatePin(4);

		String encodedPassword = passwordEncoder.encode(rawPassword);
		String encodedPin = passwordEncoder.encode(rawPin);

		savePasswordHistory(user.getUserId(), rawPassword, CredentialType.PASSWORD);
		savePasswordHistory(user.getUserId(), rawPin, CredentialType.PIN);

		user.setPassword(encodedPassword);
		user.setPin(encodedPin);

		user.setPasswordUpdatedAt(LocalDateTime.now());
		user.setPinUpdatedAt(LocalDateTime.now());

		user.setFailedAttempts(0);
		user.setAccountLocked(false);
		user.setLockTime(null);
		user.setForcePasswordReset(false);

		userRepository.save(user);

		try {
			sendForgotPasswordMail(user, rawPassword, rawPin);
		} catch (Exception e) {
			log.error("Mail failed");
			throw new RuntimeException("Failed to send email");
		}

		log.info("Forgot password completed");

		return ApiResponse.success("New credentials sent to registered email");
	}

	private void sendForgotPasswordMail(UserEntity user, String password, String pin) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside the sendForgotPasswordMail method");

		String subject = Constants.FORGOT_PASSWORD_SUBJECT;

		String body = String.format(Constants.FORGOT_PASSWORD_BODY, user.getFirstName(), user.getEmail(), password,
				pin);

		mailService.sendMail(fromEmail, user.getEmail(), null, subject, body, null);
	}

	@Override
	public ApiResponse<?> changePassword(ChangePasswordRequest request, String channel) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside the changePassword method");

		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		UserEntity user = userRepository.findByEmailAndActiveTrue(email)
				.orElseThrow(() -> new IllegalArgumentException("User is deactivated"));

		log.info("User fetched");

		if (ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)) {

			log.info("Validating old password");

			if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
				throw new IllegalArgumentException("Old password is incorrect");
			}

			log.info("Checking password history");

			validatePasswordHistory(user.getUserId(), request.getNewPassword(), CredentialType.PASSWORD);

			String encodedPassword = passwordEncoder.encode(request.getNewPassword());

			user.setPassword(encodedPassword);
			user.setPasswordUpdatedAt(LocalDateTime.now());

			savePasswordHistory(user.getUserId(), request.getNewPassword(), CredentialType.PASSWORD);

		} else {

			log.info("Validating old pin");

			if (!passwordEncoder.matches(request.getOldPin(), user.getPin())) {
				throw new IllegalArgumentException("Old PIN is incorrect");
			}

			log.info("Checking pin history");

			validatePasswordHistory(user.getUserId(), request.getNewPin(), CredentialType.PIN);

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

	private void validatePasswordHistory(Integer userId, String newValue, CredentialType type) {

		LOGGER.info("UserManagement::UserServiceImpl::Inside the validatePasswordHistory method");

		List<PasswordHistoryEntity> historyList = passwordHistoryRepository
				.findTop5ByUserIdAndCredentialTypeOrderByCreatedAtDesc(userId, type);

		for (PasswordHistoryEntity history : historyList) {

			if (passwordEncoder.matches(newValue, history.getCredential())) {
				throw new IllegalArgumentException("New " + type.name().toLowerCase() + " must not match last 5");
			}
		}
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
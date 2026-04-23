package com.hms.service.serviceImpl;

import java.security.Key;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.ModuleEntity;
import com.hms.service.entity.PermissionEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.enums.ChannelTypes;
import com.hms.service.repository.AssignRolesRepository;
import com.hms.service.repository.BusinessUnitRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.ModuleRepository;
import com.hms.service.repository.PasswordHistoryRepository;
import com.hms.service.repository.PermissionRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.FilterRequest;
import com.hms.service.request.LoginRequest;
import com.hms.service.request.UpdateUserRequest;
import com.hms.service.request.UserCreationRequest;
import com.hms.service.response.LoginResponse;
import com.hms.service.response.UserResponse;
import com.hms.service.response.UserUpdationResponse;
import com.hms.service.service.IUserService;
import com.hms.service.utils.PasswordGenerator;
import com.hms.service.utils.SequenceGenerator;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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

	@Value("${spring.mail.username}")
	private String fromEmail;
	
	private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);
	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655465675458576D5A71347437";

	@Override
	@Transactional
	public ApiResponse<?> createUser(UserCreationRequest request) {

		log.info("UserServiceImpl:: Inside the createUser Method");

		if (userRepository.existsByEmployeeId(request.getEmployeeId())) {
			log.info("employee is already exists");
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.EMPLOYEE_ID_ALREADY_EXISTS);
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			log.info("email already exists");
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.EMAIL_ALREADY_EXISTS);
		}

		LocalDate dob;
		try {
			dob = LocalDate.parse(request.getDateOfBirth());
		} catch (Exception e) {
			log.error("exception occured at dob " + e.getMessage());
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.INVALID_DOB_FORMAT);
		}

		if (dob.isAfter(LocalDate.now().minusYears(18))) {
			log.info("user age must be above 18");
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.USER_AGE_MUST_BE_ABOVE_18);
		}

		if (request.getAlternateContact() != null && request.getAlternateContact().equals(request.getMobileNumber())) {
			log.info("alternative number must be different");
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.ALTERNATIVE_NUMBER_MUST_BE_DIFFERENT);
		}

		String rawPassword = PasswordGenerator.generatePassword(8);
		String rawPin = PasswordGenerator.generatePin(4);

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
		user.setEmploymentTypeId(request.getEmploymentTypeId());
		if (businessUnitRepository.existsById(request.getBusinessUnitId())) {
			user.setBusinessUnitId(request.getBusinessUnitId());
		} else {
			log.info("BusinessUnit Id is required");
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(Constants.INVALID_BUSINESS_UNIT_ID));
		}
		if (departmentsRepository.existsById(request.getDepartmentId())) {
			user.setDepartmentId(request.getDepartmentId());
		} else {
			log.info("Department Id is required");
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(Constants.INVALID_DEPARTMENT_ID));
		}

		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setPin(passwordEncoder.encode(rawPin));
		log.info("PIN" + rawPin);
		log.info("password" + rawPassword);

		user.setActive(true);
		user.setDeactivated(false);
		user.setUpdatedBy("ADMIN");
		user.setUpdatedAt(LocalDate.now());

		Integer userId = sequenceGenerator.generateUserId();
		user.setUserId(userId);

		log.info("Saving user: {}", userId);
		userRepository.save(user);

		AssignRolesEntity role = new AssignRolesEntity();
		role.setAssignRoleId(sequenceGenerator.generateAssignRoleId());
		role.setUserId(userId);
		role.setRoleId(request.getRoleId());
		role.setAssignedBy("ADMIN");
		role.setAssignedAt(LocalDate.now());

		assignRolesRepository.save(role);

		log.info("User created successfully: {}", userId);

		Map<String, Object> data = new HashMap<>();
		data.put("userId", userId);
		data.put("username", request.getFirstName());
		log.info("UserServiceImpl:: Exit from the createUser Method");
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
	        return ApiResponse.failure(
	            ResponseCode.FAILURE,
	            "failure",
	            List.of("page and size must be provided")
	        );
	    }

	    if (request.getPage() < 0 || request.getSize() <= 0) {
	        return ApiResponse.failure(
	            ResponseCode.FAILURE,
	            "failure",
	            List.of("Invalid page or size values")
	        );
	    }

	    
	    Sort sort = Sort.by(
	        "DESC".equalsIgnoreCase(request.getDirection())
	            ? Sort.Direction.DESC
	            : Sort.Direction.ASC,
	        request.getSortBy() != null ? request.getSortBy() : "userId"
	    );

	    Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

	 
	    Integer roleId = null;

	    if (request.getFilters() != null && request.getFilters().containsKey("roleId")) {
	        try {
	            roleId = Integer.parseInt(request.getFilters().get("roleId").toString());
	        } catch (Exception e) {
	            return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "failure",
	                List.of("roleId must be a valid number")
	            );
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

	    UserEntity user = userRepository.findByUserId(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    AssignRolesEntity roleEntity = assignRolesRepository.findByUserId(id)
	            .orElseThrow(() -> new RuntimeException("Role mapping not found"));

	    RolesEntity role = rolesRepository.findById(roleEntity.getRoleId())
	            .orElseThrow(() -> new RuntimeException("Role not found"));

	    UserUpdationResponse response = new UserUpdationResponse(
	            user.getUsername(),
	            user.getEmail(),
	            user.getActive(),
	            role.getRoleName(),
	            roleEntity.getAssignedBy(),
	            roleEntity.getAssignedAt()
	    );

	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            "User details fetched successfully",
	            response
	    );
	}


	@Override
	public boolean validateToken(String token) {
		LOGGER.info("UserManagement::UserServiceImpl::Inside the validateToken method");
		try {
			Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
			LOGGER.info("UserManagement::UserServiceImpl::Exit from the validateToken method");
			return true;

		} catch (io.jsonwebtoken.security.SignatureException | ExpiredJwtException | UnsupportedJwtException
				| MalformedJwtException | IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String generateToken(String email, String userName, String roleName, List<String> modules) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("username", userName);
		claims.put("role", roleName);
		claims.put("modules", modules);
		return createToken(claims, email);
	}

	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 180))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public Claims decodeToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	public String extractUsername(String token) {
		return decodeToken(token).getSubject();
	}
	
	public String extractUsernameFromClaims(String token) {
	    return decodeToken(token).get("username", String.class);
	}

	public String extractRole(String token) {
		return decodeToken(token).get("role", String.class);
	}

	public List<String> extractModules(String token) {
		return decodeToken(token).get("modules", List.class);
	}

	@Override
	public ApiResponse<LoginResponse> login(LoginRequest request, String channel) {

		try {
			validateLogin(request, channel);

			UserEntity user = userRepository.findByEmailAndActiveTrue(request.getEmail())
					.orElseThrow(() -> new IllegalArgumentException("User is deactivated"));

			if (Boolean.TRUE.equals(user.getAccountLocked())) {

				if (Boolean.TRUE.equals(user.getForcePasswordReset())) {
					throw new IllegalArgumentException("Please reset your password");
				}

				if (user.getLockTime() != null && user.getLockTime().plusMinutes(2).isAfter(LocalDateTime.now())) {

					throw new IllegalArgumentException("Account is locked. Try after 2 minutes");

				} else {
					user.setAccountLocked(false);
					user.setLockTime(null);
					userRepository.save(user);
				}
			}

			if (user.getPasswordUpdatedAt() != null
					&& user.getPasswordUpdatedAt().plusMonths(3).isBefore(LocalDateTime.now())) {

				user.setForcePasswordReset(true);
				userRepository.save(user);

				throw new IllegalArgumentException("Password expired. Please reset your password");
			}

			boolean isValid;

			if (ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)) {
				isValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
			} else {
				isValid = passwordEncoder.matches(request.getPin(), user.getPin());
			}

			if (!isValid) {

				int attempts = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
				attempts++;
				user.setFailedAttempts(attempts);

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

			user.setFailedAttempts(0);
			user.setAccountLocked(false);
			user.setLockTime(null);
			userRepository.save(user);

			AssignRolesEntity assignRole = assignRolesRepository.findByUserId(user.getUserId())
					.orElseThrow(() -> new IllegalArgumentException("User is deactivated"));

			RolesEntity role = rolesRepository.findByRoleId(assignRole.getRoleId())
					.orElseThrow(() -> new IllegalArgumentException("User is deactivated"));

			List<PermissionEntity> permissions = permissionRepository.findByRoleId(assignRole.getRoleId());

			if (permissions == null || permissions.isEmpty()) {
				throw new IllegalArgumentException("User is deactivated");
			}

			Map<Integer, String> moduleMap = moduleRepository.findAll().stream()
					.collect(Collectors.toMap(ModuleEntity::getModuleId, ModuleEntity::getModuleName));

			List<String> modules = permissions.stream().map(p -> moduleMap.get(p.getModuleId()))
					.filter(Objects::nonNull).map(name -> name.toUpperCase().replace(" ", "_")).distinct().toList();

			String token = generateToken(user.getEmail(),user.getUsername(), role.getRoleName(), modules);

			LoginResponse response = new LoginResponse();
			response.setToken(token);

			return ApiResponse.success(ResponseCode.SUCCESS, "Login Successfull", response);

		} catch (Exception e) {
			return ApiResponse.failure(e.getMessage());
		}
	}

	private void validateLogin(LoginRequest request, String channel) {

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

	@Override
	public ApiResponse<?> forgotPassword(String email) {

	    UserEntity user = userRepository.findByEmailAndActiveTrue(email)
	            .orElseThrow(() -> new IllegalArgumentException("User is deactivated"));

	    String rawPassword = PasswordGenerator.generatePassword(8);
	    String rawPin = PasswordGenerator.generatePin(4);

	    String encodedPassword = passwordEncoder.encode(rawPassword);
	    String encodedPin = passwordEncoder.encode(rawPin);

	    user.setPassword(encodedPassword);
	    user.setPin(encodedPin);

	    user.setPasswordUpdatedAt(LocalDateTime.now());
	    user.setPinUpdatedAt(LocalDateTime.now());

	    user.setFailedAttempts(0);
	    user.setAccountLocked(false);
	    user.setLockTime(null);
	    user.setForcePasswordReset(false);

	    userRepository.save(user);
	    sendForgotPasswordMail(user, rawPassword, rawPin);

	    return ApiResponse.success("New credentials sent to registered email");
	}
	
	private void sendForgotPasswordMail(UserEntity user, String password, String pin) {

	    String subject = "Your New Login Credentials";

	    String body = "<html><body>"
	            + "<p>Dear " + user.getFirstName() + ",</p>"
	            + "<p>Your password has been reset successfully.</p>"
	            + "<p><b>Username:</b> " + user.getEmail() + "</p>"
	            + "<p><b>Password:</b> " + password + "</p>"
	            + "<p><b>PIN:</b> " + pin + "</p>"
	            + "<br/>"
	            + "<p>Please login and change your credentials immediately.</p>"
	            + "<br/>"
	            + "<p>Regards,<br/>Infospoke</p>"
	            + "</body></html>";

	    mailService.sendMail(
	            fromEmail, 
	            user.getEmail(),        
	            null,                  
	            subject,
	            body,
	            null                   
	    );
	}


}
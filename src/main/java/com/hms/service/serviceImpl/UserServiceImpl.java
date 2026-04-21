package com.hms.service.serviceImpl;

import java.security.Key;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;


import com.hms.service.entity.AssignRolesEntity;

import com.hms.service.entity.UserEntity;


import com.hms.service.repository.AssignRolesRepository;


import com.hms.service.repository.ModuleRepository;
import com.hms.service.repository.PermissionRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.LoginRequest;
import com.hms.service.request.UpdateUserRequest;
import com.hms.service.request.UserCreationRequest;

import com.hms.service.request.UserFilterRequest;
import com.hms.service.response.UserListResponse;
import com.hms.service.response.LoginResponse;

import com.hms.service.response.UserResponse;
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

	private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);
	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655465675458576D5A71347437";

	@Override
	@Transactional
	public ApiResponse<?> createUser(UserCreationRequest request) {

		log.info("UserServiceImpl:: Inside the createUser Method");

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
		user.setBusinessUnitId(request.getBusinessUnitId());
		user.setDepartmentId(request.getDepartmentId());

		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setPin(passwordEncoder.encode(rawPin));

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

		return ApiResponse.success(ResponseCode.SUCCESS, "success", data);
	}

	@Override
	public ApiResponse<?> getUsers(UserFilterRequest request) {

		log.info("getUsers - Started");

		if (request.getPage() == null || request.getSize() == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("page and size must be provided"));
		}

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("userId").descending());

		log.info("Fetching user list for roleId: {}", request.getRoleId());

		Page<UserResponse> pageResult = userRepository.findUsersByRole(request.getRoleId(), pageable);

		Long total = userRepository.getTotalUsers();
		Long active = userRepository.getActiveUsers();
		Long deactivated = userRepository.getDeactivatedUsers();
		Long filtered = userRepository.getFilteredUsers(request.getRoleId());

		UserListResponse response = new UserListResponse(pageResult.getContent(), total, active, deactivated, filtered);

		log.info("Users fetched. Total: {}, Filtered: {}", total, filtered);

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	@Transactional
	public ApiResponse<String> updateUser(Integer id, UpdateUserRequest request) {

	    log.info("updateUser - Started for userId: {}", id);

	    
	    UserEntity user = userRepository.findByUserId(id)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    AssignRolesEntity roleEntity = assignRolesRepository.findByUserId(id)
	            .orElseThrow(() -> new RuntimeException("Role mapping not found"));

	    if (request.getRoleId() != null &&
	            !request.getRoleId().equals(roleEntity.getRoleId())) {

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

	        return ApiResponse.success(
	                ResponseCode.SUCCESS,
	                "User deactivated successfully",
	                null
	        );
	    }

	    user.setUpdatedBy("ADMIN");
	    user.setUpdatedAt(LocalDate.now());

	    userRepository.save(user);

	    log.info("User updated successfully");

	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            "User updated successfully",
	            null
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

	public String generateToken(String userName, String roleName, List<String> modules) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", roleName);
		claims.put("modules", modules);
		return createToken(claims, userName);
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

	public String extractRole(String token) {
		return decodeToken(token).get("role", String.class);
	}

	public List<String> extractModules(String token) {
		return decodeToken(token).get("modules", List.class);
	}

	@Override
	public ApiResponse<LoginResponse> login(LoginRequest request, String channel) {
		// TODO Auto-generated method stub
		return null;
	}


//	public ApiResponse<LoginResponse> login(LoginRequest request, String channel) {
//
//		UserEntity user = userRepository.findByEmail(request.getEmail());
//
//		if (user == null) {
//			return ApiResponse.failure(Constants.INVALID_CREDENTIALS);
//		}
//
//		if (channel == null || channel.isBlank()) {
//			return ApiResponse.failure("Channel is required");
//		}
//
//		if (ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)) {
//
//			if (request.getPassword() == null || request.getPassword().isBlank()) {
//				return ApiResponse.failure("Password is required");
//			}
//
//			if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//				return ApiResponse.failure(Constants.INVALID_CREDENTIALS);
//			}
//
//		} else if (ChannelTypes.MOBILE.getChannelName().equalsIgnoreCase(channel)) {
//
//			if (request.getPin() == null || request.getPin().isBlank()) {
//				return ApiResponse.failure("Pin is required");
//			}
//
//			if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
//				return ApiResponse.failure(Constants.INVALID_CREDENTIALS);
//			}
//
//		} else {
//			return ApiResponse.failure("Invalid channel");
//		}
//
//		RolesEntity role = rolesRepository.findById(user.getRoleId())
//				.orElseThrow(() -> new CustomSystemErrorException("Role not found"));
//
//		String roleName = role.getRoleName();
//
//		List<PermissionEntity> permissionEntities = permissionRepository.findByRoleId(user.getRoleId());
//
//		if (permissionEntities == null || permissionEntities.isEmpty()) {
//			return ApiResponse.failure("Permissions not found for this role");
//		}
//
//		Map<Integer, String> moduleMap = moduleRepository.findAll().stream()
//				.collect(Collectors.toMap(ModuleEntity::getModuleId, ModuleEntity::getModuleName));
//		List<String> modules = permissionEntities.stream().map(p -> moduleMap.get(p.getModuleId()))
//				.filter(Objects::nonNull).map(name -> name.toUpperCase().replace(" ", "_")).distinct()
//				.collect(Collectors.toList());
//
//		String token = generateToken(user.getEmail(), roleName, modules);
//
//		LoginResponse loginResponse = new LoginResponse();
//		loginResponse.setToken(token);
//
//		return new ApiResponse<>(ResponseCode.SUCCESS, "Success", loginResponse);
//
//	}
}
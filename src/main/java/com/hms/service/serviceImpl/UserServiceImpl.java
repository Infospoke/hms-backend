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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.dto.LoginData;
import com.hms.service.entity.ModuleEntity;
import com.hms.service.entity.PermissionEntity;
import com.hms.service.entity.RolesEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.enums.ChannelTypes;
import com.hms.service.enums.UserStatus;
import com.hms.service.repository.ModuleRepository;
import com.hms.service.repository.PermissionRepository;
import com.hms.service.repository.RolesRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.LoginRequest;
import com.hms.service.request.UpdateUserRequest;
import com.hms.service.request.UserCreationRequest;
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

	private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);
	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655465675458576D5A71347437";

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
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.INVALID_DOB_FORMAT);
		}

		if (dob.isAfter(LocalDate.now().minusYears(18))) {
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.USER_AGE_MUST_BE_ABOVE_18);
		}

		if (request.getAlternateContact() != null && request.getAlternateContact().equals(request.getMobileNumber())) {

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
		user.setRoleName(request.getRoleName());

		user.setPassword(encryptedPassword);
		user.setPin(encryptedPin);

		user.setStatus(UserStatus.ACTIVE);

		user.setAssignedBy("ADMIN");
		user.setAssignedAt(LocalDateTime.now());

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
				.map(user -> new UserResponse(user.getId(), user.getFirstName() + " " + user.getLastName(),
						user.getRoleId(), user.getEmail(), user.getRoleName(), user.getStatus()))
				.toList();

		log.info("UserServiceImpl::Exit from the getAllUsers method");
		return ApiResponse.success(Constants.USER_FETCHED, list, (int) users.getTotalElements());
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

	@Override
	public ApiResponse<String> updateUser(Integer id, UpdateUserRequest request) {

		UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

		if (request.getRoleId() != null) {
			if (!request.getRoleId().equals(user.getRoleId())) {
				user.setRoleId(request.getRoleId());

				user.setAssignedBy("ADMIN");
				user.setAssignedAt(LocalDateTime.now());
			}
		}

		if (request.getBusinessUnitId() != null) {
			user.setBusinessUnitId(request.getBusinessUnitId());
		}

		if (request.getDepartmentId() != null) {
			user.setDepartmentId(request.getDepartmentId());
		}

		if (request.getStatus() != null) {
			user.setStatus(UserStatus.valueOf(request.getStatus()));
		}

		user.setUpdatedBy("ADMIN");
		user.setUpdatedAt(LocalDateTime.now());

		userRepository.save(user);

		return ApiResponse.success(Constants.USER_UPDATED_SUCCESSFULLY);
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
	public ApiResponse<LoginData> login(LoginRequest request, String channel) {

		UserEntity user = userRepository.findByEmail(request.getEmail());

		if (user == null) {
			return ApiResponse.failure(Constants.INVALID_CREDENTIALS);
		}

		if (channel == null || channel.isBlank()) {
			return ApiResponse.failure("Channel is required");
		}

		if (ChannelTypes.WEB.getChannelName().equalsIgnoreCase(channel)) {

			if (request.getPassword() == null || request.getPassword().isBlank()) {
				return ApiResponse.failure("Password is required");
			}

			if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
				return ApiResponse.failure(Constants.INVALID_CREDENTIALS);
			}

		} else if (ChannelTypes.MOBILE.getChannelName().equalsIgnoreCase(channel)) {

			if (request.getPin() == null || request.getPin().isBlank()) {
				return ApiResponse.failure("Pin is required");
			}

			if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
				return ApiResponse.failure(Constants.INVALID_CREDENTIALS);
			}

		} else {
			return ApiResponse.failure("Invalid channel");
		}

		RolesEntity role = rolesRepository.findById(user.getRoleId())
				.orElseThrow(() -> new RuntimeException("Role not found"));

		String roleName = role.getRoleName();

		List<PermissionEntity> permissionEntities = permissionRepository.findByRoleId(user.getRoleId());

		if (permissionEntities == null || permissionEntities.isEmpty()) {
			return ApiResponse.failure("Permissions not found for this role");
		}

		Map<Integer, String> moduleMap = moduleRepository.findAll().stream()
				.collect(Collectors.toMap(ModuleEntity::getModuleId, ModuleEntity::getModuleName));
		List<String> modules = permissionEntities.stream().map(p -> moduleMap.get(p.getModuleId()))
				.filter(Objects::nonNull).map(name -> name.toUpperCase().replace(" ", "_")).distinct()
				.collect(Collectors.toList());

		String token = generateToken(user.getEmail(), roleName, modules);

		LoginData data = new LoginData();
		data.setEmail(user.getEmail());
		data.setToken(token);

		return new ApiResponse<>(ResponseCode.SUCCESS, "Success", data);
	}
}
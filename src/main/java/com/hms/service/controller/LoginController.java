package com.hms.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.dto.LoginData;
import com.hms.service.request.LoginRequest;
import com.hms.service.service.IUserService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/hms/login")
public class LoginController {

	private IUserService userService;

	public LoginController(IUserService userService) {
		this.userService = userService;
	}

	@PostMapping("/user-login")
	public ApiResponse<LoginData> login(@RequestHeader("X-Channel") String channel,@Valid @RequestBody LoginRequest request) {

		return userService.login(request, channel);
	}

	@GetMapping("/validate")
	public boolean validateToken(@RequestParam("token") String token) {
		return userService.validateToken(token);
	}

}

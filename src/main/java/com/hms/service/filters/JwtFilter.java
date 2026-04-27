package com.hms.service.filters;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.service.serviceImpl.UserServiceImpl;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private UserServiceImpl userService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();

		if (path.startsWith("/hms/login/user-login") || path.startsWith("/hms/login/forgot-password")
				|| path.startsWith("/hms/login/validate")) {

			filterChain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");

			ApiResponse<?> error = ApiResponse.failure(ResponseCode.FAILURE, "Failure",
					List.of("Missing Authorization header"));

			objectMapper.writeValue(response.getWriter(), error);
			return;
		}

		String token = authHeader.substring(7);

		if (!userService.validateToken(token)) {

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");

			ApiResponse<?> error = ApiResponse.failure(ResponseCode.FAILURE, "Failure",
					List.of("Invalid or expired token"));

			objectMapper.writeValue(response.getWriter(), error);
			return;
		}

		String email = userService.extractUsername(token);

		List<String> permissions = userService.extractPermissions(token);

		List<SimpleGrantedAuthority> authorities = permissions.stream().map(SimpleGrantedAuthority::new).toList();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
				authorities);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}
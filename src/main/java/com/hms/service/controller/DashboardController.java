package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.service.IRecuriterDashboardService;
import com.hms.service.wrappers.ApiResponse;

@RestController
@RequestMapping("/hms/dashboard")

public class DashboardController {

	@Autowired
	private IRecuriterDashboardService iRecuriterDashboardService;

	@GetMapping("/recruiter")
	public ResponseEntity<ApiResponse<?>> dashboard() {

		ApiResponse<?> response = iRecuriterDashboardService.getDashboard();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/analytics")
	public ResponseEntity<ApiResponse<?>> recruiterAnalytics(@PathVariable("jobId") Integer jobId) {
		ApiResponse<?> response = iRecuriterDashboardService.getRecruiterAnalytics(jobId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
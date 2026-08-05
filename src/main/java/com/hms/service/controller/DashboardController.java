package com.hms.service.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.RecuriterPerformanceRequest;
import com.hms.service.service.IDashboardService;
import com.hms.service.wrappers.ApiResponse;

@RestController
@RequestMapping("/hms/dashboard")

public class DashboardController {

	@Autowired
	private IDashboardService iRecuriterDashboardService;

	@GetMapping("/recruiter")
	public ResponseEntity<ApiResponse<?>> dashboard() {

		ApiResponse<?> response = iRecuriterDashboardService.getDashboard();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/analytics")
	public ResponseEntity<ApiResponse<?>> getRecruiterAnalytics(

			@RequestParam(name = "jobId") Integer jobId,

			@RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,

			@RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {

		return ResponseEntity.ok(iRecuriterDashboardService.getRecruiterAnalytics(jobId, fromDate, toDate));
	}

	@GetMapping("/hiring")
	public ResponseEntity<ApiResponse<?>> getDashboard() {
		ApiResponse<?> response = iRecuriterDashboardService.getHiringDashboard();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/hiring-dashboard-analytics")
	public ResponseEntity<ApiResponse<?>> getHiringManagerAnalytics(@RequestParam(name = "srId") String srId,
			@RequestParam(name = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
			@RequestParam(name = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
		return ResponseEntity.ok(iRecuriterDashboardService.getHiringManagerAnalytics(srId, fromDate, toDate));
	}

	@PostMapping("/recruiter-performance")
	public ResponseEntity<ApiResponse<?>> getRecruiterPerformance(@RequestBody RecuriterPerformanceRequest request) {
		ApiResponse<?> response = iRecuriterDashboardService.getRecruiterPerformance(request);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}
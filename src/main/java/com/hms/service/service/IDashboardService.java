package com.hms.service.service;

import java.time.LocalDate;

import com.hms.service.request.RecuriterPerformanceRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IDashboardService {

	ApiResponse<?> getDashboard();

	ApiResponse<?> getRecruiterAnalytics(Integer jobId, LocalDate fromDate, LocalDate toDate);

	ApiResponse<?> getHiringDashboard();

	ApiResponse<?> getHiringManagerAnalytics(String srId, LocalDate fromDate, LocalDate toDate);
	
	 ApiResponse<?> getRecruiterPerformance(RecuriterPerformanceRequest request);
}
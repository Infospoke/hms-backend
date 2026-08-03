package com.hms.service.service;

import java.time.LocalDate;

import com.hms.service.wrappers.ApiResponse;

public interface IRecuriterDashboardService {

	ApiResponse<?> getDashboard();
	
	ApiResponse<?> getRecruiterAnalytics(Integer jobId, LocalDate fromDate, LocalDate toDate);


}
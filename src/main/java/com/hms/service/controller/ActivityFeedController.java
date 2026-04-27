package com.hms.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.response.ActivityFeedResponse;
import com.hms.service.service.IActivityFeedService;
import com.hms.service.wrappers.ApiResponse;

@RequestMapping("/hms/job-overview")
@RestController
public class ActivityFeedController {
	
	@Autowired
	private IActivityFeedService activityFeedService;
	
	@GetMapping("/activity-feed")
	public ResponseEntity<ApiResponse<List<ActivityFeedResponse>>> getAllActivities() {

	    ApiResponse<List<ActivityFeedResponse>> response = activityFeedService.getallFeeds();

	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

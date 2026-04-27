package com.hms.service.service;

import java.util.List;

import com.hms.service.response.ActivityFeedResponse;
import com.hms.service.wrappers.ApiResponse;

public interface IActivityFeedService {
	
	ApiResponse<List<ActivityFeedResponse>> getallFeeds();

}

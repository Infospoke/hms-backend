package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.entity.ActivityFeedEntity;
import com.hms.service.repository.ActivityFeedRepository;
import com.hms.service.response.ActivityFeedResponse;
import com.hms.service.service.IActivityFeedService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ActivityFeedServiceImpl implements IActivityFeedService {
	
	@Autowired
	private ActivityFeedRepository activityFeedRepository;
	
	@Override
	public ApiResponse<List<ActivityFeedResponse>> getallFeeds() {

	    log.info("ActivityFeedServiceImpl::Inside the getallFeeds method");

	    try {
	        LocalDateTime to = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
	        LocalDateTime from = to.minusHours(24);

	        List<ActivityFeedEntity> entities =
	                activityFeedRepository.findAllByTimeStampBetweenOrderByTimeStampDesc(from, to);

	        List<ActivityFeedResponse> activityFeedResponses = entities.stream().map(entity -> {
	            ActivityFeedResponse response = new ActivityFeedResponse();
	            BeanUtils.copyProperties(entity, response);
	            return response;
	        }).collect(Collectors.toList());

	        log.info("ActivityFeedServiceImpl::Exit from the getallFeeds method");

	        return ApiResponse.success(
	                ResponseCode.SUCCESS,
	                "Activity feeds fetched successfully",
	                activityFeedResponses
	        );

	    } catch (Exception e) {

	        log.error("Error while fetching activity feeds", e);

	      
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "Failed to fetch activity feeds",
	                List.of(e.getMessage())
	        );
	    }
	}
}
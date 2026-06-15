package com.hms.service.service;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IAIInterviewZoneService {

	ApiResponse<?> getAiInterviewZoneList(SpecificationFilterRequest request);

	ApiResponse<?> getAllScheduledInterviews(SpecificationFilterRequest request);

}

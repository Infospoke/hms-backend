package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.IAIInterviewZoneService;
import com.hms.service.wrappers.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hms/ai-interview-zone")
@RequiredArgsConstructor
public class AIInterviewZoneController {
	
	@Autowired
	private IAIInterviewZoneService iAIInterviewZoneService;
	
	@PostMapping("/list")
	public ApiResponse<?> getAiInterviewZoneList(@RequestBody SpecificationFilterRequest request) {
	    return iAIInterviewZoneService.getAiInterviewZoneList(request);
	}
	
	@PostMapping("/get-all-scheduled-interviews")
	public ApiResponse<?> getAllScheduledInterviews(@RequestBody SpecificationFilterRequest request) {
		return iAIInterviewZoneService.getAllScheduledInterviews(request);
	}

}

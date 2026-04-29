package com.hms.service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.FilterRequest;
import com.hms.service.request.KanbanFilterRequest;
import com.hms.service.service.IKanbanService;
import com.hms.service.service.IStaffingRequisitionService;
//import com.hms.service.serviceImpl.KanbanFilterServiceImpl;
import com.hms.service.wrappers.ApiResponse;

@RequestMapping("/hms/kanban")
@RestController()
public class KanbanFilterController {
	
	@Autowired
	private IKanbanService iKanbanService;

	@PostMapping("/filter")
	public ApiResponse<?> filterKanban(@RequestBody FilterRequest request) {
	    return iKanbanService.getFilteredData(request);
	}
	
}

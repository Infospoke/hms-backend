package com.hms.service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.request.SRFilterRequest;
import com.hms.service.request.StaffingRequisitionRequest;
import com.hms.service.request.UserFilterRequest;
import com.hms.service.service.IStaffingRequisitionService;
import com.hms.service.wrappers.ApiResponse;

@RequestMapping("/hms/staffing-requisition")
@RestController()
public class StaffingRequisitionController {
	
	@Autowired
	private IStaffingRequisitionService iStaffingRequisitionService;
	
	@PostMapping("/new-staffing-requisition")
	public ResponseEntity<ApiResponse<?>>newStaffingRequisition(@RequestPart(value="request") StaffingRequisitionRequest request,@RequestPart(value = "file", required = false) MultipartFile file){
		ApiResponse<?> response=iStaffingRequisitionService.newStaffingRequisition(request,file);
		return new ResponseEntity<>(response,HttpStatus.OK);
		
	}

	 @GetMapping("/by-sr-id")
	    public ResponseEntity<ApiResponse<?>> getBySrId(@RequestParam (value="request") String srId) {
	        return ResponseEntity.ok(iStaffingRequisitionService.getBySrId(srId));
	    }
	 
	 @PostMapping("/sr-list")
	 public ApiResponse<?> getAll(@RequestBody SRFilterRequest request) {
	     return iStaffingRequisitionService.getAll(request);
	 }
	 
	
}

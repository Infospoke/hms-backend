package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.request.StaffingRequisitionRequest;
import com.hms.service.service.IStaffingRequisitionService;
import com.hms.service.wrappers.ApiResponse;

@RequestMapping("/staffing-requisition")
@RestController()
public class StaffingRequisitionController {
	
	@Autowired
	private IStaffingRequisitionService iStaffingRequisitionService;
	
	@PostMapping("/new-staffing-requisition")
	public ResponseEntity<ApiResponse<?>>newStaffingRequisition(@RequestPart(value="request") StaffingRequisitionRequest request,@RequestPart(value = "file", required = false) MultipartFile file){
		ApiResponse<?> response=iStaffingRequisitionService.newStaffingRequisition(request,file);
		return new ResponseEntity<>(response,HttpStatus.OK);
		
	}

	@GetMapping("/test")
    public String testMongo() {
       iStaffingRequisitionService.test();
        return "Data inserted!";
    }
	
	

}

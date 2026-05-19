package com.hms.service.controller;

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
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.StaffingRequisitionRequest;
import com.hms.service.request.UpdateSrRequest;
import com.hms.service.service.IStaffingRequisitionService;
import com.hms.service.wrappers.ApiResponse;

@RequestMapping("/hms/staffing-requisition")
@RestController()
public class StaffingRequisitionController {

	@Autowired
	private IStaffingRequisitionService iStaffingRequisitionService;

	@PostMapping("/new-staffing-requisition")
	public ResponseEntity<ApiResponse<?>> newStaffingRequisition(
			@RequestPart(value = "request") StaffingRequisitionRequest request,
			@RequestPart(value = "file", required = false) MultipartFile file) {
		ApiResponse<?> response = iStaffingRequisitionService.newStaffingRequisition(request, file);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/by-sr-id")
	public ResponseEntity<ApiResponse<?>> getBySrId(@RequestParam(value = "request") String srId) {
		ApiResponse<?> response = iStaffingRequisitionService.getBySrId(srId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/sr-list")
	public ResponseEntity<ApiResponse<?>> getAll(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = iStaffingRequisitionService.getAll(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/get-all-sr-list-count")
	public ResponseEntity<ApiResponse<?>> getAllSrListCount() {
	    ApiResponse<?> response = iStaffingRequisitionService.getAllSrListCount();
	    return new ResponseEntity<>(response,HttpStatus.OK);
	}

	@PostMapping("/sr-approval")
	public ResponseEntity<ApiResponse<?>> srApproval(@RequestBody UpdateSrRequest request) {
		ApiResponse<?> response = iStaffingRequisitionService.srApproval(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/sr-counts")
	public ResponseEntity<ApiResponse<?>> getSrCounts() {
		ApiResponse<?> response = iStaffingRequisitionService.getSrCounts();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/assigned-srs-for-approvals")
	public ResponseEntity<ApiResponse<?>> assignedSrsForApprovals(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = iStaffingRequisitionService.assignedSrsForApprovals(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/approved-srs")
	public ResponseEntity<ApiResponse<?>> getAllApprovedServiceRequests(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = iStaffingRequisitionService.getAllApprovedServiceRequests(request);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
}
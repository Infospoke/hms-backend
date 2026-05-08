package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.ApprovalChainRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateApprovalChainRequest;
import com.hms.service.service.IApprovalChainService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hms/approval-chains")
@RequiredArgsConstructor
public class ApprovalChainController {

	@Autowired
    private IApprovalChainService iApprovalChainService;
	
	@PostMapping("/list")
	public ApiResponse<?> getApprovalChains(@RequestBody SpecificationFilterRequest request) {
	    return iApprovalChainService.getApprovalChainsList(request);
	}

	@GetMapping("/count")
	public ApiResponse<?> getCounts() {
	    return iApprovalChainService.getApprovalChainCounts();
	}
	
	@GetMapping("/details/{id}")
	public ApiResponse<?> getApprovalChainById(@PathVariable("id") Integer id) {
	    return iApprovalChainService.getApprovalChainById(id);
	}
	
	@PostMapping("/create")
	public ResponseEntity<ApiResponse<?>> createApprovalChain(@Valid @RequestBody ApprovalChainRequest request) {

		ApiResponse<?> response = iApprovalChainService.createApprovalChain(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<ApiResponse<?>> updateChain(@RequestBody UpdateApprovalChainRequest request) {

		ApiResponse<?> response = iApprovalChainService.updateApprovalChain(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
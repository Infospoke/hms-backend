package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.FilterRequest;
import com.hms.service.service.IApprovalChainService;
import com.hms.service.wrappers.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hms/approval-chains")
@RequiredArgsConstructor
public class ApprovalChainController {

	@Autowired
    private IApprovalChainService iApprovalChainService;
	
	@PostMapping("/list")
	public ApiResponse<?> getApprovalChains(@RequestBody FilterRequest request) {
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
	
}
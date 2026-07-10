package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.IOfferDetailsService;
import com.hms.service.wrappers.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hms/offer-details")
@RequiredArgsConstructor
public class OfferDetailsController {

	@Autowired
	private IOfferDetailsService iOfferDetailsService;

	@PostMapping("/ready-to-release")
	public ResponseEntity<ApiResponse<?>> getReadyToRelease(@RequestBody SpecificationFilterRequest request) {

		return ResponseEntity.ok(iOfferDetailsService.getReadyToRelease(request));

	}
	
	@PostMapping("/get-all-raise-offer-requests")
	public ResponseEntity<ApiResponse<?>> getAllRaiseOfferRequests(@RequestBody SpecificationFilterRequest request) {

		return ResponseEntity.ok(iOfferDetailsService.getAllRaiseOfferRequests(request));
	}

}

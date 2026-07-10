package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.hms.service.request.ApproveOfferRequest;

import com.hms.service.request.ReleaseOfferRequest;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.IOfferDetailsService;
import com.hms.service.wrappers.ApiResponse;



@RestController
@RequestMapping("/hms/offer-details")
public class OfferDetailsController {

	@Autowired
	private IOfferDetailsService iOfferDetailsService;

	@PostMapping("/ready-to-release-list")
	public ResponseEntity<ApiResponse<?>> getReadyToRelease(@RequestBody SpecificationFilterRequest request) {

		return ResponseEntity.ok(iOfferDetailsService.getReadyToRelease(request));

	}
	

	@PostMapping("/approve-offer")
	public ResponseEntity<ApiResponse<?>>approveOffer(@RequestBody ApproveOfferRequest request ) {
	    ApiResponse<?> response = iOfferDetailsService.approveOffer(request);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/get-all-raise-offer-requests")
	public ResponseEntity<ApiResponse<?>> getAllRaiseOfferRequests(@RequestBody SpecificationFilterRequest request) {


		return ResponseEntity.ok(iOfferDetailsService.getAllRaiseOfferRequests(request));
	}

	@PostMapping("/release-offers")
	public ResponseEntity<ApiResponse<?>> releaseOfferLetters(@RequestBody ReleaseOfferRequest request) {

		return ResponseEntity.ok(iOfferDetailsService.releaseOfferLetters(request));

	}
}

package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/get-offer-details-by-applicant-id/{applicantId}")
	public ResponseEntity<ApiResponse<?>> getOfferDetailsByApplicantId(
			@PathVariable("applicantId") Integer applicantId) {

		ApiResponse<?> response = iOfferDetailsService.getOfferDetailsByApplicantId(applicantId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get-offer-comments/{applicantId}")
	public ResponseEntity<ApiResponse<?>> getOfferComments(@PathVariable("applicantId") Integer applicantId) {

		ApiResponse<?> response = iOfferDetailsService.getOfferComments(applicantId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}

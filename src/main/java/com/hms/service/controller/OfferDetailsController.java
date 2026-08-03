package com.hms.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hms.service.request.ApproveOfferRequest;
import com.hms.service.request.ReleaseOfferRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateRaiseOfferRequest;
import com.hms.service.service.IOfferDetailsService;
import com.hms.service.wrappers.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/hms/offer-details")
public class OfferDetailsController {

	@Autowired
	private IOfferDetailsService iOfferDetailsService;
	

	@PostMapping("/ready-to-release-list")
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

	@PostMapping("/approve-offer")
	public ResponseEntity<ApiResponse<?>> approveOffer(@RequestBody ApproveOfferRequest request) {
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

	@GetMapping("/dashboard-counts")
	public ResponseEntity<ApiResponse<?>> getOfferDashboardCounts() {
		return ResponseEntity.ok(iOfferDetailsService.getOfferDashboardCounts());
	}

	@PostMapping("/submit-raise-offer-request")
	public ResponseEntity<ApiResponse<?>> submitFinancialApproval(@RequestBody UpdateRaiseOfferRequest request) {
		return ResponseEntity.ok(iOfferDetailsService.submitFinancialApproval(request));
	}

	@GetMapping("/download/offerLetter")
	public void viewOfferLetter(@RequestParam("appId") Integer appId,@RequestParam(value = "action", defaultValue = "view") String action, HttpServletResponse response) {
	iOfferDetailsService.viewOfferLetter(appId, action, response);
	}

	@PostMapping("/get-pending-approvals")
	public ResponseEntity<ApiResponse<?>> getPendingApprovals(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = iOfferDetailsService.getPendingApprovals(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/get-all-pending-approvals")
	public ResponseEntity<ApiResponse<?>> getAllPendingApprovals(@RequestBody SpecificationFilterRequest request) {
		ApiResponse<?> response = iOfferDetailsService.getAllPendingApprovals(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/candidate-response")
	public ResponseEntity<ApiResponse<?>> getOfferNegotiationList(@RequestBody SpecificationFilterRequest request) {

		ApiResponse<?> response = iOfferDetailsService.getOfferNegotiationList(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

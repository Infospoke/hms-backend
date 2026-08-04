package com.hms.service.service;

import com.hms.service.request.ApproveOfferRequest;
import com.hms.service.request.HrRecommendationRequest;
import com.hms.service.request.ReleaseOfferRequest;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateRaiseOfferRequest;
import com.hms.service.wrappers.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;

public interface IOfferDetailsService {

	ApiResponse<?> getReadyToRelease(SpecificationFilterRequest request);

	ApiResponse<?> getOfferDetailsByApplicantId(Integer applicantId);

	ApiResponse<?> getOfferComments(Integer applicantId);

	ApiResponse<?> approveOffer(ApproveOfferRequest request);

	ApiResponse<?> getAllRaiseOfferRequests(SpecificationFilterRequest request);

	ApiResponse<?> releaseOfferLetters(ReleaseOfferRequest request);

	ApiResponse<?> getOfferDashboardCounts();

	ApiResponse<?> submitFinancialApproval(UpdateRaiseOfferRequest request);

	ApiResponse<?> getPendingApprovals(SpecificationFilterRequest request);

	void viewOfferLetter(Integer appId, String action, HttpServletResponse response);

	ApiResponse<?> getAllPendingApprovals(SpecificationFilterRequest request);

	ApiResponse<?> getOfferNegotiationList(SpecificationFilterRequest request);

	ApiResponse<?> getNegotiationDetails(Integer applicantId);

	ApiResponse<?> reviewNegotiationRequest(HrRecommendationRequest request);

	void viewDocument(String filePath, String action, HttpServletResponse response);

}

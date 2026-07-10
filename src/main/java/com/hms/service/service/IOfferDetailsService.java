package com.hms.service.service;

import org.jspecify.annotations.Nullable;

import com.hms.service.request.ApproveOfferRequest;

import com.hms.service.request.ReleaseOfferRequest;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateRaiseOfferRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IOfferDetailsService {

	ApiResponse<?> getReadyToRelease(SpecificationFilterRequest request);

	ApiResponse<?> getOfferDetailsByApplicantId(Integer applicantId);

	ApiResponse<?> getOfferComments(Integer applicantId);

	ApiResponse<?> approveOffer(ApproveOfferRequest request);

	ApiResponse<?> getAllRaiseOfferRequests(SpecificationFilterRequest request);

	ApiResponse<?> releaseOfferLetters(ReleaseOfferRequest request);

	ApiResponse<?> getOfferDashboardCounts();

	ApiResponse<?> UpdateRaiseOffer(UpdateRaiseOfferRequest request);


}

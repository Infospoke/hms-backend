package com.hms.service.service;


import com.hms.service.request.ApproveOfferRequest;

import com.hms.service.request.ReleaseOfferRequest;

import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IOfferDetailsService {

	ApiResponse<?> getReadyToRelease(SpecificationFilterRequest request);


	ApiResponse<?> approveOffer(ApproveOfferRequest request);

	ApiResponse<?> getAllRaiseOfferRequests(SpecificationFilterRequest request);

	ApiResponse<?> releaseOfferLetters(ReleaseOfferRequest request);



}

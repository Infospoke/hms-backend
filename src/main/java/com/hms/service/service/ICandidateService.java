package com.hms.service.service;

import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.request.NegotiateOfferRequest;
import com.hms.service.wrappers.ApiResponse;

public interface ICandidateService {

	ApiResponse<?> createCandidate(CandidateCreationRequest request);


	ApiResponse<?> candidateOffers(String candidateId);


	ApiResponse<?> negotiateOffer(NegotiateOfferRequest request);

}
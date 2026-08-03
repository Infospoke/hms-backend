package com.hms.service.repository;

import java.util.Optional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.OfferDetailsEntity;

@Repository
public interface OfferDetailsRepository
		extends JpaRepository<OfferDetailsEntity, Integer>, JpaSpecificationExecutor<OfferDetailsEntity> {

	List<OfferDetailsEntity> findByJobApplication_IdIn(List<Integer> applicationIds);

	OfferDetailsEntity findTopByJobApplicationOrderByIdDesc(JobApplicationEntity application);

	Optional<OfferDetailsEntity> findByJobApplicationId(Integer applicantId);

	Long countBySubmitFinancialApprovalFalse();

	Long countBySubmitFinancialApprovalTrueAndApproveFalseAndRejectFalse();

	Long countByApproveTrueAndOfferReleasedFalse();
	
	Optional<OfferDetailsEntity> findByJobApplication(JobApplicationEntity jobApplication);

	Optional<OfferDetailsEntity> findByJobApplication_IdAndNegotiationFalse(Integer applicantId);

	Optional<OfferDetailsEntity> findByJobApplication_Id(Integer applicantId);

	Optional<OfferDetailsEntity> findByJobApplication_IdAndNegotiationTrue(Integer applicantId);



}
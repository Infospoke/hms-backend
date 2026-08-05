package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.OfferDetailsEntity;

@Repository
public interface OfferDetailsRepository
		extends JpaRepository<OfferDetailsEntity, Integer>, JpaSpecificationExecutor<OfferDetailsEntity> {

	OfferDetailsEntity findTopByJobApplicationOrderByIdDesc(JobApplicationEntity application);

	Optional<OfferDetailsEntity> findByJobApplicationId(Integer applicantId);

	Optional<OfferDetailsEntity> findByJobApplication(JobApplicationEntity jobApplication);

	Long countByJobApplicationIdInAndOfferReleasedTrue(Iterable<Integer> applicationIds);

	Optional<OfferDetailsEntity> findByJobApplication_IdAndNegotiationFalse(Integer applicantId);

	Optional<OfferDetailsEntity> findByJobApplication_Id(Integer applicantId);

	Optional<OfferDetailsEntity> findByJobApplication_IdAndNegotiationTrue(Integer applicantId);

	@Query("""
			SELECT COUNT(o)
			FROM OfferDetailsEntity o
			WHERE o.offerReleased = true
			AND o.jobApplication.id IN :applicationIds
			""")
	Long countReleasedOffers(@Param("applicationIds") List<Integer> applicationIds);

	@Query("""
			SELECT COUNT(o)
			FROM OfferDetailsEntity o
			WHERE UPPER(o.interviewCompletionStatus) = 'HIRED'
			AND o.submitFinancialApproval = false
			""")
	Long countNewOfferRequests();

	@Query("""
			SELECT COUNT(DISTINCT o.jobApplication.id)
			FROM OfferDetailsEntity o
			WHERE o.submitFinancialApproval = true
			AND o.approve = false
			AND o.reReleaseOfferId IS NULL
			""")
	Long countNewOfferApprovals();

	@Query("""
			SELECT COUNT(o)
			FROM OfferDetailsEntity o
			WHERE o.approve = true
			AND o.offerReleased = false
			AND o.reReleaseOfferId IS NULL
			""")
	Long countPendingRelease();

	@Query("""
			SELECT COUNT(DISTINCT o.reReleaseOfferId)
			FROM OfferDetailsEntity o
			WHERE o.approve = true
			AND o.offerReleased = false
			AND o.reReleaseOfferId IS NOT NULL
			""")
	Long countReRelease();

	@Query("""
			SELECT COUNT(o)
			FROM OfferDetailsEntity o
			WHERE UPPER(o.offerStatus) IN (
			'PENDING',
			'ACCEPTED',
			'REJECTED',
			'EXPIRED',
			'REQUEST FOR NEGOTIATION'
			)
			""")
	Long countCandidateResponses();

	@Query("""
			SELECT COUNT(DISTINCT o.reReleaseOfferId)
			FROM OfferDetailsEntity o
			WHERE o.submitFinancialApproval = true
			AND o.approve = false
			AND o.reReleaseOfferId IS NOT NULL
			""")
	Long countNegotiationApprovals();
	
	Optional<OfferDetailsEntity> findByReReleaseOfferId(Integer reReleaseOfferId);
	
	List<OfferDetailsEntity> findByJobApplication_IdIn(List<Integer> applicationIds);
	
	
	
}

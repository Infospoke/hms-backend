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

	Optional<OfferDetailsEntity> findByJobApplication_Id(Integer applicantId);

	List<OfferDetailsEntity> findByJobApplication_IdIn(List<Integer> applicationIds);

	OfferDetailsEntity findTopByJobApplicationOrderByIdDesc(JobApplicationEntity application);

	Optional<OfferDetailsEntity> findByJobApplicationId(Integer applicantId);

	Long countBySubmitFinancialApprovalFalse();

	Long countBySubmitFinancialApprovalTrueAndApproveFalseAndRejectFalse();

	Long countByApproveTrueAndOfferReleasedFalse();

	Optional<OfferDetailsEntity> findByJobApplication(JobApplicationEntity jobApplication);

	Long countByJobApplicationIdInAndOfferReleasedTrue(Iterable<Integer> applicationIds);

	@Query("""
			SELECT COUNT(o)
			FROM OfferDetailsEntity o
			WHERE o.offerReleased = true
			AND o.jobApplication.id IN :applicationIds
			""")
	Long countReleasedOffers(@Param("applicationIds") List<Integer> applicationIds);
	

}

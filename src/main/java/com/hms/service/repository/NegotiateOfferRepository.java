package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.NegotiationOfferEntity;
import com.hms.service.entity.OfferDetailsEntity;

@Repository

public interface NegotiateOfferRepository
		extends JpaRepository<NegotiationOfferEntity, Integer>, JpaSpecificationExecutor<NegotiationOfferEntity> {

	List<NegotiationOfferEntity> findByIdIn(List<Integer> ids);

	List<OfferDetailsEntity> findByApplicant_IdIn(List<Integer> applicationIds);
	
	Optional<NegotiationOfferEntity> findByApplicant_Id(Integer applicantId);

}

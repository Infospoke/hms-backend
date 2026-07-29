package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.NegotiationOfferEntity;

@Repository

public interface NegotiateOfferRepository
		extends JpaRepository<NegotiationOfferEntity, Integer>, JpaSpecificationExecutor<NegotiationOfferEntity> {

}

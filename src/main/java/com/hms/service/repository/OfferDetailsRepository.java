package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.OfferDetailsEntity;

@Repository
public interface OfferDetailsRepository
		extends JpaRepository<OfferDetailsEntity, Integer>, JpaSpecificationExecutor<OfferDetailsEntity> {

	Optional<OfferDetailsEntity> findByJobApplication_Id(Integer applicantId);

}
package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.service.entity.OfferDetailsChildEntity;

public interface OfferDeatilsChildRepository extends JpaRepository<OfferDetailsChildEntity,Integer>{

	Optional<OfferDetailsChildEntity> findByJobApplication_Id(Integer applicantId);
	
	List<OfferDetailsChildEntity> findByOffer_IdIn(List<Integer> offerIds);

	Optional<OfferDetailsChildEntity> findByOfferId(Integer offerId);

}

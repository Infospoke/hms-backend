package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.OfferLetterTemplateEntity;

@Repository
	public interface OfferLetterTemplateRepository extends JpaRepository<OfferLetterTemplateEntity, Integer> {



}

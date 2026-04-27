package com.hms.service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.OfferEntity;
@Repository

public interface OfferRepository extends JpaRepository<OfferEntity, Integer> {

	Optional<OfferEntity> deleteByCandidateId_Id(Integer candidateId);

	List<OfferEntity> getByCandidateId_Id(Integer id);
	 
	OfferEntity findByIssueDate(LocalDateTime issueDate);

	long countByStatus(String string);
}
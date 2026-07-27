package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.CandidateCreationDetailsEntity;

@Repository
public interface CandidateCreationDetailsRepository extends JpaRepository<CandidateCreationDetailsEntity, Integer> {

	CandidateCreationDetailsEntity findByEmail(String email);

	Optional<CandidateCreationDetailsEntity> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	Optional<CandidateCreationDetailsEntity> findTopByOrderByIdDesc();

	Optional<CandidateCreationDetailsEntity> findByCandidateId(String candidateId);

	Optional<CandidateCreationDetailsEntity> findByCandidateId(Integer candidateId);

	@Query(value = "SELECT nextval('candidate_id_seq')", nativeQuery = true)
	Long getNextCandidateSequence();

	Optional<CandidateCreationDetailsEntity> findByToken(String token);

	boolean existsByCandidateId(String candidateId);
}
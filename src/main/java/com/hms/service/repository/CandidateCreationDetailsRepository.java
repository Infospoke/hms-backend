package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.CandidateCreationDetailsEntity;
@Repository
public interface CandidateCreationDetailsRepository extends JpaRepository<CandidateCreationDetailsEntity,Integer>{

	CandidateCreationDetailsEntity findByEmail(String email);

}

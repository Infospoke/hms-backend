package com.hms.service.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.BGVEntity;


@Repository
public interface BGVRepository extends JpaRepository<BGVEntity, Integer> {

	Optional<BGVEntity> findByCandidateId_Id(Integer candidateId);
	
	Optional<BGVEntity> deleteByCandidateId_Id(Integer candidateId);
	
}

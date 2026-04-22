package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.BusinessJustificationEntity;
@Repository
public interface BusinessJustificationRepository extends JpaRepository<BusinessJustificationEntity,Integer>{

	Optional<BusinessJustificationEntity> findBySrId(String srId);
	
}

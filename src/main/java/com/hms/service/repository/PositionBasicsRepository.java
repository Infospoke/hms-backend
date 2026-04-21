package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hms.service.entity.SRPositionBasicsEntity;

@Repository
public interface PositionBasicsRepository extends JpaRepository<SRPositionBasicsEntity,Integer> {

	Optional<SRPositionBasicsEntity> findBySrId(String srId);
	
	Page<SRPositionBasicsEntity> findAll(Pageable pageable);
	
}

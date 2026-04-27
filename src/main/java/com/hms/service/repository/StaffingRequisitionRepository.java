package com.hms.service.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.SRPositionBasicsEntity;

@Repository
public interface StaffingRequisitionRepository extends JpaRepository<SRPositionBasicsEntity,Integer> {
	
	@Query(value = "SELECT nextval('sr_sequence')", nativeQuery = true)
	Integer getNextSrSequence();
	
}

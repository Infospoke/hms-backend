package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.BudgetAndCompensationEntity;
import com.hms.service.entity.BusinessJustificationEntity;
import com.hms.service.entity.RolesAndRequirementsEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.entity.SourcingStrategyEntity;
@Repository
public interface StaffingRequisitionRepository extends JpaRepository<SRPositionBasicsEntity,Integer> {
	
	@Query(value = "SELECT nextval('sr_sequence')", nativeQuery = true)
	Integer getNextSrSequence();
	
}

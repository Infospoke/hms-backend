package com.hms.service.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ApprovalChainEntity;
@Repository

public interface ApprovalChainRepository
		extends JpaRepository<ApprovalChainEntity, Integer>, JpaSpecificationExecutor<ApprovalChainEntity> {

	Long countByApprovalIgnoreCase(String approval);

	Long countByStatusIgnoreCase(String status);

	ApprovalChainEntity findByChainNameIgnoreCase(String chainName);


	ApprovalChainEntity findByFunctionality(Integer functionalityId);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(String status,
			LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCase(
			String status, String chainName, String approval, Pageable pageable);

	
	



}

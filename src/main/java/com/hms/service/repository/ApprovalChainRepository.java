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

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCaseAndCreatedAtBetween(
			String status, String chainName, String approval, LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndApprovalContainingIgnoreCaseAndCreatedAtBetween(String status,
			String approval, LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCaseAndCreatedAtBetween(
			String chainName, String approval, LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByApprovalContainingIgnoreCaseAndCreatedAtBetween(String approval,
			LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndChainNameContainingIgnoreCaseAndCreatedAtBetween(String status,
			String chainName, LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusContainingIgnoreCaseAndCreatedAtBetween(String status, LocalDate fromDate,
			LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByChainNameContainingIgnoreCaseAndCreatedAtBetween(String chainName,
			LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByCreatedAtBetween(LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCase(
			String status, String chainName, String approval, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndApprovalContainingIgnoreCase(String status, String approval,
			Pageable pageable);

	Page<ApprovalChainEntity> findByChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCase(String chainName,
			String approval, Pageable pageable);

	Page<ApprovalChainEntity> findByApprovalContainingIgnoreCase(String approval, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndChainNameContainingIgnoreCase(String status, String chainName,
			Pageable pageable);

	Page<ApprovalChainEntity> findByStatusContainingIgnoreCase(String status, Pageable pageable);

	Page<ApprovalChainEntity> findByChainNameContainingIgnoreCase(String chainName, Pageable pageable);

	ApprovalChainEntity findByChainNameIgnoreCase(String chainName);


}

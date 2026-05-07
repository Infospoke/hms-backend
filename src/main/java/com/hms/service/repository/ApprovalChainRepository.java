package com.hms.service.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.ApprovalsChildEntity;

@Repository

public interface ApprovalChainRepository
		extends JpaRepository<ApprovalChainEntity, Integer>, JpaSpecificationExecutor<ApprovalChainEntity> {

	Long countByApprovalIgnoreCase(String approval);

	Long countByStatusIgnoreCase(String status);

	Page<ApprovalChainEntity> findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
	        LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusContainingIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
	        String status, LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByChainNameContainingIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
	        String chainName, LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByApprovalContainingIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
	        String approval, LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndChainNameContainingIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
	        String status, String chainName, LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndApprovalContainingIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
	        String status, String approval, LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
	        String chainName, String approval, LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
	        String status, String chainName, String approval, LocalDate fromDate, LocalDate toDate, Pageable pageable);

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

	ApprovalChainEntity findByFunctionality(Integer functionalityId);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(String status,
			LocalDate fromDate, LocalDate toDate, Pageable pageable);

	Page<ApprovalChainEntity> findByStatusIgnoreCaseAndChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCase(
			String status, String chainName, String approval, Pageable pageable);


}

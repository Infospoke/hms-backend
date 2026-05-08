package com.hms.service.repository;

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

}

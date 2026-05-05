package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ApprovalChainEntity;

@Repository
public interface ApprovalChainRepository  extends JpaRepository<ApprovalChainEntity,Integer>{

	ApprovalChainEntity findByChainNameIgnoreCase(String chainName);

}

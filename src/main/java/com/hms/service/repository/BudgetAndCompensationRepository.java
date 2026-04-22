package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.BudgetAndCompensationEntity;
@Repository
public interface BudgetAndCompensationRepository extends JpaRepository<BudgetAndCompensationEntity,Integer> {

	Optional<BudgetAndCompensationEntity> findBySrId(String srId);
	
}

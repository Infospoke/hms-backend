package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.BudgetAndCompensationEntity;
@Repository
public interface BudgetAndCompensationRepository extends JpaRepository<BudgetAndCompensationEntity,Integer> {

}

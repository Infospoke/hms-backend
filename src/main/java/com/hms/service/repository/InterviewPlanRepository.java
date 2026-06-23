package com.hms.service.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewPlanEntity;

@Repository
public interface InterviewPlanRepository extends JpaRepository<InterviewPlanEntity,Integer>,JpaSpecificationExecutor<InterviewPlanEntity> { 

	Long countByStatus(String status);

	long countByStatusIsNull();

    List<InterviewPlanEntity> findByPlanNameContainingIgnoreCase(String planName);
    
    Optional<InterviewPlanEntity> findByPlanId(Integer planId);
}

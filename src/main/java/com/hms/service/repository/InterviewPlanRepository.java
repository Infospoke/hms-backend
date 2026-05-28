package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewPlanEntity;

@Repository
public interface InterviewPlanRepository extends JpaRepository<InterviewPlanEntity,Integer>,JpaSpecificationExecutor<InterviewPlanEntity> { 

	Long countByStatus(String status);

}

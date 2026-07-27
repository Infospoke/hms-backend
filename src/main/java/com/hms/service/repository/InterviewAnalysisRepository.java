package com.hms.service.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewAnalysisEntity;
import com.hms.service.entity.InterviewRoundDropDownEntity;

@Repository

public interface InterviewAnalysisRepository extends JpaRepository<InterviewAnalysisEntity,Integer>{

	long countByJobId(Integer jobId);

	boolean existsByApplicationId(Integer applicationId);

	

}

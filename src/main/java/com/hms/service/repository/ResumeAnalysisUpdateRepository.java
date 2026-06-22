package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ApplicanDetailsEntity;

@Repository
public interface ResumeAnalysisUpdateRepository extends JpaRepository<ApplicanDetailsEntity,Integer> {

	Optional<ApplicanDetailsEntity> findByApplicationId(Integer applicationId);

}

package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ResumeAnalysisUpdateEntity;

@Repository
public interface ResumeAnalysisUpdateRepository extends JpaRepository<ResumeAnalysisUpdateEntity,Integer> {

	Optional<ResumeAnalysisUpdateEntity> findByApplicationId(Integer applicationId);

}

package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.AIInterviewQuestionsEntity;

@Repository
public interface AInterviewQuestionsRepository extends JpaRepository<AIInterviewQuestionsEntity, Long> {

	Optional<AIInterviewQuestionsEntity> findByApplicationId(Integer applicationId);

}

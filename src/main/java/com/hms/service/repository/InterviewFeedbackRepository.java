package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewFeedbackEntity;

@Repository
public interface InterviewFeedbackRepository extends JpaRepository<InterviewFeedbackEntity, Integer> {

	Integer countByApplicantId(Integer applicantId);
	
    List<InterviewFeedbackEntity> findByApplicantId(Integer applicantId);
}

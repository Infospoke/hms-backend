package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.AIInterviewQuestionsEntity;

@Repository
public interface AInterviewQuestionsRepository extends JpaRepository<AIInterviewQuestionsEntity, Long> {

	Optional<AIInterviewQuestionsEntity> findByApplicationId(Integer applicationId);

	@Query("""
			    SELECT COUNT(i)
			    FROM InterviewSessionEntity i
			    JOIN ResumeAnalysisEntity r
			        ON r.applicationId = i.applicationId
			    WHERE LOWER(r.status) = 'shortlisted'
			      AND i.moveToSchedule = false
			      AND (i.isDeleted = false OR i.isDeleted IS NULL)
			""")
	Long countGenerateAIQuestions();

	@Query("""
			    SELECT COUNT(i)
			    FROM InterviewSessionEntity i
			    WHERE i.moveToSchedule = true
			      AND i.isScheduled = false
			      AND (i.isDeleted = false OR i.isDeleted IS NULL)
			""")
	Long countScheduleAIInterview();

	@Query("""
			    SELECT COUNT(i)
			    FROM InterviewSessionEntity i
			    WHERE i.isScheduled = true
			      AND LOWER(i.status) = 'upcoming'
			      AND i.interviewScheduledDatetime > CURRENT_TIMESTAMP
			      AND (i.isDeleted = false OR i.isDeleted IS NULL)
			""")
	Long countUpcomingAIInterview();

}

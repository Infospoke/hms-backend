package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewQuestionsEntity;


@Repository
public interface InterviewQuestionsRepository extends JpaRepository<InterviewQuestionsEntity, Integer> {

	void deleteByJobId(Integer jobId);

	@Query("SELECT iq FROM InterviewQuestionsEntity iq " + "JOIN QuestionEntity q ON iq.questionId = q.questionId "
			+ "WHERE iq.jobId = :jobId AND q.skillId = :skillId")
	InterviewQuestionsEntity findByJobIdAndSkillId(@Param("jobId") int jobId, @Param("skillId") int skillId);
}

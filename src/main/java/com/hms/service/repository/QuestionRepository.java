package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.QuestionEntity;



@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {

	@Query("FROM QuestionEntity q WHERE q.skillId = :skillId AND q.experienceLevel <= :experience")
	List<QuestionEntity> findQuestionsForSkill(@Param("skillId") Integer skillId,
			@Param("experience") Integer experience);

	@Query("SELECT q FROM QuestionEntity q " + "WHERE q.skillId = :skillId "
			+ "AND q.experienceLevel = :experienceLevel " + "AND q.questionWeightage = :weightage")
	List<QuestionEntity> findExactQuestions(@Param("skillId") int skillId,
			@Param("experienceLevel") int experienceLevel, @Param("weightage") int weightage);

}
package com.hms.service.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewRoundEntity;


@Repository
public interface InterviewRoundRepository extends JpaRepository<InterviewRoundEntity, Integer> {

	List<InterviewRoundEntity> findByInterviewPlan_IdOrderByRoundOrderAsc(Integer interviewPlanId);

	Optional<InterviewRoundEntity> findById(Long id);

	List<InterviewRoundEntity> findByIdIn(List<Integer> roundIds);
	

}

package com.hms.service.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hms.service.entity.InterviewRoundEntity;

@Repository
public interface InterviewRoundRepository extends JpaRepository<InterviewRoundEntity, Integer> {

	List<InterviewRoundEntity> findByInterviewPlan_IdOrderByRoundOrderAsc(Integer interviewPlanId);

	Optional<InterviewRoundEntity> findById(Long id);

	List<InterviewRoundEntity> findByStageTypeIdIn(List<Integer> stageTypeIds);

	List<InterviewRoundEntity> findByInterviewPlanId(Integer planId);

	List<InterviewRoundEntity> findByInterviewPlan_Id(int planId);

	InterviewRoundEntity findByInterviewPlan_IdAndStageTypeId(Integer interviewPlanId, Integer stageTypeId);

	Integer findByInterviewPlanIdAndStageType(Integer planId, Integer currentStageType);

	InterviewRoundEntity findByInterviewPlan_IdAndRoundOrder(int planId, int i);

	List<InterviewRoundEntity> findByInterviewPlan_IdIn(Collection<Integer> planIds);
	
	Optional<InterviewRoundEntity> findByStageTypeId(Integer stageTypeId);


}

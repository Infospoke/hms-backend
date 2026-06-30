package com.hms.service.repository;


import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewScheduleEntity;

@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewScheduleEntity,Integer> {

	InterviewScheduleEntity findByApplicantIdAndInterviewDate(Integer applicationId, LocalDate now);
	 


}

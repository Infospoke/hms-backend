package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hms.service.entity.InterviewCandidateDetailsEntity;

public interface InterviewCandidateDetailsRepository extends JpaRepository<InterviewCandidateDetailsEntity, Integer>,JpaSpecificationExecutor<InterviewCandidateDetailsEntity>{

}

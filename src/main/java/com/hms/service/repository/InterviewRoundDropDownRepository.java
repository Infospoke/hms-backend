package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewRoundDropDownEntity;
@Repository
public interface InterviewRoundDropDownRepository extends JpaRepository<InterviewRoundDropDownEntity, Integer>{

}

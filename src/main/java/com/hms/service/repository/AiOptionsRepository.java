package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.AIOptionsEntity;
@Repository

public interface AiOptionsRepository extends JpaRepository<AIOptionsEntity, Integer> {

}

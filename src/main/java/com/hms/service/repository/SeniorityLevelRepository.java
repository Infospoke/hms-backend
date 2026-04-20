package com.hms.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hms.service.entity.SeniorityLevelEntity;

@Repository
public interface SeniorityLevelRepository extends JpaRepository<SeniorityLevelEntity, Long>{

}

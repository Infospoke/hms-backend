package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ApprovalsChildEntity;

@Repository
public interface ApprovalsChildRepository extends JpaRepository<ApprovalsChildEntity,Integer>{

	Optional<ApprovalsChildEntity> findByProcessId(String finalSrId);

	

}

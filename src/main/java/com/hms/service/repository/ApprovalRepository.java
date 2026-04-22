package com.hms.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hms.service.entity.ApprovalEntity;
import com.hms.service.entity.SRPositionBasicsEntity;

public interface ApprovalRepository extends JpaRepository<ApprovalEntity, Integer> {

	Optional<ApprovalEntity> findBySrId(String srId);

}

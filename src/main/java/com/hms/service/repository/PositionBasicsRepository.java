package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.response.JrResponse;

@Repository
public interface PositionBasicsRepository extends JpaRepository<SRPositionBasicsEntity,Integer> ,JpaSpecificationExecutor<SRPositionBasicsEntity>{

	Optional<SRPositionBasicsEntity> findBySrId(String srId);

	@Query("SELECT p.srId AS srId, p.jobTitle AS jobTitle " +
		       "FROM SRPositionBasicsEntity p WHERE p.approved = true")
		List<JrResponse> findApprovedJrDetails();
	
	Page<SRPositionBasicsEntity> findByUserId(Long userId, Pageable pageable);

	List<SRPositionBasicsEntity> findAll();
	
	List<SRPositionBasicsEntity> findBySrIdIn(List<String> srIds);
}

package com.hms.service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hms.service.entity.SourcingStrategyEntity;

@Repository
public interface SourceStrategyRepository extends JpaRepository<SourcingStrategyEntity,Integer>  {

	Optional<SourcingStrategyEntity> findBySrId(String srId);
	
}

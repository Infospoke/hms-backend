package com.hms.service.repository;


import java.util.Optional;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hms.service.entity.FunctionalityEntity;



@Repository
public interface FunctionalityRepository extends JpaRepository<FunctionalityEntity, Integer> {

	List<FunctionalityEntity> findByIsChaincreatedFalse();
	
	Optional<FunctionalityEntity> findByFunctionalityName(String name);
	
	Long countByIsChaincreatedTrue();

}

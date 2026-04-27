package com.hms.service.repository;

 
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.PreOnBoardingEntity;

@Repository

public interface PreOnBoardingRepository extends JpaRepository<PreOnBoardingEntity, Integer>{
 
	@Query(value = "select count(p) from PreOnBoardingEntity p where p.id = :id")
	int findByPreId(@Param("id") Integer id);
	Optional<PreOnBoardingEntity> findByCandidateId_Id(Integer candidateId);
	void deleteByCandidateId_Id(Integer candidateId);
	
	@Query("""
			    SELECT COUNT(c)
			    FROM CandidateInfoEntity c
			    WHERE c.status = :status
			      AND c.createdDate BETWEEN :from AND :to

			""")
	
	long countByStatusAndDate(@Param("status") String status, @Param("from") LocalDateTime from,
			@Param("to") LocalDateTime to);

}
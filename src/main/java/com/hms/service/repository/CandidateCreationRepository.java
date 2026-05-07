package com.hms.service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.CandidateInfoEntity;

@Repository
public interface CandidateCreationRepository extends JpaRepository<CandidateInfoEntity, Integer> {

	@Query("""
			    SELECT COUNT(c)
			    FROM CandidateInfoEntity c
			    WHERE c.createdDate BETWEEN :from AND :to

			""")
	long totalCandidates(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
	@Query("""
		    SELECT count(c)
		    FROM CandidateInfoEntity c
		    WHERE c.email =:email
 
		""")
	Integer findByEmail(@Param("email") String email);

	
	@Query("SELECT c.applicationId, c.status FROM CandidateInfoEntity c WHERE c.applicationId IN :ids")
	List<Object[]> findStatusByApplicationIds(@Param("ids") List<Integer> applicationIds);
	
	long countByJobIdAndStatusNotIgnoreCase(Integer jobId, String status);
	long countByJobIdAndStatusIgnoreCase(Integer jobId, String status);
	

}

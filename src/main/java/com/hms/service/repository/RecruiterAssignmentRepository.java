package com.hms.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.RecruiterAssignmentEntity;

@Repository
public interface RecruiterAssignmentRepository extends JpaRepository<RecruiterAssignmentEntity, Integer> {

	Long countByIdIsNotNull();

	@Query("""
			SELECT

			SUM(
			    CASE
			        WHEN UPPER(r.status) = 'ACCEPTED'
			        THEN 1
			        ELSE 0
			    END
			),

			SUM(
			    CASE
			        WHEN UPPER(r.status) = 'DECLINED'
			        THEN 1
			        ELSE 0
			    END
			),

			SUM(
			    CASE
			        WHEN UPPER(r.status) = 'PENDING'
			        THEN 1
			        ELSE 0
			    END
			)

			FROM RecruiterAssignmentEntity r
			""")
	Object getStatusCounts();

	List<RecruiterAssignmentEntity> findByJobId(Integer id);

}

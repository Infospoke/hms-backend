package com.hms.service.repository;


import org.springframework.data.domain.Sort.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.RecruiterAssignmentEntity;

@Repository
public interface RecruiterAssignmentRepository
		extends JpaRepository<RecruiterAssignmentEntity, Integer>, JpaSpecificationExecutor<RecruiterAssignmentEntity> {

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

	Long countByUserId(Integer userId);

	Long countByUserIdAndStatus(Integer userId, String status);

	@Query("""
			    SELECT ra.userId, COUNT(ra.id)
			    FROM RecruiterAssignmentEntity ra
			    WHERE ra.userId IN :userIds
			    GROUP BY ra.userId
			""")
	List<Object[]> findAssignmentCounts(@Param("userIds") List<Integer> userIds);

	@Query("""
			SELECT r.status, COUNT(r)
			FROM RecruiterAssignmentEntity r
			WHERE r.userId = :userId
			GROUP BY r.status
			""")
	List<Object[]> getStatusCountsByUserId(@Param("userId") Integer userId);




	List<Integer> findJobIdsByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") String status);

	List<Integer> findJobIdsByUserId(@Param("userId") Integer userId);

	  RecruiterAssignmentEntity findByJobIdAndUserId(
	            Integer jobId,
	            Integer userId
	    );

	    List<RecruiterAssignmentEntity> findAllByUserId(
	            Integer userId
	    );

	    List<RecruiterAssignmentEntity> findAllByUserIdAndStatusIgnoreCase(
	            Integer userId,
	            String status
	    );
	    RecruiterAssignmentEntity findRecuirtersByJobId(Integer jobId);

	Page<RecruiterAssignmentEntity> findByJobId(Integer jobId, Pageable pageable);


}

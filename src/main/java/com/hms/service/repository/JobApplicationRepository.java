package com.hms.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.JobApplicationEntity;

@Repository

public interface JobApplicationRepository extends JpaRepository<JobApplicationEntity, Integer> {

	int countByJobId(Integer jobId);

	List<JobApplicationEntity> findByJobIdInOrderByStageEntryDateDesc(List<Integer> jobIds);

	List<JobApplicationEntity> findByJobIdOrderByCreatedDateDesc(Integer jobId);

	Optional<JobApplicationEntity> findById(Integer id);

	@Query(value = """
			SELECT
			    (SELECT COUNT(*)
			     FROM tb_job_applications
			     WHERE in_person_interviews = TRUE),

			    (SELECT COUNT(DISTINCT application_id)
			     FROM tb_interview_sessions
			     WHERE UPPER(status) = 'COMPLETED'),

			    (SELECT COUNT(DISTINCT cs.application_id)
			     FROM tb_interview_current_stage cs
			     INNER JOIN tb_interview_round_dropdown rd
			            ON cs.current_stage_type = rd.id
			     WHERE cs.interview_completed = TRUE
			       AND LOWER(rd.round_name) = 'techincal round'),

			    (SELECT COUNT(DISTINCT cs.application_id)
			     FROM tb_interview_current_stage cs
			     INNER JOIN tb_interview_round_dropdown rd
			            ON cs.current_stage_type = rd.id
			     WHERE cs.interview_completed = TRUE
			       AND LOWER(rd.round_name) = 'mangerial round'),

			    (SELECT COUNT(DISTINCT cs.application_id)
			     FROM tb_interview_current_stage cs
			     INNER JOIN tb_interview_round_dropdown rd
			            ON cs.current_stage_type = rd.id
			     WHERE cs.interview_completed = TRUE
			       AND LOWER(rd.round_name) = 'hr round')
			""", nativeQuery = true)
	List<Object[]> getInterviewDashboard();

	@Query("""
			 SELECT j.jobId, COUNT(j)
			 FROM JobApplicationEntity j
			 GROUP BY j.jobId
			""")
	List<Object[]> getApplicationCountByJobId();
}

package com.hms.service.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.CandidateCreationDetailsEntity;
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

	List<JobApplicationEntity> findByIdIn(Collection<Integer> ids);

	Optional<JobApplicationEntity> findByPhNoAndEmailAndJobId(String phNo, String email, Integer jobId);

	@Query("""
			SELECT j.id
			FROM JobApplicationEntity j
			WHERE j.candidate.candidateId = :candidateId
			""")
	List<Integer> findApplicantIdsByCandidateId(@Param("candidateId") String candidateId);

	List<JobApplicationEntity> findByCandidate(CandidateCreationDetailsEntity candidate);

	List<JobApplicationEntity> findByCandidate_CandidateId(String candidateId);

	boolean existsByCandidate_CandidateId(String candidateId);

	Optional<JobApplicationEntity> findByCandidate_CandidateIdAndJobId(String candidateId, Integer jobId);

	boolean existsByCandidate_CandidateIdAndJobId(String candidateId, Integer jobId);

	List<JobApplicationEntity> findByCandidateCandidateIdOrderByCreatedDateDesc(String candidateId);

	List<JobApplicationEntity> findByRecruiterId(Integer recruiterId);

	@Query("""
			    SELECT j
			    FROM JobApplicationEntity j
			    WHERE j.recruiterId = :recruiterId
			      AND j.jobId = :jobId
			      AND j.createdDate BETWEEN :fromDate AND :toDate
			""")
	List<JobApplicationEntity> findRecruiterApplicationsByDate(@Param("recruiterId") Integer recruiterId,
			@Param("jobId") Integer jobId, @Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	List<JobApplicationEntity> findByRecruiterIdAndJobId(Integer recruiterId, Integer jobId);

	Long countByJobIdAndInPersonInterviewsTrue(Integer jobId);

	List<JobApplicationEntity> findByJobId(Integer jobId);

	List<JobApplicationEntity> findByJobIdAndCreatedDateBetween(Integer jobId, LocalDateTime fromDate,
			LocalDateTime toDate);

}

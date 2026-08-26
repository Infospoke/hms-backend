package com.hms.service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewScheduleEntity;

@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewScheduleEntity, Integer>, JpaSpecificationExecutor<InterviewScheduleEntity> {

	@Query(value = """
			SELECT
			    j.job_title,
			    d.department_name,
			    ir.stage_name,
			    ir.interview_mode,
			    ir.stage_type,
			    j.employment_type,
			    CONCAT(j.location, ', ', j.country),
			    j.work_mode,
			    CONCAT(j.min_experience, ' - ', j.max_experience, ' Years'),
			    ad.name,
			    ad.email,
			    ad.phone_no,
			    ad.current_company,
			    ad.current_location,
			    ad.total_experience,
			    ad.notice_period,
			    rd.round_name,
			    cs.interview_completed_on,
			    bc.proposed_total_compensation,
			    cs.current_stage_type,
			    j.job_id,
			    ja.candidate_id
			    

			FROM tb_job_applications ja

			INNER JOIN tb_applicant_details ad
			    ON ad.application_id = ja.id

			INNER JOIN tb_create_job_details j
			    ON j.job_id = ja.job_id

			LEFT JOIN tb_budget_compensation bc
			    ON bc.sr_id = j.sr_id

			LEFT JOIN tb_departments d
			    ON d.id = j.department

			INNER JOIN tb_interview_plan ip
			    ON ip.id = j.plan_id

			LEFT JOIN tb_interview_current_stage cs
			    ON cs.id = (
			        SELECT MAX(cs1.id)
			        FROM tb_interview_current_stage cs1
			        WHERE cs1.application_id = ja.id
			    )

			LEFT JOIN tb_interview_round ir
			    ON ir.interview_plan_id = ip.id
			   AND ir.stage_type_id = cs.current_stage_type

			LEFT JOIN tb_interview_round_dropdown rd
			    ON rd.id = cs.current_stage_type
			    
			    

			WHERE ja.id = :applicationId
			""", nativeQuery = true)
	List<Object[]> getInterviewSummary(@Param("applicationId") Integer applicationId);

	List<InterviewScheduleEntity> findByInterviewDateAfter(LocalDate now);

	List<InterviewScheduleEntity> findByApplicantId(Integer applicantId);

	Optional<InterviewScheduleEntity> findTopByApplicantIdOrderByIdDesc(Integer applicantId);

	InterviewScheduleEntity findByApplicantIdAndInterviewDateAndRoundId(Integer applicantId, LocalDate now,
			Integer currentStageId);
	
	Optional<InterviewScheduleEntity> findByApplicantIdAndRoundId(Integer applicantId, Integer roundId);

}

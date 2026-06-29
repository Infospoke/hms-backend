package com.hms.service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hms.service.entity.InterviewScheduleEntity;

@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewScheduleEntity, Integer> {

	long countByUserId(Integer userId);

	InterviewScheduleEntity findByApplicantIdAndInterviewDate(Integer applicationId, LocalDate now);

	@Query(value = """
			SELECT

			    s.id,
			    ja.id,
			    CONCAT(ja.first_name,' ',ja.last_name) AS candidateName,
			    j.job_title,
			    d.department_name,
			    ir.stage_name,
			    ir.interview_mode,

			    cs.round_order,

			    (
			        SELECT COUNT(*)
			        FROM tb_interview_round ir2
			        WHERE ir2.interview_plan_id = ir.interview_plan_id
			    ) AS totalRounds,

			    s.interview_date,
			    s.start_time,
			    s.end_time,
			    s.meeting_link,
			    s.venue_details

			FROM tb_interview_schedule s

			INNER JOIN tb_job_applications ja
			        ON ja.id = s.applicant_id

			INNER JOIN tb_create_job_details j
			        ON j.job_id = ja.job_id

			LEFT JOIN tb_departments d
			       ON d.id = j.department

			INNER JOIN tb_interview_round ir
			       ON ir.id = s.round_id

			INNER JOIN tb_interview_current_stage cs
			       ON cs.application_id = ja.id

			WHERE

			(
			    :search = ''
			    OR LOWER(CONCAT(ja.first_name,' ',ja.last_name))
			       LIKE LOWER(CONCAT('%',:search,'%'))
			    OR LOWER(j.job_title)
			       LIKE LOWER(CONCAT('%',:search,'%'))
			)

			AND
			(
			    :departmentId = 0
			    OR d.id = :departmentId
			)

			AND
			(
			    :roundId = 0
			    OR ir.id = :roundId
			)

			AND
			(
			    :interviewMode = ''
			    OR LOWER(ir.interview_mode)=LOWER(:interviewMode)
			)

			AND
			(
			    :interviewDate = DATE '1900-01-01'
			    OR s.interview_date = :interviewDate
			)
			""", countQuery = """
			SELECT COUNT(*)

			FROM tb_interview_schedule s

			INNER JOIN tb_job_applications ja
			        ON ja.id = s.applicant_id

			INNER JOIN tb_create_job_details j
			        ON j.job_id = ja.job_id

			LEFT JOIN tb_departments d
			       ON d.id = j.department

			INNER JOIN tb_interview_round ir
			       ON ir.id = s.round_id

			INNER JOIN tb_interview_current_stage cs
			       ON cs.application_id = ja.id

			WHERE

			(
			    :search = ''
			    OR LOWER(CONCAT(ja.first_name,' ',ja.last_name))
			       LIKE LOWER(CONCAT('%',:search,'%'))
			    OR LOWER(j.job_title)
			       LIKE LOWER(CONCAT('%',:search,'%'))
			)

			AND
			(
			    :departmentId = 0
			    OR d.id = :departmentId
			)

			AND
			(
			    :roundId = 0
			    OR ir.id = :roundId
			)

			AND
			(
			    :interviewMode = ''
			    OR LOWER(ir.interview_mode)=LOWER(:interviewMode)
			)

			AND
			(
			    :interviewDate = DATE '1900-01-01'
			    OR s.interview_date = :interviewDate
			)
			""", nativeQuery = true)
	Page<Object[]> getInterviewSchedules(

			@Param("search") String search,

			@Param("departmentId") Integer departmentId,

			@Param("roundId") Integer roundId,

			@Param("interviewMode") String interviewMode,

			@Param("interviewDate") LocalDate interviewDate,

			Pageable pageable);

	@Query(value = """
			SELECT

			  j.job_title,

			  d.department_name,

			  ir.stage_name,

			  ir.interview_mode,

			  ir.stage_name,

			  j.employment_type,

			  j.location || ', ' || j.country,

			  j.work_mode,

			  j.min_experience || ' - ' || j.max_experience || ' Years',

			  ad.name,

			  ad.email,

			  ad.phone_no,

			  ad.current_company,

			  ad.current_location,

			  ad.total_experience,

			  ad.notice_period,

			  rd.round_name

					    FROM tb_interview_schedule s

					    INNER JOIN tb_job_applications ja
					        ON ja.id=s.applicant_id

					    INNER JOIN tb_applicant_details ad
					        ON ad.application_id=ja.id

					    INNER JOIN tb_create_job_details j
					        ON j.job_id=ja.job_id

					    LEFT JOIN tb_departments d
					        ON d.id=j.department

					    INNER JOIN tb_interview_round ir
					        ON ir.id=s.round_id

					    LEFT JOIN tb_interview_current_stage cs
					        ON cs.application_id=ja.id

					    LEFT JOIN tb_interview_round_dropdown rd
					        ON rd.id=cs.current_stage_type

					    WHERE s.id=:scheduleId

					    """, nativeQuery = true)
	List<Object[]> getInterviewSummary(@Param("scheduleId") Integer scheduleId);
}

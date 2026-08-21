package com.hms.service.request;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hms.service.entity.AgencyDetailsEntity;
import com.hms.service.entity.ApplicanDetailsEntity;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.AssignRolesEntity;
import com.hms.service.entity.CandidateCreationDetailsEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.InterviewCurrentStageEntity;
import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewScheduleEntity;
import com.hms.service.entity.InterviewSessionEntity;
import com.hms.service.entity.InterviewerAssignmentEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.NegotiationOfferEntity;
import com.hms.service.entity.NotificationEngineEntity;
import com.hms.service.entity.OfferDetailsChildEntity;
import com.hms.service.entity.OfferDetailsEntity;
import com.hms.service.entity.RecruiterAssignmentEntity;
import com.hms.service.entity.ResumeAnalysisEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.repository.InterviewPlanRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data

@NoArgsConstructor

@AllArgsConstructor

@JsonIgnoreProperties(ignoreUnknown = true)

public class SpecificationFilterRequest {

	private Integer page = 0;

	private Integer size = 10;

	private String sortBy = "id";

	private String direction = "DESC";

	private String status;

	private Map<String, Object> filters;

	public String getFilter(String key) {

		if (filters == null || !filters.containsKey(key)) {

			return null;

		}

		String value = filters.get(key).toString().trim();

		return value.isBlank() ? null : value;

	}

	private LocalDate[] getDateRange() {

		if (filters == null || !filters.containsKey("dateFilter")) {

			return null;

		}

		String dateFilter = getFilter("dateFilter");

		if (dateFilter == null) {

			return null;

		}

		dateFilter = dateFilter.replace("_", "").toUpperCase();

		LocalDate today = LocalDate.now();

		LocalDate fromDate = null;

		LocalDate toDate = null;

		switch (dateFilter) {

		case "TODAY":

			fromDate = today;

			toDate = today.plusDays(1);

			break;

		case "THISWEEK":

			fromDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.SUNDAY);

			toDate = today.plusDays(1);

			break;

		case "THISMONTH":

			fromDate = LocalDate.now().minusMonths(0).withDayOfMonth(1);

			toDate = today.plusDays(1);

			break;

		case "CUSTOM":

			if (filters.containsKey("fromDate") && filters.containsKey("toDate")) {

				fromDate = LocalDate.parse(filters.get("fromDate").toString());

				toDate = LocalDate.parse(filters.get("toDate").toString()).plusDays(1);

			}

			break;

		}

		if (fromDate == null || toDate == null) {

			return null;

		}

		return new LocalDate[] { fromDate, toDate };

	}

	private <T> Specification<T> likeSpec(

			String field,

			String value

	) {

		return (root, query, cb) ->

		cb.like(

				cb.lower(root.get(field)),

				"%" + value.toLowerCase() + "%"

		);

	}

	private <T> Specification<T> equalSpec(

			String field,

			String value

	) {

		return (root, query, cb) ->

		cb.equal(

				cb.lower(root.get(field)),

				value.toLowerCase()

		);

	}

	private <T> Specification<T> dateSpec(

			String field

	) {

		LocalDate[] dates = getDateRange();

		if (dates == null) {

			return null;

		}

		return (root, query, cb) -> {

			Predicate fromPredicate =

					cb.greaterThanOrEqualTo(

							root.get(field),

							dates[0]

					);

			Predicate toPredicate =

					cb.lessThan(

							root.get(field),

							dates[1]

					);

			return cb.and(fromPredicate, toPredicate);

		};

	}

	public Specification<ApprovalChainEntity> buildBaseSpec() {

		Specification<ApprovalChainEntity> spec =

				Specification.allOf();

		String approval = getFilter("approval");

		if (approval != null) {

			spec = spec.and(equalSpec("approval", approval));

		}

		String status = getFilter("status");

		if (status != null) {

			spec = spec.and(equalSpec("status", status));

		}

		String chainName = getFilter("chainName");

		if (chainName != null) {

			spec = spec.and(likeSpec("chainName", chainName));

		}

		String functionalityName = getFilter("functionalityName");

		if (functionalityName != null) {

			spec = spec.and(likeSpec("functionalityName", functionalityName));

		}

		String search = getFilter("search");

		if (search != null) {

			spec = spec.and((r, q, c) -> c.or(

					c.like(

							c.lower(r.get("chainName")),

							"%" + search.toLowerCase() + "%"

					),

					c.like(

							c.lower(r.get("description")),

							"%" + search.toLowerCase() + "%"

					),

					c.like(

							c.lower(r.get("approval")),

							"%" + search.toLowerCase() + "%"

					),

					c.like(

							c.lower(r.get("status")),

							"%" + search.toLowerCase() + "%"

					)

			));

		}

		Specification<ApprovalChainEntity> dateSpec =

				dateSpec("createdAt");

		if (dateSpec != null) {

			spec = spec.and(dateSpec);

		}

		return spec;

	}

	public Specification<ApprovalChainEntity> buildCountSpec() {

		Specification<ApprovalChainEntity> spec =

				Specification.allOf();

		String chainName = getFilter("chainName");

		if (chainName != null) {

			spec = spec.and(likeSpec("chainName", chainName));

		}

		String functionalityName = getFilter("functionalityName");

		if (functionalityName != null) {

			spec = spec.and(likeSpec("functionalityName", functionalityName));

		}

		String search = getFilter("search");

		if (search != null) {

			spec = spec.and((r, q, c) -> c.or(

					c.like(

							c.lower(r.get("chainName")),

							"%" + search.toLowerCase() + "%"

					),

					c.like(

							c.lower(r.get("description")),

							"%" + search.toLowerCase() + "%"

					)

			));

		}

		Specification<ApprovalChainEntity> dateSpec =

				dateSpec("createdAt");

		if (dateSpec != null) {

			spec = spec.and(dateSpec);

		}

		return spec;

	}

	public Specification<NotificationEngineEntity>

			toNotificationSpecification() {

		Specification<NotificationEngineEntity> spec =

				Specification.allOf();

		if (filters != null && filters.containsKey("isRead")) {

			Boolean isRead =

					Boolean.parseBoolean(

							filters.get("isRead").toString()

					);

			spec = spec.and(

					(r, q, c) ->

					c.equal(r.get("isRead"), isRead)

			);

		}

		String search = getFilter("search");

		if (search != null) {

			spec = spec.and(

					likeSpec("notificationTitle", search)

			);

		}

		Specification<NotificationEngineEntity> dateSpec =

				dateSpec("notificationSentAt");

		if (dateSpec != null) {

			spec = spec.and(dateSpec);

		}

		return spec;

	}

	public Specification<NotificationEngineEntity>

			buildNotificationCountSpec() {

		Specification<NotificationEngineEntity> spec =

				Specification.allOf();

		String search = getFilter("search");

		if (search != null) {

			spec = spec.and(

					likeSpec("notificationTitle", search)

			);

		}

		Specification<NotificationEngineEntity> dateSpec =

				dateSpec("notificationSentAt");

		if (dateSpec != null) {

			spec = spec.and(dateSpec);

		}

		return spec;

	}

	public Specification<SRPositionBasicsEntity>

			toSrApprovalSpecification(

					List<String> srIds

	) {

		Specification<SRPositionBasicsEntity> spec =

				Specification.allOf();

		spec = spec.and(

				(r, q, c) -> r.get("srId").in(srIds)

		);

		String search = getFilter("search");

		if (search != null) {

			spec = spec.and((r, q, c) -> c.or(

					c.like(

							c.lower(r.get("jobTitle")),

							"%" + search.toLowerCase() + "%"

					)

			));

		}

		String status = getFilter("status");

		if (status != null) {

			switch (status.toLowerCase()) {

			case "completed":

				spec = spec.and(

						(r, q, c) ->

						c.equal(r.get("approved"), true)

				);

				break;

			case "rejected":

				spec = spec.and(

						(r, q, c) ->

						c.equal(r.get("rejected"), true)

				);

				break;

			case "pending":

				spec = spec.and(

						(r, q, c) ->

						c.equal(r.get("inProgress"), false)

				);

				break;

			}

		}

		Specification<SRPositionBasicsEntity> dateSpec =

				dateSpec("submittedOn");

		if (dateSpec != null) {

			spec = spec.and(dateSpec);

		}

		return spec;

	}

	public Specification<SRPositionBasicsEntity>

			buildApprovedSrSpecification() {

		Specification<SRPositionBasicsEntity> spec =

				Specification.allOf();

		spec = spec.and(

				(r, q, c) ->

				c.equal(r.get("approved"), true)

		);
		spec = spec.and((r, q, c) -> c.or(c.isFalse(r.get("jobSubmit")), c.isNull(r.get("jobSubmit"))));

		String search = getFilter("search");

		if (search != null) {

			spec = spec.and((r, q, c) -> c.or(

					c.like(

							c.lower(r.get("srId")),

							"%" + search.toLowerCase() + "%"

					),

					c.like(

							c.lower(r.get("jobTitle")),

							"%" + search.toLowerCase() + "%"

					)

			));

		}

		String departmentId = getFilter("departmentId");

		if (departmentId != null) {

			spec = spec.and(

					(r, q, c) ->

					c.equal(

							r.get("departmentId"),

							Integer.parseInt(departmentId)

					)

			);

		}

		String requestedBy = getFilter("requestedBy");

		if (requestedBy != null) {

			spec = spec.and(

					likeSpec("createdBy", requestedBy)

			);

		}

		Specification<SRPositionBasicsEntity> dateSpec =

				dateSpec("dateOfApproval3");

		if (dateSpec != null) {

			spec = spec.and(dateSpec);

		}

		return spec;

	}

	public Specification<SRPositionBasicsEntity> buildMyStaffingRequisitionSpecification(Long userId) {

		return (root, query, cb) -> {

			Predicate predicate = cb.conjunction();

			predicate = cb.and(predicate, cb.equal(root.get("userId"), userId));

			String search = getFilter("jobTitle");

			if (search != null && !search.isBlank()) {

				Predicate jobTitlePredicate = cb.like(cb.lower(root.get("jobTitle")),

						"%" + search.toLowerCase().trim() + "%");

				Predicate srIdPredicate = cb.like(cb.lower(root.get("srId")),

						"%" + search.toLowerCase().trim() + "%");

				predicate = cb.and(predicate, cb.or(jobTitlePredicate, srIdPredicate));

			}

			String departmentId = getFilter("departmentId");

			if (departmentId != null && !departmentId.isBlank()) {

				predicate = cb.and(predicate, cb.equal(root.get("departmentId"), Integer.parseInt(departmentId)));

			}

			String requestedBy = getFilter("requestedBy");

			if (requestedBy != null && !requestedBy.isBlank()) {

				predicate = cb.and(predicate,

						cb.like(cb.lower(root.get("createdBy")), "%" + requestedBy.toLowerCase().trim() + "%"));

			}

			String status = getStatus();

			if (status != null && !status.isBlank()) {

				if ("APPROVED".equalsIgnoreCase(status)) {

					predicate = cb.and(predicate, cb.isTrue(root.get("approved")));

				}

				else if ("REJECTED".equalsIgnoreCase(status)) {

					predicate = cb.and(predicate, cb.isTrue(root.get("rejected")));

				}

				else if ("DRAFT".equalsIgnoreCase(status)) {

					predicate = cb.and(predicate, cb.isFalse(root.get("submitted")));

				}

				else if ("PENDING".equalsIgnoreCase(status)) {

					predicate = cb.and(predicate,

							cb.isTrue(root.get("submitted")),

							cb.isFalse(root.get("approved")),

							cb.isFalse(root.get("rejected")));

				}

			}

			Specification<SRPositionBasicsEntity> dateSpecification =

					dateSpec("createdOn");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				predicate = cb.and(predicate, datePredicate);

			}

			return predicate;

		};

	}

	public Specification<CreateJobDetailsEntity> buildJobSpecification() {

		Specification<CreateJobDetailsEntity> spec = Specification.allOf();

		String search = getFilter("search");

		if (search != null) {

			spec = spec.and((r, q, c) -> c.or(

					c.like(c.lower(r.get("jobTitle")), "%" + search.toLowerCase() + "%"),

					c.like(c.lower(r.get("jobCode")), "%" + search.toLowerCase() + "%"),

					c.like(c.lower(r.get("location")), "%" + search.toLowerCase() + "%")));
		}

		String departmentId = getFilter("departmentId");

		if (departmentId != null) {

			spec = spec.and((r, q, c) -> c.equal(r.get("departmentId"), Integer.parseInt(departmentId)));
		}

		String businessUnitId = getFilter("businessUnitId");

		if (businessUnitId != null) {

			spec = spec.and((r, q, c) -> c.equal(r.get("businessUnitId"), Integer.parseInt(businessUnitId)));
		}

		String workMode = getFilter("workMode");

		if (workMode != null) {

			spec = spec.and(equalSpec("workMode", workMode));
		}

		String employmentType = getFilter("employmentType");

		if (employmentType != null) {

			spec = spec.and(equalSpec("employmentType", employmentType));
		}

		Specification<CreateJobDetailsEntity> dateSpec = dateSpec("createdAt");

		if (dateSpec != null) {

			spec = spec.and(dateSpec);
		}

		return spec;
	}

	public List<Integer> getIntegerListFilter(String key) {

		if (filters == null || !filters.containsKey(key)) {
			return List.of();
		}

		Object value = filters.get(key);

		if (value == null) {
			return List.of();
		}

		if (value instanceof List<?> list) {

			return list.stream()

					.filter(Objects::nonNull)

					.map(item -> Integer.parseInt(item.toString()))

					.toList();
		}

		return List.of();

	}

	public Specification<AssignRolesEntity>

			buildRecruiterSpecification(

					List<Integer> roleIds

	) {

		Specification<AssignRolesEntity> spec =

				Specification.allOf();

		if (roleIds != null && !roleIds.isEmpty()) {

			spec = spec.and(

					(r, q, c) -> r.get("roleId").in(roleIds)

			);

		}

		return spec;

	}

	public Specification<RecruiterAssignmentEntity> buildRecruiterStatusSpecification(Long userId) {

		return (root, query, cb) -> {

			Predicate predicate = cb.conjunction();

			if (userId != null) {

				predicate = cb.and(predicate, cb.equal(root.get("userId"), userId.intValue()));
			}

			String status = getStatus();

			if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {

				predicate = cb.and(predicate, cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
			}

			return predicate;
		};
	}

	public Specification<CreateJobDetailsEntity> buildMyRecruiterSpecification(List<Integer> jobIds) {

		return (root, query, cb) -> {

			Predicate predicate = cb.conjunction();

			if (jobIds != null && !jobIds.isEmpty()) {

				predicate = cb.and(predicate, root.get("jobId").in(jobIds));
			}

			String jobTitle = getFilter("jobTitle");

			if (jobTitle != null && !jobTitle.isBlank()) {

				predicate = cb.and(predicate,
						cb.like(cb.lower(root.get("jobTitle")), "%" + jobTitle.toLowerCase().trim() + "%"));
			}

			String departmentId = getFilter("departmentId");

			if (departmentId != null && !departmentId.isBlank()) {

				predicate = cb.and(predicate, cb.equal(root.get("departmentId"), Integer.parseInt(departmentId)));
			}

			String requestedBy = getFilter("requestedBy");

			if (requestedBy != null && !requestedBy.isBlank()) {

				predicate = cb.and(predicate,
						cb.like(cb.lower(root.get("createdBy")), "%" + requestedBy.toLowerCase().trim() + "%"));
			}

			String search = getFilter("search");

			if (search != null && !search.isBlank()) {

				predicate = cb.and(predicate, cb.or(

						cb.like(cb.lower(root.get("jobTitle")), "%" + search.toLowerCase().trim() + "%"),

						cb.like(cb.lower(root.get("jobCode")), "%" + search.toLowerCase().trim() + "%"),

						cb.like(cb.lower(root.get("location")), "%" + search.toLowerCase().trim() + "%")));
			}

			LocalDate[] dates = getDateRange();

			if (dates != null) {

				predicate = cb.and(predicate,

						cb.greaterThanOrEqualTo(root.get("createdAt"), Timestamp.valueOf(dates[0].atStartOfDay())),

						cb.lessThan(root.get("createdAt"), Timestamp.valueOf(dates[1].atStartOfDay())));
			}

			return predicate;
		};
	}

	public Specification<InterviewPlanEntity> buildInterviewPlanSpecification() {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (filters != null) {

				String status = getFilter("status");

				if (status != null && !status.isBlank()) {

					predicates.add(

							cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
				}
				String approvalStatus = getFilter("approvalStatus");

				if (approvalStatus != null && !approvalStatus.isBlank()) {

					predicates.add(

							cb.equal(cb.lower(root.get("approvalStatus")), approvalStatus.toLowerCase()));
				}

				String search = getFilter("search");

				if (search != null && !search.isBlank()) {

					search = search.toLowerCase().trim();

					predicates.add(

							cb.or(

									cb.like(cb.lower(root.get("planName")), "%" + search + "%"),

									cb.like(cb.lower(root.get("description")), "%" + search + "%")

							));
				}
				String userId = getFilter("createdBy");

				if (userId != null && !userId.isBlank()) {

					predicates.add(

							cb.equal(

									root.get("userId"),

									Integer.parseInt(userId)

							));
				}

				Specification<InterviewPlanEntity> dateSpecification = dateSpec("createdOn");

				if (dateSpecification != null) {

					Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Map<String, Long> buildInterviewPlanCounts(InterviewPlanRepository interviewPlanRepository) {

		Map<String, Long> counts = new LinkedHashMap<>();

		Specification<InterviewPlanEntity> countSpec = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (filters != null) {

				String search = getFilter("search");

				if (search != null && !search.isBlank()) {

					search = search.toLowerCase().trim();

					predicates.add(cb.or(cb.like(cb.lower(root.get("planName")), "%" + search + "%"),
							cb.like(cb.lower(root.get("description")), "%" + search + "%")));
				}

				String userId = getFilter("createdBy");

				if (userId != null && !userId.isBlank()) {

					predicates.add(cb.equal(root.get("userId"), Integer.parseInt(userId)));
				}

				Specification<InterviewPlanEntity> dateSpecification = dateSpec("createdOn");

				if (dateSpecification != null) {

					Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

					if (datePredicate != null) {
						predicates.add(datePredicate);
					}
				}

			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};

		long allPlans = interviewPlanRepository.count(countSpec);
		long activePlans = interviewPlanRepository
				.count(countSpec.and((root, query, cb) -> cb.equal(root.get("status"), "ACTIVE")));

		long deactivePlans = interviewPlanRepository
				.count(countSpec.and((root, query, cb) -> cb.equal(root.get("status"), "INACTIVE")));
		counts.put("allPlans", allPlans);
		counts.put("activePlans", activePlans);
		counts.put("inactivePlans", deactivePlans);

		return counts;
	}

	public Specification<InterviewPlanEntity> buildInterviewPlanApprovalSpecification() {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			String search = getFilter("search");

			if (search != null && !search.isBlank()) {

				search = search.toLowerCase().trim();

				predicates.add(cb.or(cb.like(cb.lower(root.get("planName")), "%" + search + "%")

				));
			}

			Specification<InterviewPlanEntity> dateSpecification = dateSpec("createdOn");

			if (dateSpecification != null) {

				predicates.add(dateSpecification.toPredicate(root, query, cb));
			}

			predicates.add(cb.equal(cb.lower(root.get("approvalStatus")), "inprogress"));

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<CreateJobDetailsEntity> buildJobAssignmentSpecification() {

		Specification<CreateJobDetailsEntity> spec = Specification.allOf();

		spec = spec.and((root, query, cb) -> cb.isTrue(root.get("isOpen")));

		String jobTitle = getFilter("jobTitle");

		if (jobTitle != null) {

			spec = spec.and(
					(root, query, cb) -> cb.like(cb.lower(root.get("jobTitle")), "%" + jobTitle.toLowerCase() + "%"));
		}

		String jobId = getFilter("jobId");

		if (jobId != null) {

			spec = spec.and((root, query, cb) -> cb.equal(root.get("jobId"), Integer.valueOf(jobId)));
		}

		Specification<CreateJobDetailsEntity> createdDateSpec = dateSpec("createdAt");

		if (createdDateSpec != null) {

			spec = spec.and(createdDateSpec);
		}

		return spec;
	}

	public Specification<InterviewCurrentStageEntity> buildTodayInterviewSpecification(Integer userId) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			// Logged-in user filter
			predicates.add(cb.equal(root.get("interviewerId"), userId));
			predicates.add(cb.equal(root.get("interviewDate"), LocalDate.now()));
			predicates.add(cb.isFalse(root.get("interviewCompleted")));
			String interviewType = getFilter("interviewType");

			if (interviewType != null && !interviewType.isBlank()) {

				predicates.add(cb.equal(cb.lower(root.get("interviewType")), interviewType.toLowerCase()));
			}

			String round = getFilter("round");

			if (round != null && !round.isBlank()) {

				predicates.add(cb.equal(cb.lower(root.get("round")), round.toLowerCase()));
			}

			Specification<InterviewCurrentStageEntity> dateSpecification = dateSpec("createdOn");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<InterviewerAssignmentEntity> buildInterviewAssignmentSpecification(Integer userId) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("interviewerUserId"), userId.longValue()));

			// Search Filter
			String search = getFilter("search");

			if (search != null && !search.isBlank()) {

				String searchText = "%" + search.toLowerCase().trim() + "%";

				Predicate jobTitlePredicate = cb.like(cb.lower(root.get("jobTitle")), searchText);

				Predicate departmentPredicate = cb.like(cb.lower(root.get("deptName")), searchText);

				predicates.add(cb.or(jobTitlePredicate, departmentPredicate));
			}

			Specification<InterviewerAssignmentEntity> dateSpecification = dateSpec("createdAt");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<InterviewSessionEntity> buildAIInterviewQuestionSpecification() {

		return (root, query, cb) -> {

			query.distinct(true);

			List<Predicate> predicates = new ArrayList<>();

			Join<InterviewSessionEntity, ResumeAnalysisEntity> applicantJoin = root.join("applicant", JoinType.INNER);

			Join<InterviewSessionEntity, CreateJobDetailsEntity> jobJoin = root.join("job", JoinType.LEFT);

			predicates.add(cb.or(cb.isFalse(root.get("moveToSchedule")), cb.isNull(root.get("moveToSchedule"))));

			predicates.add(cb.equal(cb.lower(applicantJoin.get("status")), "shortlisted"));

			String search = getFilter("search");

			if (search != null && !search.isBlank()) {

				String keyword = "%" + search.trim().toLowerCase() + "%";

				Subquery<Integer> candidateSearchSubquery = query.subquery(Integer.class);

				Root<JobApplicationEntity> applicationRoot = candidateSearchSubquery.from(JobApplicationEntity.class);

				candidateSearchSubquery.select(applicationRoot.get("id")).where(
						cb.equal(applicationRoot.get("id"), root.get("applicationId")),
						cb.like(cb.lower(applicationRoot.join("candidate", JoinType.LEFT).get("candidateId")),
								keyword));

				predicates.add(cb.or(cb.like(cb.lower(applicantJoin.get("candidateName")), keyword),
						cb.like(cb.lower(jobJoin.get("jobTitle")), keyword), cb.exists(candidateSearchSubquery)));
			}
			String jobTitle = getFilter("jobTitle");

			if (jobTitle != null && !jobTitle.isBlank()) {

				predicates.add(cb.equal(cb.lower(jobJoin.get("jobTitle")), jobTitle.toLowerCase()));
			}

			String questionStatusFilter = getFilter("questionStatus");

			if (questionStatusFilter != null && !questionStatusFilter.isBlank()) {

				Boolean questionStatus = Boolean.valueOf(questionStatusFilter);

				predicates.add(cb.equal(root.get("questionsStatus"), questionStatus));
			}
			LocalDate[] dates = getDateRange();

			if (dates != null) {

				predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), dates[0].atStartOfDay()));

				predicates.add(cb.lessThan(root.get("createdDate"), dates[1].plusDays(1).atStartOfDay()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<InterviewSessionEntity> buildInterviewInProgressSpecification() {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.isTrue(root.get("isScheduled")));
			predicates.add(cb.isNotNull(root.get("scheduledTime")));
			predicates.add(cb.equal(root.get("status"), "upcoming"));

			predicates.add(cb.or(cb.isFalse(root.get("isDeleted")), cb.isNull(root.get("isDeleted"))));

			predicates.add(cb.greaterThanOrEqualTo(root.get("scheduledTime"), LocalDateTime.now()));

			String search = getFilter("search");

			String jobTitle = getFilter("jobTitle");

			if (jobTitle != null && !jobTitle.isBlank() && !"ALL".equalsIgnoreCase(jobTitle)) {

				Join<InterviewSessionEntity, CreateJobDetailsEntity> job = root.join("job", JoinType.LEFT);

				predicates.add(cb.like(cb.lower(job.get("jobTitle")), "%" + jobTitle.toLowerCase().trim() + "%"));
			}

			if (search != null && !search.isBlank()) {

				Join<InterviewSessionEntity, ResumeAnalysisEntity> applicant = root.join("applicant", JoinType.LEFT);

				String keyword = "%" + search.toLowerCase().trim() + "%";

				Predicate candidateName = cb.like(cb.lower(applicant.get("candidateName")), keyword);

				Predicate email = cb.like(cb.lower(applicant.get("email")), keyword);

				// Candidate ID search
				Subquery<Integer> candidateSearchSubquery = query.subquery(Integer.class);

				Root<JobApplicationEntity> applicationRoot = candidateSearchSubquery.from(JobApplicationEntity.class);

				Join<JobApplicationEntity, CandidateCreationDetailsEntity> candidateJoin = applicationRoot
						.join("candidate", JoinType.LEFT);

				candidateSearchSubquery.select(applicationRoot.get("id")).where(
						cb.equal(applicationRoot.get("id"), root.get("applicationId")),
						cb.like(cb.lower(candidateJoin.get("candidateId")), keyword));

				Predicate candidateId = cb.exists(candidateSearchSubquery);

				predicates.add(cb.or(candidateName, email, candidateId));
			}

			Specification<InterviewSessionEntity> dateSpecification = dateSpec("scheduledTime");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<InterviewSessionEntity> buildAIScheduleInterviewSpecification(
			InterviewPlanRepository interviewPlanRepository) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.isFalse(root.get("isScheduled")));

			predicates.add(cb.isTrue(root.get("moveToSchedule")));

			// Search
			// Search
			String search = getFilter("search");

			if (search != null && !search.isBlank()) {

				String keyword = "%" + search.toLowerCase().trim() + "%";

				Subquery<Integer> candidateSearchSubquery = query.subquery(Integer.class);

				Root<JobApplicationEntity> applicationRoot = candidateSearchSubquery.from(JobApplicationEntity.class);

				Join<JobApplicationEntity, CandidateCreationDetailsEntity> candidateJoin = applicationRoot
						.join("candidate", JoinType.LEFT);

				candidateSearchSubquery.select(applicationRoot.get("id")).where(
						cb.equal(applicationRoot.get("id"), root.get("applicationId")),
						cb.like(cb.lower(candidateJoin.get("candidateId")), keyword));

				predicates.add(cb.or(cb.like(cb.lower(root.get("applicant").get("candidateName")), keyword),
						cb.like(cb.lower(root.get("applicant").get("email")), keyword),
						cb.like(cb.lower(root.get("job").get("jobTitle")), keyword),
						cb.exists(candidateSearchSubquery)));
			}
			// Job Title Filter
			String jobTitle = getFilter("jobTitle");

			if (jobTitle != null && !jobTitle.isBlank()) {

				predicates.add(
						cb.like(cb.lower(root.get("job").get("jobTitle")), "%" + jobTitle.toLowerCase().trim() + "%"));
			}

			// Interview Plan Filter
			String interviewPlan = getFilter("interviewPlan");

			log.info(interviewPlan);
			if (interviewPlan != null && !interviewPlan.isBlank()) {

				List<Integer> planIds = interviewPlanRepository.findByPlanNameContainingIgnoreCase(interviewPlan)
						.stream().map(InterviewPlanEntity::getId).toList();

				log.info("Plan Ids : {}", planIds);

				if (!planIds.isEmpty()) {

					predicates.add(root.get("job").get("planId").in(planIds));
				}
			}

			// Priority Filter
			String priority = getFilter("priority");

			if (priority != null && !priority.isBlank()) {

				LocalDateTime now = LocalDateTime.now();

				if ("Low".equalsIgnoreCase(priority)) {

					predicates.add(cb.between(root.get("moveToScheduleDateTime"), now.minusDays(2), now));

				} else if ("Medium".equalsIgnoreCase(priority)) {

					predicates.add(cb.between(root.get("moveToScheduleDateTime"), now.minusDays(3), now.minusDays(2)));

				} else if ("High".equalsIgnoreCase(priority)) {

					predicates.add(cb.lessThan(root.get("moveToScheduleDateTime"), now.minusDays(3)));
				}
			}

			Specification<InterviewSessionEntity> dateSpecification = dateSpec("moveToScheduleDateTime");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<CreateJobDetailsEntity> buildJobsSpecification() {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			// Only open jobs
			predicates.add(cb.or(cb.isTrue(root.get("isOpen")), cb.isNull(root.get("isOpen"))));

			String country = getFilter("country");

			if (country != null && !country.isBlank()) {

				predicates.add(cb.like(cb.lower(root.get("country")), "%" + country.toLowerCase().trim() + "%"));
			}

			String location = getFilter("location");

			if (location != null && !location.isBlank()) {

				predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase().trim() + "%"));
			}

			String workMode = getFilter("workMode");

			if (workMode != null && !workMode.isBlank()) {

				predicates.add(cb.equal(cb.lower(root.get("workMode")), workMode.toLowerCase().trim()));
			}

			String employmentType = getFilter("employmentType");

			if (employmentType != null && !employmentType.isBlank()) {

				predicates.add(cb.equal(cb.lower(root.get("employmentType")), employmentType.toLowerCase().trim()));
			}

			String search = getFilter("search");

			if (search != null && !search.isBlank()) {

				String keyword = "%" + search.toLowerCase().trim() + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.get("jobTitle")), keyword),
						cb.like(cb.lower(root.get("jobCode")), keyword)));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<InterviewCurrentStageEntity> buildFeedbackSpecification(Integer userId) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			Predicate feedbackStatusPredicate = cb.or(cb.equal(cb.lower(root.get("feedbackStatus")), "pending"),
					cb.equal(cb.lower(root.get("feedbackStatus")), "hold"));

			predicates.add(feedbackStatusPredicate);
			predicates.add(cb.isTrue(root.get("interviewCompleted")));

			predicates.add(cb.equal(root.get("interviewerId"), userId));

			LocalDate[] dates = getDateRange();

			if (dates != null) {

				predicates.add(cb.greaterThanOrEqualTo(root.get("interviewCompletedOn"), dates[0].atStartOfDay()));

				predicates.add(cb.lessThan(root.get("interviewCompletedOn"), dates[1].plusDays(1).atStartOfDay()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<InterviewCurrentStageEntity> toBeScheduleInterviewSpecification(Integer userId) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.isFalse(root.get("toSchedule")));

			predicates.add(cb.equal(root.get("interviewerId"), userId));

			String round = getFilter("round");

			if (round != null && !round.isBlank()) {

				try {

					predicates.add(cb.equal(root.get("currentStageType"), Integer.parseInt(round)));

				} catch (NumberFormatException e) {

				}
			}

			Specification<InterviewCurrentStageEntity> dateSpecification = dateSpec("createdOn");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<ApplicanDetailsEntity> buildInterviewProgressSpecification() {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (filters != null) {

				Object jobId = filters.get("jobId");

				if (jobId != null && !jobId.toString().isBlank()) {

					predicates.add(cb.equal(root.get("jobId"), Integer.parseInt(jobId.toString())));
				}

			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<OfferDetailsEntity> buildReadyToReleaseSpecification() {

		Specification<OfferDetailsEntity> spec = (root, query, cb) -> cb.conjunction();

		String releaseType = getFilter("releaseType");

		if ("PENDING".equalsIgnoreCase(releaseType)) {

			// ============================
			// PENDING - ORIGINAL OFFER
			// ============================

			spec = spec.and((root, query, cb) -> {

				Subquery<Integer> reReleaseSubquery = query.subquery(Integer.class);

				Root<OfferDetailsEntity> oldOffer = reReleaseSubquery.from(OfferDetailsEntity.class);

				reReleaseSubquery.select(oldOffer.get("reReleaseOfferId"));

				reReleaseSubquery.where(

						// Same application
						cb.equal(oldOffer.get("jobApplication").get("id"), root.get("jobApplication").get("id")),

						// Another offer row points to the current row
						cb.equal(oldOffer.get("reReleaseOfferId"), root.get("id")));

				return cb.and(

						// Approval completed
						cb.isTrue(root.get("approve")),

						// Offer is not released
						cb.or(cb.isFalse(root.get("offerReleased")), cb.isNull(root.get("offerReleased"))),

						// Current row must NOT be the re-release row
						cb.not(cb.exists(reReleaseSubquery)));
			});

		} else if ("RE-RELEASE".equalsIgnoreCase(releaseType)) {

			// ============================
			// RE-RELEASE - NEW OFFER
			// ============================

			spec = spec.and((root, query, cb) -> {

				Subquery<Integer> reReleaseSubquery = query.subquery(Integer.class);

				Root<OfferDetailsEntity> oldOffer = reReleaseSubquery.from(OfferDetailsEntity.class);

				reReleaseSubquery.select(oldOffer.get("reReleaseOfferId"));

				reReleaseSubquery.where(

						// Same application
						cb.equal(oldOffer.get("jobApplication").get("id"), root.get("jobApplication").get("id")),

						// Old/original row has re-release ID
						cb.isNotNull(oldOffer.get("reReleaseOfferId")),

						// Old row points to the current row
						cb.equal(oldOffer.get("reReleaseOfferId"), root.get("id")));

				return cb.and(

						// new offer is approved
						cb.isTrue(root.get("approve")),

						// new offer is not released
						cb.or(cb.isFalse(root.get("offerReleased")), cb.isNull(root.get("offerReleased"))),

						// Current row is the re-release row
						cb.exists(reReleaseSubquery));
			});

		}

		String search = getFilter("search");
		if (search != null) {

			String like = "%" + search.toLowerCase() + "%";

			spec = spec.and((root, query, cb) -> {

				query.distinct(true);

				Join<OfferDetailsEntity, JobApplicationEntity> application = root.join("jobApplication");

				Root<CreateJobDetailsEntity> job = query.from(CreateJobDetailsEntity.class);

				Predicate jobJoin = cb.equal(application.get("jobId"), job.get("jobId"));

				Predicate candidateName = cb.like(
						cb.lower(cb.concat(cb.concat(application.get("firstName"), " "), application.get("lastName"))),
						like);

				Predicate firstName = cb.like(cb.lower(application.get("firstName")), like);

				Predicate lastName = cb.like(cb.lower(application.get("lastName")), like);

				Predicate email = cb.like(cb.lower(application.get("email")), like);

				Predicate jobTitle = cb.like(cb.lower(job.get("jobTitle")), like);

				return cb.and(jobJoin, cb.or(candidateName, firstName, lastName, email, jobTitle));
			});
		}
		String jobId = getFilter("jobId");

		if (jobId != null) {

			Integer id = Integer.valueOf(jobId);

			spec = spec.and((root, query, cb) -> {

				Join<OfferDetailsEntity, JobApplicationEntity> application = root.join("jobApplication");

				return cb.equal(application.get("jobId"), id);

			});
		}
		String departmentId = getFilter("departmentId");

		if (departmentId != null) {

			Integer id = Integer.valueOf(departmentId);

			spec = spec.and((root, query, cb) -> {

				Join<OfferDetailsEntity, JobApplicationEntity> application = root.join("jobApplication");

				Root<CreateJobDetailsEntity> job = query.from(CreateJobDetailsEntity.class);

				return cb.and(cb.equal(application.get("jobId"), job.get("jobId")),
						cb.equal(job.get("departmentId"), id));

			});
		}

		String priority = getFilter("priority");

		if (priority != null) {

			LocalDateTime now = LocalDateTime.now();

			switch (priority.toLowerCase()) {

			case "high":

				spec = spec.and((root, query, cb) ->

				cb.lessThanOrEqualTo(root.get("dateOfApproval3"), now.minusDays(5)));

				break;

			case "medium":

				spec = spec.and((root, query, cb) ->

				cb.and(

						cb.greaterThan(root.get("dateOfApproval3"), now.minusDays(5)),

						cb.lessThanOrEqualTo(root.get("dateOfApproval3"), now.minusDays(3))

				));

				break;

			case "low":

				spec = spec.and((root, query, cb) ->

				cb.greaterThan(root.get("dateOfApproval3"), now.minusDays(3)));

				break;

			}

		}

		Specification<OfferDetailsEntity> dateSpecification = dateSpec("dateOfApproval3");

		if (dateSpecification != null) {
			spec = spec.and(dateSpecification);
		}

		return spec;
	}

	public Specification<OfferDetailsEntity> buildRaiseOfferRequestSpecification() {

		return (root, query, cb) -> {

			query.distinct(true);

			List<Predicate> predicates = new ArrayList<>();

			Join<OfferDetailsEntity, JobApplicationEntity> application = root.join("jobApplication", JoinType.INNER);

			Root<CreateJobDetailsEntity> job = query.from(CreateJobDetailsEntity.class);

			predicates.add(cb.equal(application.get("jobId"), job.get("jobId")));

			// Candidate should be hired/accepted
			predicates.add(cb.equal(cb.lower(root.get("interviewCompletionStatus")), "hired"));

			// Candidate should NOT be submitted for financial approval
			predicates.add(cb.or(cb.isNull(root.get("submitFinancialApproval")),
					cb.isFalse(root.get("submitFinancialApproval"))));

			// Search
			String search = getFilter("search");

			if (search != null && !search.isBlank()) {

				String keyword = "%" + search.toLowerCase().trim() + "%";

				Predicate candidateName = cb.like(
						cb.lower(cb.concat(cb.concat(application.get("firstName"), " "), application.get("lastName"))),
						keyword);

				Predicate email = cb.like(cb.lower(application.get("email")), keyword);

				predicates.add(cb.or(candidateName, email));
			}

			// Job Filter
			String jobId = getFilter("jobId");

			if (jobId != null) {

				predicates.add(cb.equal(job.get("jobId"), Integer.parseInt(jobId)));
			}

			// Department Filter
			String departmentId = getFilter("departmentId");

			if (departmentId != null) {

				predicates.add(cb.equal(job.get("departmentId"), Integer.parseInt(departmentId)));
			}

			// Priority Filter
			String priority = getFilter("priority");

			if (priority != null && !priority.isBlank()) {

				LocalDate today = LocalDate.now();

				switch (priority.toUpperCase()) {

				case "LOW":

					predicates.add(cb.between(root.get("interviewCompletionDate"), today.minusDays(1).atStartOfDay(),
							today.atStartOfDay()));
					break;

				case "MEDIUM":

					predicates.add(cb.between(root.get("interviewCompletionDate"), today.minusDays(2).atStartOfDay(),
							today.minusDays(1).atStartOfDay()));
					break;

				case "HIGH":

					predicates.add(cb.lessThanOrEqualTo(root.get("interviewCompletionDate"),
							today.minusDays(3).atStartOfDay()));
					break;
				}
			}

			// Date Filter
			Specification<OfferDetailsEntity> dateSpecification = dateSpec("interviewCompletionDate");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {

					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<InterviewScheduleEntity> buildUpcomingInterviewSpecification() {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			LocalDate today = LocalDate.now();

			Predicate rescheduledInterview = cb.and(cb.isNotNull(root.get("rescheduleDate")),
					cb.greaterThan(root.get("rescheduleDate"), today));

			Predicate scheduledInterview = cb.and(cb.isNull(root.get("rescheduleDate")),
					cb.greaterThan(root.get("interviewDate"), today));

			predicates.add(cb.or(rescheduledInterview, scheduledInterview));

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<OfferDetailsEntity> buildOfferApprovalSpecification() {

		return (root, query, cb) -> {
			query.distinct(true);

			List<Predicate> predicates = new ArrayList<>();

			Join<OfferDetailsEntity, JobApplicationEntity> application = root.join("jobApplication", JoinType.INNER);

			Root<CreateJobDetailsEntity> job = query.from(CreateJobDetailsEntity.class);

			Root<DepartmentsEntity> department = query.from(DepartmentsEntity.class);

			// predicates.add(cb.equal(cb.lower(root.get("interviewCompletionStatus")),
			// "hired"));

			predicates.add(cb.isTrue(root.get("submitFinancialApproval")));
			predicates.add(cb.isFalse(root.get("approve")));

			String approvalType = getFilter("approvalType");

			if (approvalType != null && !approvalType.isBlank()) {

				// NEW OFFER APPROVALS
				if ("New Offer Approvals".equalsIgnoreCase(approvalType)) {

					Subquery<Long> countSubquery = query.subquery(Long.class);

					Root<OfferDetailsEntity> subOffer = countSubquery.from(OfferDetailsEntity.class);

					countSubquery.select(cb.count(subOffer));

					countSubquery.where(
							cb.equal(subOffer.get("jobApplication").get("id"), root.get("jobApplication").get("id")));

					// Only applications having ONE offer row
					predicates.add(cb.equal(countSubquery, 1L));
				}

				// NEGOTIATION APPROVALS
				else if ("Negotiation Approvals".equalsIgnoreCase(approvalType)) {

					Subquery<Integer> reReleaseSubquery = query.subquery(Integer.class);

					Root<OfferDetailsEntity> oldOffer = reReleaseSubquery.from(OfferDetailsEntity.class);

					reReleaseSubquery.select(oldOffer.get("reReleaseOfferId"));

					reReleaseSubquery.where(
							cb.equal(oldOffer.get("jobApplication").get("id"), root.get("jobApplication").get("id")),
							cb.isNotNull(oldOffer.get("reReleaseOfferId")),
							cb.equal(oldOffer.get("reReleaseOfferId"), root.get("id")));

					// Current row must be the re-release offer
					predicates.add(cb.exists(reReleaseSubquery));

					// Re-release offer must still be pending
					predicates.add(cb.isTrue(root.get("submitFinancialApproval")));

					predicates.add(cb.isFalse(root.get("approve")));

					predicates.add(cb.isFalse(root.get("offerReleased")));
				}
			}

			predicates.add(cb.equal(application.get("jobId"), job.get("jobId")));
			predicates.add(cb.equal(job.get("departmentId"), department.get("id")));
			String search = getFilter("search");

			if (search != null && !search.isBlank()) {

				String keyword = "%" + search.trim().toLowerCase() + "%";

				Predicate applicantName = cb
						.like(cb.lower(cb.concat(cb.concat(cb.coalesce(application.get("firstName"), ""), " "),
								cb.coalesce(application.get("lastName"), ""))), keyword);

				Predicate firstName = cb.like(cb.lower(cb.coalesce(application.get("firstName"), "")), keyword);

				Predicate lastName = cb.like(cb.lower(cb.coalesce(application.get("lastName"), "")), keyword);

				Predicate email = cb.like(cb.lower(cb.coalesce(application.get("email"), "")), keyword);

				predicates.add(cb.or(applicantName, firstName, lastName, email));
			}

			String jobId = getFilter("jobId");

			if (jobId != null && !jobId.isBlank()) {
				predicates.add(cb.equal(job.get("jobId"), Integer.valueOf(jobId)));
			}

			String departmentId = getFilter("departmentId");

			if (departmentId != null && !departmentId.isBlank()) {
				predicates.add(cb.equal(department.get("id"), Integer.valueOf(departmentId)));
			}

			String approvalStatus = getFilter("approvalStatus");

			if (approvalStatus != null && !approvalStatus.isBlank()) {

				if ("APPROVED".equalsIgnoreCase(approvalStatus)) {

					predicates.add(cb.isTrue(root.get("approve")));

				} else if ("PENDING".equalsIgnoreCase(approvalStatus)) {

					predicates.add(cb.isFalse(root.get("approve")));
				}
			}

			Specification<OfferDetailsEntity> dateSpecification = dateSpec("createdDate");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<OfferDetailsEntity> buildPendingApprovalSpecification(Long loginRoleId) {

		return (root, query, cb) -> {

			query.distinct(true);

			List<Predicate> predicates = new ArrayList<>();

			Join<OfferDetailsEntity, JobApplicationEntity> application = root.join("jobApplication", JoinType.INNER);

			Root<CreateJobDetailsEntity> job = query.from(CreateJobDetailsEntity.class);
			Root<DepartmentsEntity> department = query.from(DepartmentsEntity.class);
			Root<OfferDetailsChildEntity> child = query.from(OfferDetailsChildEntity.class);

			predicates.add(cb.equal(application.get("jobId"), job.get("jobId")));
			predicates.add(cb.equal(job.get("departmentId"), department.get("id")));

			// Join OfferDetailsEntity with OfferDetailsChildEntity
			predicates.add(cb.equal(child.get("offer").get("id"), root.get("id")));

			// Pending with Approver 1
			Predicate approver1 = cb.and(cb.equal(child.get("role1"), loginRoleId), cb.isTrue(child.get("approver1")),
					cb.isFalse(root.get("approver1")));

			// Pending with Approver 2
			Predicate approver2 = cb.and(cb.equal(child.get("role2"), loginRoleId), cb.isTrue(child.get("approver2")),
					cb.isFalse(root.get("approver2")));

			// Pending with Approver 3
			Predicate approver3 = cb.and(cb.equal(child.get("role3"), loginRoleId), cb.isTrue(child.get("approver3")),
					cb.isFalse(root.get("approver3")));

			predicates.add(cb.or(approver1, approver2, approver3));

			String search = getFilter("search");

			if (search != null && !search.isBlank()) {

				String keyword = "%" + search.trim().toLowerCase() + "%";

				Predicate applicantName = cb
						.like(cb.lower(cb.concat(cb.concat(cb.coalesce(application.get("firstName"), ""), " "),
								cb.coalesce(application.get("lastName"), ""))), keyword);

				Predicate firstName = cb.like(cb.lower(cb.coalesce(application.get("firstName"), "")), keyword);

				Predicate lastName = cb.like(cb.lower(cb.coalesce(application.get("lastName"), "")), keyword);

				Predicate email = cb.like(cb.lower(cb.coalesce(application.get("email"), "")), keyword);

				Predicate jobTitle = cb.like(cb.lower(cb.coalesce(job.get("jobTitle"), "")), keyword);

				predicates.add(cb.or(applicantName, firstName, lastName, email, jobTitle));
			}

			String jobId = getFilter("jobId");

			if (jobId != null && !jobId.isBlank()) {
				predicates.add(cb.equal(job.get("jobId"), Integer.valueOf(jobId)));
			}

			String departmentId = getFilter("departmentId");

			if (departmentId != null && !departmentId.isBlank()) {
				predicates.add(cb.equal(department.get("id"), Integer.valueOf(departmentId)));
			}

			Specification<OfferDetailsEntity> dateSpecification = dateSpec("createdDate");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<AgencyDetailsEntity> buildAgencySpec(List<Integer> categoryIds) {

		Specification<AgencyDetailsEntity> spec = null;

		String search = getFilter("search");
		if (search != null) {

			Specification<AgencyDetailsEntity> searchSpec = (root, query, cb) -> cb.or(
					cb.like(cb.lower(root.get("agencyName")), "%" + search.toLowerCase() + "%"),
					cb.like(cb.lower(root.get("emailId")), "%" + search.toLowerCase() + "%"));

			spec = (spec == null) ? searchSpec : spec.and(searchSpec);
		}

		// Category Filter
		if (!categoryIds.isEmpty()) {

			Specification<AgencyDetailsEntity> categorySpec = (root, query, cb) -> {

				List<Predicate> predicates = new ArrayList<>();

				for (Integer id : categoryIds) {

					predicates.add(cb.like(cb.concat(cb.concat(",", root.get("categoryIds")), ","), "%," + id + ",%"));

				}

				return cb.or(predicates.toArray(new Predicate[0]));

			};

			spec = (spec == null) ? categorySpec : spec.and(categorySpec);
		}

		return spec;
	}

	public Specification<NegotiationOfferEntity> buildOfferNegotiationSpecification() {

		return (root, query, cb) -> {

			query.distinct(false);

			List<Predicate> predicates = new ArrayList<>();

			Join<NegotiationOfferEntity, CandidateCreationDetailsEntity> candidate = root.join("candidate",
					JoinType.LEFT);

			Join<NegotiationOfferEntity, CreateJobDetailsEntity> job = root.join("job", JoinType.LEFT);

			Join<NegotiationOfferEntity, OfferDetailsEntity> offer = root.join("offer", JoinType.LEFT);

			String search = getFilter("search");

			if (search != null && !search.trim().isEmpty()) {

				String keyword = "%" + search.trim().toLowerCase() + "%";

				predicates.add(cb.or(cb.like(cb.lower(candidate.get("firstName")), keyword),
						cb.like(cb.lower(candidate.get("email")), keyword)));
			}

			String jobId = getFilter("jobId");

			if (jobId != null && !jobId.isBlank()) {
				predicates.add(cb.equal(job.get("jobId"), Integer.valueOf(jobId)));
			}

			String priority = getFilter("priority");

			if (priority != null && !priority.isBlank()) {

				LocalDate today = LocalDate.now();

				switch (priority.toUpperCase()) {

				case "LOW":
					predicates.add(cb.greaterThanOrEqualTo(root.get("offerNegotiatedDate"), today.minusDays(1)));
					break;

				case "MEDIUM":
					predicates.add(cb.between(root.get("offerNegotiatedDate"), today.minusDays(2), today.minusDays(2)));
					break;

				case "HIGH":
					predicates.add(cb.lessThanOrEqualTo(root.get("offerNegotiatedDate"), today.minusDays(3)));
					break;
				}
			}

			Specification<NegotiationOfferEntity> dateSpecification = dateSpec("offerNegotiatedDate");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			String status = getFilter("status");

			if (status != null && !status.isBlank()) {

				predicates.add(cb.equal(cb.upper(offer.get("offerStatus")), status.toUpperCase()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<OfferDetailsEntity> buildOfferStatusSpecification() {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			Join<OfferDetailsEntity, JobApplicationEntity> application = root.join("jobApplication", JoinType.LEFT);

			Join<JobApplicationEntity, CandidateCreationDetailsEntity> candidate = application.join("candidate",
					JoinType.LEFT);

			String search = getFilter("search");

			if (search != null && !search.trim().isEmpty()) {

				String keyword = "%" + search.trim().toLowerCase() + "%";

				predicates.add(cb.or(cb.like(cb.lower(candidate.get("firstName")), keyword),
						cb.like(cb.lower(candidate.get("email")), keyword)));
			}

			String jobId = getFilter("jobId");

			if (jobId != null && !jobId.isBlank()) {

				predicates.add(cb.equal(application.get("jobId"), Integer.valueOf(jobId)));
			}

			String status = getFilter("status");

			if (status != null && !status.isBlank()) {

				switch (status.toUpperCase()) {

				case "REQUESTED FOR NEGOTIATION":
					break;

				case "ACCEPTED":
				case "REJECTED":
				case "PENDING":

					predicates.add(cb.equal(cb.upper(root.get("offerStatus")), status.toUpperCase()));
					break;

				case "EXPIRED":

					predicates.add(cb.equal(cb.upper(root.get("offerStatus")), "EXPIRED"));

					predicates.add(cb.lessThanOrEqualTo(root.get("offerReleasedAt"), LocalDateTime.now().minusDays(7)));
					break;
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<RecruiterAssignmentEntity> buildRecruiterDashboardSpecification(Integer recruiterId) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("userId"), recruiterId));

			Specification<RecruiterAssignmentEntity> dateSpecification = dateSpec("assignedAt");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<JobApplicationEntity> buildRecruiterApplicationSpecification(Integer recruiterId) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			predicates.add(cb.equal(root.get("recruiterId"), recruiterId));

			Specification<JobApplicationEntity> dateSpecification = dateSpec("createdDate");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<OfferDetailsEntity> buildRecruiterOfferSpecification(Integer recruiterId) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			Join<OfferDetailsEntity, JobApplicationEntity> application = root.join("jobApplication");

			predicates.add(cb.equal(application.get("recruiterId"), recruiterId));

			predicates.add(cb.isTrue(root.get("offerReleased")));

			Specification<OfferDetailsEntity> dateSpecification = dateSpec("offerReleasedAt");

			if (dateSpecification != null) {

				Predicate datePredicate = dateSpecification.toPredicate(root, query, cb);

				if (datePredicate != null) {
					predicates.add(datePredicate);
				}
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}

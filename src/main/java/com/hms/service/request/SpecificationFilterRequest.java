package com.hms.service.request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.NotificationEngineEntity;
import com.hms.service.entity.SRPositionBasicsEntity;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	private String getFilter(String key) {

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

							c.lower(r.get("srId")),

							"%" + search.toLowerCase() + "%"

					),

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

				dateSpec("createdOn");

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

		Specification<CreateJobDetailsEntity> dateSpec = dateSpec("targetStartDate");

		if (dateSpec != null) {

			spec = spec.and(dateSpec);
		}

		return spec;
	}

}

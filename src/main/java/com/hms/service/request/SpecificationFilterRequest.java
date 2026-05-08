package com.hms.service.request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.NotificationEngineEntity;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpecificationFilterRequest {

	private Integer page = 0;

	private Integer size = 10;

	private String sortBy = "id";

	private String direction = "DESC";

	private Map<String, Object> filters;

	public Specification<ApprovalChainEntity> buildBaseSpec() {

		return (root, query, cb) -> {
			Specification<ApprovalChainEntity> spec = Specification.allOf();

			if (filters != null) {

				if (filters.containsKey("approval")) {

					String approval = filters.get("approval").toString().trim();

					if (!approval.isBlank()) {

						spec = spec.and((r, q, c) -> c.equal(c.lower(r.get("approval")), approval.toLowerCase()));
					}
				}

				if (filters.containsKey("status")) {

					String status = filters.get("status").toString().trim();

					if (!status.isBlank()) {

						spec = spec.and((r, q, c) -> c.equal(c.lower(r.get("status")), status.toLowerCase()));
					}
				}

				if (filters.containsKey("chainName")) {

					String chainName = filters.get("chainName").toString().trim();

					if (!chainName.isBlank()) {

						spec = spec.and(
								(r, q, c) -> c.like(c.lower(r.get("chainName")), "%" + chainName.toLowerCase() + "%"));
					}
				}

				if (filters.containsKey("search")) {

					String search = filters.get("search").toString().trim();

					if (!search.isBlank()) {

						spec = spec.and((r, q, c) -> c.or(

								c.like(c.lower(r.get("chainName")), "%" + search.toLowerCase() + "%"),

								c.like(c.lower(r.get("description")), "%" + search.toLowerCase() + "%"),

								c.like(c.lower(r.get("approval")), "%" + search.toLowerCase() + "%"),

								c.like(c.lower(r.get("status")), "%" + search.toLowerCase() + "%")));
					}
				}

				spec = applyDateFilter(spec);
			}

			return spec.toPredicate(root, query, cb);
		};
	}

	public Specification<ApprovalChainEntity> buildCountSpec() {

		return (root, query, cb) -> {

			Specification<ApprovalChainEntity> spec = Specification.allOf();

			if (filters != null) {

				if (filters.containsKey("chainName")) {

					String chainName = filters.get("chainName").toString().trim();

					if (!chainName.isBlank()) {

						spec = spec.and(
								(r, q, c) -> c.like(c.lower(r.get("chainName")), "%" + chainName.toLowerCase() + "%"));
					}
				}

				if (filters.containsKey("search")) {

					String search = filters.get("search").toString().trim();

					if (!search.isBlank()) {

						spec = spec.and((r, q, c) -> c.or(

								c.like(c.lower(r.get("chainName")), "%" + search.toLowerCase() + "%"),

								c.like(c.lower(r.get("description")), "%" + search.toLowerCase() + "%")));
					}
				}

				spec = applyDateFilter(spec);
			}

			return spec.toPredicate(root, query, cb);
		};
	}

	private Specification<ApprovalChainEntity> applyDateFilter(Specification<ApprovalChainEntity> spec) {

		return spec.and((root, query, cb) -> {

			LocalDate fromDate = null;
			LocalDate toDate = null;

			if (filters != null && filters.containsKey("dateFilter")) {

				String range = filters.get("dateFilter").toString().replace("_", "").toUpperCase();

				LocalDate today = LocalDate.now();

				switch (range) {

				case "TODAY":

					fromDate = today;
					toDate = today.plusDays(1);
					break;

				case "THISWEEK":

					fromDate = today.minusWeeks(1).with(DayOfWeek.SUNDAY);

					toDate = today.plusDays(1);
					break;

				case "THISMONTH":

					fromDate = today.withDayOfMonth(1);

					toDate = today.plusDays(1);
					break;

				case "CUSTOM":

					if (filters.containsKey("fromDate") && filters.containsKey("toDate")) {

						fromDate = LocalDate.parse(filters.get("fromDate").toString());

						toDate = LocalDate.parse(filters.get("toDate").toString()).plusDays(1);
					}

					break;
				}
			}

			if (fromDate == null || toDate == null) {

				return cb.conjunction();
			}

			LocalDate finalFromDate = fromDate;
			LocalDate finalToDate = toDate;

			Predicate greaterThan = cb.greaterThanOrEqualTo(root.get("createdAt"), finalFromDate);

			Predicate lessThan = cb.lessThan(root.get("createdAt"), finalToDate);

			return cb.and(greaterThan, lessThan);
		});
	}

	public Specification<NotificationEngineEntity> toNotificationSpecification() {

		return (root, query, cb) -> {

			Specification<NotificationEngineEntity> spec = Specification.allOf();

			if (filters != null) {

				if (filters.containsKey("isRead")) {

					Boolean isRead = Boolean.parseBoolean(filters.get("isRead").toString());

					spec = spec.and((r, q, c) -> c.equal(r.get("isRead"), isRead));
				}

				if (filters.containsKey("search")) {

					String search = filters.get("search").toString().trim();

					if (!search.isBlank()) {

						spec = spec.and((r, q, c) -> c.like(c.lower(r.get("notificationTitle")),
								"%" + search.toLowerCase() + "%"));
					}
				}

				LocalDate fromDate = null;
				LocalDate toDate = null;

				if (filters.containsKey("dateFilter")) {
					String range = filters.get("dateFilter").toString().replace("_", "").toUpperCase();
					LocalDate today = LocalDate.now();

					switch (range) {

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
				}

				if (fromDate != null && toDate != null) {

					LocalDate finalFromDate = fromDate;
					LocalDate finalToDate = toDate;

					spec = spec.and((r, q, c) -> {

						Predicate greaterThan = c.greaterThanOrEqualTo(r.get("notificationSentAt"), finalFromDate);

						Predicate lessThan = c.lessThan(r.get("notificationSentAt"), finalToDate);

						return c.and(greaterThan, lessThan);
					});
				}
			}

			return spec.toPredicate(root, query, cb);
		};
	}

}
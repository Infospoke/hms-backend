package com.hms.service.request;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.NotificationEngineEntity;

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

    private String direction;

    private Map<String, Object> filters;

    public Specification<NotificationEngineEntity> toNotificationSpecification() {

        return (root, query, cb) -> {

        	Specification<NotificationEngineEntity> spec =
        	        Specification.allOf();

            if (filters != null) {

                
                if (filters.containsKey("isRead")) {

                    Boolean isRead = Boolean.parseBoolean(
                            filters.get("isRead").toString()
                    );

                    spec = spec.and((r, q, c) ->
                            c.equal(r.get("isRead"), isRead));
                }

               
                if (filters.containsKey("search")) {

                    String search = filters.get("search")
                            .toString()
                            .trim();

                    if (!search.isBlank()) {

                        spec = spec.and((r, q, c) ->
                                c.like(
                                        c.lower(r.get("notificationTitle")),
                                        "%" + search.toLowerCase() + "%"
                                ));
                    }
                }

               
                LocalDateTime fromDate = null;
                LocalDateTime toDate = null;

                if (filters.containsKey("dateFilter")) {

                    String range = filters.get("dateFilter")
                            .toString()
                            .replace("_", "")
                            .toUpperCase();

                    LocalDate today = LocalDate.now();

                    switch (range) {

                        case "TODAY":

                            fromDate = today.atStartOfDay();
                            toDate = today.plusDays(1).atStartOfDay();
                            break;

                        case "LASTWEEK":

                            LocalDate startOfCurrentWeek =
                                    today.with(DayOfWeek.MONDAY);

                            LocalDate startOfLastWeek =
                                    startOfCurrentWeek.minusWeeks(1);

                            fromDate = startOfLastWeek.atStartOfDay();
                            toDate = startOfCurrentWeek.atStartOfDay();

                            break;

                        case "LASTMONTH":

                            LocalDate firstDayOfLastMonth =
                                    today.minusMonths(1).withDayOfMonth(1);

                            LocalDate firstDayOfThisMonth =
                                    today.withDayOfMonth(1);

                            fromDate = firstDayOfLastMonth.atStartOfDay();
                            toDate = firstDayOfThisMonth.atStartOfDay();

                            break;

                        case "CUSTOM":

                            if (filters.containsKey("fromDate")
                                    && filters.containsKey("toDate")) {

                                fromDate = LocalDate.parse(
                                        filters.get("fromDate").toString()
                                ).atStartOfDay();

                                toDate = LocalDate.parse(
                                        filters.get("toDate").toString()
                                ).plusDays(1).atStartOfDay();
                            }

                            break;
                    }
                }

                
                if (fromDate != null && toDate != null) {

                    LocalDateTime finalFromDate = fromDate;
                    LocalDateTime finalToDate = toDate;

                    spec = spec.and((r, q, c) -> {

                        Predicate greaterThan =
                                c.greaterThanOrEqualTo(
                                        r.get("notificationSentAt"),
                                        finalFromDate
                                );

                        Predicate lessThan =
                                c.lessThan(
                                        r.get("notificationSentAt"),
                                        finalToDate
                                );

                        return c.and(greaterThan, lessThan);
                    });
                }
            }

            return spec.toPredicate(root, query, cb);
        };
    }
    
    
    public Specification<ApprovalChainEntity> toApprovalChainSpecification() {

        return (root, query, cb) -> {

            Specification<ApprovalChainEntity> spec =
                    Specification.allOf();

            if (filters != null) {

                
                if (filters.containsKey("status")) {

                    String status =
                            filters.get("status").toString().trim();

                    if (!status.isBlank()) {

                        spec = spec.and((r, q, c) ->
                                c.like(
                                        c.lower(r.get("status")),
                                        "%" + status.toLowerCase() + "%"
                                ));
                    }
                }

                
                if (filters.containsKey("approval")) {

                    String approval =
                            filters.get("approval").toString().trim();

                    if (!approval.isBlank()) {

                        spec = spec.and((r, q, c) ->
                                c.like(
                                        c.lower(r.get("approval")),
                                        "%" + approval.toLowerCase() + "%"
                                ));
                    }
                }

                // chainName filter
                if (filters.containsKey("chainName")) {

                    String chainName =
                            filters.get("chainName").toString().trim();

                    if (!chainName.isBlank()) {

                        spec = spec.and((r, q, c) ->
                                c.like(
                                        c.lower(r.get("chainName")),
                                        "%" + chainName.toLowerCase() + "%"
                                ));
                    }
                }

               
                LocalDate fromDate = null;
                LocalDate toDate = null;

                if (filters.containsKey("dateFilter")) {

                    String dateFilter = filters.get("dateFilter")
                            .toString()
                            .replace("_", "")
                            .toUpperCase();

                    LocalDate today = LocalDate.now();

                    switch (dateFilter) {

                        case "TODAY":

                            fromDate = today;
                            toDate = today.plusDays(1);
                            break;

                        case "LASTWEEK":

                            LocalDate startOfCurrentWeek =
                                    today.with(DayOfWeek.MONDAY);

                            LocalDate startOfLastWeek =
                                    startOfCurrentWeek.minusWeeks(1);

                            fromDate = startOfLastWeek;
                            toDate = startOfCurrentWeek;
                            break;

                        case "LASTMONTH":

                            LocalDate firstDayOfLastMonth =
                                    today.minusMonths(1).withDayOfMonth(1);

                            LocalDate firstDayOfThisMonth =
                                    today.withDayOfMonth(1);

                            fromDate = firstDayOfLastMonth;
                            toDate = firstDayOfThisMonth;
                            break;

                        case "CUSTOM":

                            if (filters.containsKey("fromDate")
                                    && filters.containsKey("toDate")) {

                                fromDate = LocalDate.parse(
                                        filters.get("fromDate").toString()
                                );

                                toDate = LocalDate.parse(
                                        filters.get("toDate").toString()
                                ).plusDays(1);
                            }

                            break;
                    }
                }

                if (fromDate != null && toDate != null) {

                    LocalDate finalFromDate = fromDate;
                    LocalDate finalToDate = toDate;

                    spec = spec.and((r, q, c) ->
                            c.and(
                                    c.greaterThanOrEqualTo(
                                            r.get("createdAt"),
                                            finalFromDate
                                    ),
                                    c.lessThan(
                                            r.get("createdAt"),
                                            finalToDate
                                    )
                            ));
                }
            }

            return spec.toPredicate(root, query, cb);
        };
    }
}
package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.time.DayOfWeek;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.entity.*;
import com.hms.service.repository.*;
import com.hms.service.request.FilterRequest;
import com.hms.service.service.IKanbanService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

@Service
public class KanbanFilterServiceImpl implements IKanbanService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private SourceStrategyRepository sourcingRepository;

    @Autowired
    private BudgetAndCompensationRepository budgetRepository;

    @Override
    public ApiResponse<?> getFilteredData(FilterRequest request) {

        Map<String, Object> filters = request.getFilters();

        String applicants = filters != null && filters.containsKey("applicants")
                ? filters.get("applicants").toString()
                : null;

        String dateFilter = filters != null && filters.containsKey("dateFilter")
                ? filters.get("dateFilter").toString()
                : null;

        List<String> sources = filters != null && filters.containsKey("sources")
                ? ((List<?>) filters.get("sources")).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())
                : null;

        List<String> sla = filters != null && filters.containsKey("sla")
                ? ((List<?>) filters.get("sla")).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())
                : null;

        List<JobApplicationEntity> jobs = jobApplicationRepository.findAll();

        LocalDate today = LocalDate.now();

        jobs = jobs.stream().filter(job -> {

            if (applicants != null && !"ALL".equalsIgnoreCase(applicants)) {

                if ("REFERRALS".equalsIgnoreCase(applicants)
                        && !Boolean.TRUE.equals(job.getReferral())) {
                    return false;
                }

                if ("NON_REFERRALS".equalsIgnoreCase(applicants)
                        && Boolean.TRUE.equals(job.getReferral())) {
                    return false;
                }
            }

            if (dateFilter != null 
                    && !"ALL".equalsIgnoreCase(dateFilter)
                    && job.getCreatedDate() != null) {

                LocalDate jobDate = job.getCreatedDate().toLocalDate();
                
                String startDateStr = filters != null && filters.containsKey("startDate")
                        ? filters.get("startDate").toString()
                        : null;

                String endDateStr = filters != null && filters.containsKey("endDate")
                        ? filters.get("endDate").toString()
                        : null;

                LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : null;
                LocalDate endDate = endDateStr != null ? LocalDate.parse(endDateStr) : null;

                switch (dateFilter.toLowerCase()) {

                    case "today":
                        if (!jobDate.equals(today)) return false;
                        break;

                    case "custom":
                        if (startDate != null && endDate != null) {
                            if (jobDate.isBefore(startDate) || jobDate.isAfter(endDate)) {
                                return false;
                            }
                        }
                        break;

                    case "last week":
                        LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);
                        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
                        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);
                        if (jobDate.isBefore(startOfLastWeek) || jobDate.isAfter(endOfLastWeek))
                            return false;
                        break;

                    case "last month":
                        LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
                        LocalDate lastDayOfLastMonth = firstDayOfThisMonth.minusDays(1);
                        LocalDate firstDayOfLastMonth = lastDayOfLastMonth.withDayOfMonth(1);
                        if (jobDate.isBefore(firstDayOfLastMonth) || jobDate.isAfter(lastDayOfLastMonth))
                            return false;
                        break;
                }
            }

            if (sources != null && !sources.isEmpty() &&
                    sources.stream().noneMatch(s -> s.equalsIgnoreCase("ALL"))) {

                if (job.getSource() == null) return false;

                boolean match = sources.stream()
                        .anyMatch(src -> src.equalsIgnoreCase(job.getSource()));

                if (!match) return false;
            }

            if (job.getCreatedDate() != null && job.getSlaDays() != null) {

                long days = ChronoUnit.DAYS.between(
                        job.getCreatedDate().toLocalDate(),
                        today
                );

                job.setDaysInStage(days);

                String color = calculateSlaColor(
                        job.getCreatedDate(),
                        job.getSlaDays()
                );

                job.setSlaColor(color);

                if (sla != null && !sla.isEmpty() &&
                        sla.stream().noneMatch(s -> s.equalsIgnoreCase("ALL"))) {

                    boolean match = sla.stream()
                            .anyMatch(f -> f.equalsIgnoreCase(color));

                    if (!match) return false;
                }
            }

            return true;

        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("jobApplications", jobs);

        return ApiResponse.success(
                ResponseCode.SUCCESS,
                "success",
                response
        );
    } 

    	private String calculateSlaColor(LocalDateTime createdDate, Integer slaDays) {

    	    if (createdDate == null || slaDays == null || slaDays == 0) {
    	        return "GREEN";
    	    }

    	    long daysConsumed = ChronoUnit.DAYS.between(
    	            createdDate.toLocalDate(),
    	            LocalDate.now()
    	    );

        double percentage = (double) daysConsumed / slaDays * 100;

        if (percentage < 50) {
            return "GREEN";
        } else if (percentage <= 100) {
            return "ORANGE";
        } else {
            return "RED";
        }
    }
}
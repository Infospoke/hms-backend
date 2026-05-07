package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.entity.*;
import com.hms.service.repository.*;
import com.hms.service.request.FilterRequest;
import com.hms.service.response.KanbanFilterResponse;
import com.hms.service.service.IKanbanService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KanbanFilterServiceImpl implements IKanbanService {

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Override
	public ApiResponse<?> getFilteredData(FilterRequest request) {

		Map<String, Object> filters = request.getFilters();
		boolean isDefault = (filters == null || filters.isEmpty());
		String applicants = filters.get("applicants") != null ? filters.get("applicants").toString() : "ALL";

		String dateFilter = null;

		if (isDefault) {
			dateFilter = "last month";
		} else if (filters.get("dateFilter") != null) {
			dateFilter = filters.get("dateFilter").toString();
		}

		LocalDateTime startDate = null;
		LocalDateTime endDate = null;

		if ("custom".equalsIgnoreCase(dateFilter)) {
			Object startObj = filters.get("startDate");
			Object endObj = filters.get("endDate");

			if (startObj != null && endObj != null) {
				startDate = LocalDate.parse(startObj.toString()).atStartOfDay();
				endDate = LocalDate.parse(endObj.toString()).atTime(23, 59, 59);
			}
		}

		List<String> sources = filters.get("sources") != null ? (List<String>) filters.get("sources") : List.of("ALL");

		List<String> sla = filters.get("sla") != null ? (List<String>) filters.get("sla") : List.of("ALL");

		List<Integer> jobIds = filters.get("jobIds") != null ? ((List<?>) filters.get("jobIds")).stream()
				.map(id -> Integer.valueOf(id.toString())).collect(Collectors.toList()) : new ArrayList<>();

		LocalDateTime now = LocalDateTime.now();

		List<JobApplicationEntity> jobs = jobIds.isEmpty() ? jobApplicationRepository.findAll()
				: jobApplicationRepository.findByJobIdInOrderByStageEntryDateDesc(jobIds);

		Map<String, Integer> counts = new HashMap<>();
		counts.put("shortlisted", 0);
		counts.put("interview", 0);
		counts.put("offer", 0);
		counts.put("hired", 0);
		counts.put("rejected", 0);
		
		List<KanbanFilterResponse> filtered = new ArrayList<>();

		for (JobApplicationEntity job : jobs) {
			if (!jobIds.isEmpty() && !jobIds.contains(job.getJobId())) {
				continue;
			}
			KanbanFilterResponse kanbanFilterResponse = new KanbanFilterResponse();

			String status = job.getCurrentStage();
			LocalDateTime stageEntryDate = job.getStageEntryDate();

			if (Boolean.TRUE.equals(job.getRejected())) {
				status = "REJECTED";
			}
			if ("APPLIED".equalsIgnoreCase(status) || "SCREENED".equalsIgnoreCase(status)) {
				continue;
			}

			if (stageEntryDate != null) {
				long days = ChronoUnit.DAYS.between(stageEntryDate.toLocalDate(), now.toLocalDate());

				kanbanFilterResponse.setDaysInStage(days);

				Integer slaHours = getSlaHoursByStage(status);

				if (slaHours != null && slaHours > 0) {
					double slaDays = slaHours / 24.0;
					double percentage = (days / slaDays) * 100.0;

					kanbanFilterResponse.setDaysInStage(days);

					kanbanFilterResponse.setSlaDays((int) days);

					kanbanFilterResponse.setSlaPercentage(Math.round(percentage * 10.0) / 10.0);

					if (percentage < 50) {
						kanbanFilterResponse.setSlaColor("GREEN");
					} else if (percentage <= 100) {
						kanbanFilterResponse.setSlaColor("ORANGE");
					} else {
						kanbanFilterResponse.setSlaColor("RED");
					}
				}
			} else {
				kanbanFilterResponse.setDaysInStage(null);
				kanbanFilterResponse.setSlaColor(null);
			}

			kanbanFilterResponse.setId(job.getId());
			kanbanFilterResponse.setJobId(job.getJobId());
			kanbanFilterResponse.setFirstName(job.getFirstName());
			kanbanFilterResponse.setLastName(job.getLastName());
			kanbanFilterResponse.setEmail(job.getEmail());
			kanbanFilterResponse.setPhNo(job.getPhNo());
			kanbanFilterResponse.setSource(job.getSource());
			kanbanFilterResponse.setReferral(job.getReferral());
			kanbanFilterResponse.setCurrentStage(status);
			kanbanFilterResponse.setCreatedDate(job.getCreatedDate());
			kanbanFilterResponse.setStageEntryDate(stageEntryDate);
			kanbanFilterResponse.setRejected(job.getRejected());
			
			if (!applyFilters(kanbanFilterResponse, applicants, dateFilter, sources, sla, now, startDate, endDate)) {
				continue;
			}

			if (Boolean.TRUE.equals(job.getRejected())) {
				status = "REJECTED";
			}

			if (status == null) {
				continue;
			}
			
			switch (status.toUpperCase()) {
			case "SHORTLISTED" -> counts.put("shortlisted", counts.get("shortlisted") + 1);
			case "INTERVIEW" -> counts.put("interview", counts.get("interview") + 1);
			case "OFFER" -> counts.put("offer", counts.get("offer") + 1);
			case "HIRED" -> counts.put("hired", counts.get("hired") + 1);
			case "REJECTED" -> counts.put("rejected", counts.get("rejected") + 1);
			}
			filtered.add(kanbanFilterResponse);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("jobApplications", filtered);
		response.put("counts", counts);

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}
	
	private Integer getSlaHoursByStage(String stage) {
		if (stage == null)
			return null;

		return switch (stage.toUpperCase()) {
		case "SHORTLISTED" -> 48;
		case "INTERVIEW" -> 72;
		case "OFFER" -> 120;
		default -> null;
		};
	}

	private boolean applyFilters(KanbanFilterResponse kanbanFilterResponse, String applicants, String dateFilter, List<String> sources,
			List<String> sla, LocalDateTime now, LocalDateTime startDate, LocalDateTime endDate) {

		if (sla != null && !sla.isEmpty()&& sla.stream().noneMatch(s -> "all".equalsIgnoreCase(s))) {
			if (kanbanFilterResponse.getSlaColor() == null || sla.stream().noneMatch(s -> s.equalsIgnoreCase(kanbanFilterResponse.getSlaColor()))) {
				return false;
			}
		}

		if (sources != null && !sources.isEmpty()&& sources.stream().noneMatch(s -> "all".equalsIgnoreCase(s))){
			if (kanbanFilterResponse.getSource() == null || sources.stream().noneMatch(s -> s.equalsIgnoreCase(kanbanFilterResponse.getSource()))) {
				return false;

			}
		}

		if ("referral".equalsIgnoreCase(applicants)
		        || "referrals".equalsIgnoreCase(applicants)){
			
			if (!Boolean.TRUE.equals(kanbanFilterResponse.getReferral()))
				return false;
			
		}else if ("non-referral".equalsIgnoreCase(applicants)
		        || "non-referrals".equalsIgnoreCase(applicants)) {
			
			if (Boolean.TRUE.equals(kanbanFilterResponse.getReferral()))
				return false;
		}

		if (dateFilter != null && kanbanFilterResponse.getStageEntryDate() != null) {

		    LocalDateTime date = kanbanFilterResponse.getStageEntryDate();

			switch (dateFilter.toLowerCase()) {

			case "today" -> {
				if (!date.toLocalDate().equals(now.toLocalDate()))
					return false;
			}

			case "last week" -> {
				LocalDate today = now.toLocalDate();
				LocalDate start = today.minusWeeks(1).with(DayOfWeek.MONDAY);
				LocalDate end = start.plusDays(6);

				if (date.toLocalDate().isBefore(start) || date.toLocalDate().isAfter(end)) {
					return false;
				}
			}

			case "last month" -> {
				LocalDate today = now.toLocalDate();
				LocalDate start = today.minusMonths(1).withDayOfMonth(1);
				LocalDate end = start.plusMonths(1).minusDays(1);

				if (date.toLocalDate().isBefore(start) || date.toLocalDate().isAfter(end)) {
					return false;
				}
			}
			case "custom" -> {
				if (startDate != null && endDate != null) {
					if (date.isBefore(startDate) || date.isAfter(endDate))
						return false;
				}
			}
			}
		}

		return true;
	}

	public void rejectCandidate(Integer applicationId) {

		JobApplicationEntity job = jobApplicationRepository.findById(applicationId)
				.orElseThrow(() -> new RuntimeException("Not found"));

		job.setCurrentStage("REJECTED");
		job.setStageEntryDate(LocalDateTime.now());

		jobApplicationRepository.save(job);
	}
}
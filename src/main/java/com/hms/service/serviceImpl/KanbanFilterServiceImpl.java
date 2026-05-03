package com.hms.service.serviceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.entity.*;
import com.hms.service.repository.*;
import com.hms.service.request.FilterRequest;
import com.hms.service.service.IKanbanService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KanbanFilterServiceImpl implements IKanbanService {

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private ResumeAnalysisRepository resumeAnalysisRepository;

	@Autowired
	private InterviewAnalysisRepository interviewAnalysisRepository;

	@Autowired
	private CandidateCreationRepository candidateCreationRepository;

	@Override
	public ApiResponse<?> getFilteredData(FilterRequest request) {
		
		log.info("kanbanFilterServiceImpl: Inside getFilteredData method");
		Map<String, Object> filters = request.getFilters();

		String applicants = filters.get("applicants") != null ? filters.get("applicants").toString() : "ALL";

		String dateFilter = filters.get("dateFilter") != null ? filters.get("dateFilter").toString() : "last month";

		List<String> sources = filters.get("sources") != null ? (List<String>) filters.get("sources")
				: Collections.emptyList();

		List<String> sla = filters.get("sla") != null ? (List<String>) filters.get("sla") : Collections.emptyList();

		List<Integer> jobIds = filters.get("jobIds") != null ? ((List<?>) filters.get("jobIds")).stream()
				.map(id -> Integer.valueOf(id.toString())).collect(Collectors.toList()) : new ArrayList<>();

		LocalDateTime now = LocalDateTime.now();

		List<JobApplicationEntity> jobs;

		if (jobIds != null && !jobIds.isEmpty()) {
			jobs = jobApplicationRepository.findByJobIdInOrderByCreatedDateDesc(jobIds);
		} else {
			jobs = jobApplicationRepository.findAll();
		}

		List<Integer> appIds = jobs.stream().map(JobApplicationEntity::getId).toList();

		Map<Integer, String> resumeStatusMap = new HashMap<>();
		Map<Integer, Boolean> resumeSuccessMap = new HashMap<>();
		Map<Integer, LocalDateTime> resumeDateMap = new HashMap<>();

		List<Object[]> resumeData = resumeAnalysisRepository.findResumeDetails(appIds);
		for (Object[] r : resumeData) {

			Integer appId = ((Number) r[0]).intValue();
			String status = (String) r[1];
			LocalDateTime createdAt = (LocalDateTime) r[3];
			resumeStatusMap.put(appId, status);
			resumeDateMap.put(appId, createdAt);
		}

		Set<Integer> interviewSet = new HashSet<>(interviewAnalysisRepository.findInterviewIds(appIds));

		Map<Integer, LocalDateTime> interviewDateMap = new HashMap<>();
		List<Object[]> interviewData = interviewAnalysisRepository.findInterviewDates(appIds);

		for (Object[] i : interviewData) {

			Integer appId = (Integer) i[0];
			LocalDateTime createdDate = (LocalDateTime) i[1];
			interviewDateMap.put(appId, createdDate);
		}

		Map<Integer, String> candidateMap = new HashMap<>();
		Map<Integer, LocalDateTime> candidateDateMap = new HashMap<>();

		List<Object[]> candidateData = candidateCreationRepository.findCandidateDetails(appIds);

		for (Object[] c : candidateData) {
			Integer appId = (Integer) c[0];
			String status = (String) c[1];
			LocalDateTime acceptedDate = (LocalDateTime) c[2];
			candidateMap.put(appId, status);
			candidateDateMap.put(appId, acceptedDate);
		}

		log.info("==== RESUME MAP ====");
		resumeStatusMap.forEach((k, v) -> System.out.println(k + " -> " + v));

		log.info("==== INTERVIEW SET ====");
		System.out.println(interviewSet);

		log.info("==== CANDIDATE MAP ====");
		candidateMap.forEach((k, v) -> System.out.println(k + " -> " + v));

		Map<String, Integer> counts = new HashMap<>();
		counts.put("shortlisted", 0);
		counts.put("interview", 0);
		counts.put("offer", 0);
		counts.put("hired", 0);

		List<JobApplicationEntity> filtered = new ArrayList<>();

		for (JobApplicationEntity job : jobs) {

			Integer id = job.getId();
			String status = "APPLIED";
			LocalDateTime stageEntryDate = null;

			if (candidateMap.containsKey(id)) {

				String cStatus = candidateMap.get(id);

				if ("JOINED".equalsIgnoreCase(cStatus)) {
					status = "HIRED";
				} else if ("ACCEPTED".equalsIgnoreCase(cStatus)) {
					status = "OFFER";
					stageEntryDate = candidateDateMap.get(id);
				}

			} else if (interviewSet.contains(id)) {
				status = "INTERVIEW";
				stageEntryDate = interviewDateMap.get(id);

			} else if (resumeStatusMap.containsKey(id)) {

				String rStatus = resumeStatusMap.get(id);

				if (rStatus != null && rStatus.equalsIgnoreCase("SHORTLISTED")) {
					status = "SHORTLISTED";
				} else {
					status = "SCREENED";
				}
				stageEntryDate = resumeDateMap.get(id);
			}

			log.info("Checking ID: " + id);
			log.info("In Resume Map: " + resumeStatusMap.containsKey(id));
			log.info("In Interview Set: " + interviewSet.contains(id));
			log.info("In Candidate Map: " + candidateMap.containsKey(id));

			job.setCurrentStage(status);
			job.setStageEntryDate(stageEntryDate);
			jobApplicationRepository.save(job);

			if (stageEntryDate != null && !"HIRED".equalsIgnoreCase(status) && !"APPLIED".equalsIgnoreCase(status)
					&& !"SCREENED".equalsIgnoreCase(status) && !"REJECTED".equalsIgnoreCase(status)) {

				long hours = Duration.between(stageEntryDate, now).toHours();
				Integer slaHours = getSlaHoursByStage(status);

				if (slaHours != null) {

					double days = hours / 24.0;
					double slaDays = slaHours / 24.0;
					double percentage = (days / slaDays) * 100;

					job.setDaysInStage((long) days);

					if (percentage < 50) {
						job.setSlaColor("GREEN");
					} else if (percentage <= 100) {
						job.setSlaColor("ORANGE");
					} else {
						job.setSlaColor("RED");
					}
				}
				System.out.println("Candidate: " + job.getFirstName() + " | Stage: " + status + " | Source: "
						+ job.getSource() + " | SLA: " + job.getSlaColor());
			} else {
				job.setSlaColor(null);
				job.setDaysInStage(null);
			}

			if ("APPLIED".equalsIgnoreCase(status) || "SCREENED".equalsIgnoreCase(status)) {
				continue;
			}

			// counts.put(status.toLowerCase(), counts.getOrDefault(status.toLowerCase(), 0)
			// + 1);

			if (!applyFilters(job, applicants, dateFilter, sources, sla, now)) {
				System.out.println("Filtering OUT: " + job.getFirstName());
				continue;
			}
			// counts.put(status.toLowerCase(), counts.getOrDefault(status.toLowerCase(), 0)
			// + 1);

            // if want count for each stage 
			if ("SHORTLISTED".equalsIgnoreCase(status)) {
				counts.put("shortlisted", counts.getOrDefault("shortlisted", 0) + 1);

			} else if ("INTERVIEW".equalsIgnoreCase(status)) {
				counts.put("shortlisted", counts.getOrDefault("shortlisted", 0) + 1);
				counts.put("interview", counts.getOrDefault("interview", 0) + 1);

			} else if ("OFFER".equalsIgnoreCase(status)) {
				counts.put("shortlisted", counts.getOrDefault("shortlisted", 0) + 1);
				counts.put("interview", counts.getOrDefault("interview", 0) + 1);
				counts.put("offer", counts.getOrDefault("offer", 0) + 1);

			} else if ("HIRED".equalsIgnoreCase(status)) {
				counts.put("shortlisted", counts.getOrDefault("shortlisted", 0) + 1);
				counts.put("interview", counts.getOrDefault("interview", 0) + 1);
				counts.put("hired", counts.getOrDefault("hired", 0) + 1);
			}

			filtered.add(job);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("jobApplications", filtered);
		response.put("counts", counts);

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	private Integer getSlaHoursByStage(String stage) {
		if (stage == null)
			return null;

		switch (stage.toUpperCase()) {
		case "SHORTLISTED":
			return 48;
		case "INTERVIEW":
			return 72;
		case "OFFER":
			return 120;
		default:
			return null;
		}
	}

	private boolean applyFilters(JobApplicationEntity job, String applicants, String dateFilter, List<String> sources,
			List<String> sla, LocalDateTime now) {

		if (!sla.isEmpty()) {
			if (job.getSlaColor() == null || sla.stream().map(String::toUpperCase)
					.noneMatch(s -> s.equals(job.getSlaColor().toUpperCase()))) {
				return false;
			}
		}
		if (!sources.isEmpty()) {
			if (job.getSource() == null
					|| !sources.stream().map(String::toLowerCase).toList().contains(job.getSource().toLowerCase())) {
				return false;
			}
		}

		if ("referrals".equalsIgnoreCase(applicants)) {
			if (!Boolean.TRUE.equals(job.getReferral()))
				return false;
		} else if ("non_referrals".equalsIgnoreCase(applicants)) {
			if (Boolean.TRUE.equals(job.getReferral()))
				return false;
		}

		if (dateFilter != null && job.getCreatedDate() != null) {

			LocalDateTime created = job.getCreatedDate() ;

			switch (dateFilter.toLowerCase()) {

			case "today":
				if (!created.toLocalDate().equals(now.toLocalDate()))
					return false;
				break;
			case "this week":
				LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
				if (created.isBefore(startOfWeek)) {
					return false;
				}
				break;
			case "last week":
				LocalDateTime startLastWeek = now.minusWeeks(1).with(DayOfWeek.MONDAY).withHour(0).withMinute(0)
						.withSecond(0);

				LocalDateTime endLastWeek = startLastWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
				if (created.isBefore(startLastWeek) || created.isAfter(endLastWeek))
					return false;
				break;

			case "last month":
				LocalDateTime start = now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
				LocalDateTime end = start.plusMonths(1);
				if (created.isBefore(start) || created.isAfter(end)) {
					return false;
				}
				break;
			}
		}
		log.info("kanbanFilterServiceImpl: Exit from getFilteredData method");
		return true;
	}
}
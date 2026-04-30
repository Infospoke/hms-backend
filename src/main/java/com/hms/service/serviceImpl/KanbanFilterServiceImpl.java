package com.hms.service.serviceImpl;

import java.time.Duration;
import java.time.LocalDate;
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

@Service
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

		Map<String, Object> filters = request.getFilters();

		String applicants = filters.get("applicants") != null
		        ? filters.get("applicants").toString()
		        : "ALL";

		List<String> sources = filters.get("sources") != null
		        ? (List<String>) filters.get("sources")
		        : new ArrayList<>();

		List<String> sla = filters.get("sla") != null
		        ? (List<String>) filters.get("sla")
		        : new ArrayList<>();

		String dateFilter = filters.get("dateFilter") != null
		        ? filters.get("dateFilter").toString()
		        : "last month"; 

		Integer jobId = filters.get("jobId") != null ? Integer.valueOf(filters.get("jobId").toString()) : null;

		LocalDateTime now = LocalDateTime.now();

		List<JobApplicationEntity> jobs = (jobId != null) ? jobApplicationRepository.findByJobIdOrderByCreatedDateDesc(jobId)
				: jobApplicationRepository.findAll();

		List<Integer> appIds = jobs.stream().map(JobApplicationEntity::getId).collect(Collectors.toList());

		Map<Integer, String> resumeStatusMap = new HashMap<>();
		Map<Integer, Boolean> resumeSuccessMap = new HashMap<>();
		Map<Integer, LocalDateTime> resumeDateMap = new HashMap<>();

		List<Object[]> resumeData = resumeAnalysisRepository.findResumeDetails(appIds);
		for (Object[] r : resumeData) {
			Integer appId = (Integer) r[0];
			resumeStatusMap.put(appId, (String) r[1]);
			resumeSuccessMap.put(appId, (Boolean) r[2]);
			resumeDateMap.put(appId, (LocalDateTime) r[3]);
		}

		Set<Integer> interviewSet = new HashSet<>(interviewAnalysisRepository.findInterviewIds(appIds));

		Map<Integer, LocalDateTime> interviewDateMap = new HashMap<>();
		List<Object[]> interviewData = interviewAnalysisRepository.findInterviewDates(appIds);
		for (Object[] i : interviewData) {
			interviewDateMap.put((Integer) i[0], (LocalDateTime) i[1]);
		}

		Map<Integer, String> candidateMap = new HashMap<>();
		Map<Integer, LocalDateTime> candidateDateMap = new HashMap<>();

		List<Object[]> candidateData = candidateCreationRepository.findCandidateDetails(appIds);
			for (Object[] c : candidateData) {
			candidateMap.put((Integer) c[0], (String) c[1]);
			candidateDateMap.put((Integer) c[0], (LocalDateTime) c[2]);
		}

		Map<String, Integer> counts = new HashMap<>();
		counts.put("applied", 0);
		counts.put("screened", 0);
		counts.put("shortlisted", 0);
		counts.put("interview", 0);
		counts.put("offer", 0);
		counts.put("hired", 0);

		List<JobApplicationEntity> filtered = new ArrayList<>();

		for (JobApplicationEntity job : jobs) {

			Integer id = job.getId();
			String status = "APPLIED";
			LocalDateTime baseDate = null;

			if (candidateMap.containsKey(id)) {

				String cStatus = candidateMap.get(id);

				if ("JOINED".equalsIgnoreCase(cStatus)) {
					status = "HIRED";
				} else if ("ACCEPTED".equalsIgnoreCase(cStatus)) {
					status = "OFFER";
					baseDate = candidateDateMap.get(id);
				}

			} else if (interviewSet.contains(id)) {
				status = "INTERVIEW";
				baseDate = interviewDateMap.get(id);

			} else if (resumeStatusMap.containsKey(id)) {

				String rStatus = resumeStatusMap.get(id);

				if ("SHORTLISTED".equalsIgnoreCase(rStatus)) {
					status = "SHORTLISTED";
				} else {
					status = "SCREENED";
				}

				baseDate = resumeDateMap.get(id);
			}

			job.setCurrentStage(status);
			job.setStageEntryDate(baseDate);

			if (baseDate != null && !"HIRED".equals(status) && !"APPLIED".equals(status)) {

				long hours = Duration.between(baseDate, now).toHours();
				Integer slaHours = getSlaHoursByStage(status);

				if (slaHours != null) {
					job.setDaysInStage(Long.valueOf(hours / 24));

					if (hours < slaHours * 0.75) {
						job.setSlaColor("GREEN");
					} else if (hours < slaHours) {
						job.setSlaColor("ORANGE");
					} else {
						job.setSlaColor("RED");
					}
				}
			} else {
				job.setSlaColor(null);
				job.setDaysInStage(null);
			}

			counts.put(status.toLowerCase(), counts.getOrDefault(status.toLowerCase(), 0) + 1);

			if (!applyFilters(job, applicants, dateFilter, sources, sla, now)) {
				continue;
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
		case "SCREENED":
			return 48;
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

		if (sla != null && !sla.isEmpty()) {
			if (job.getSlaColor() == null || !sla.contains(job.getSlaColor().toUpperCase())) {
				return false;
			}
		}

		if (sources != null && !sources.isEmpty()) {
			if (job.getSource() == null || !sources.contains(job.getSource().toLowerCase())) {
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

			LocalDateTime created = job.getCreatedDate();

			switch (dateFilter.toLowerCase()) {

			case "today":
				if (!created.toLocalDate().equals(now.toLocalDate()))
					return false;
				break;

			case "this week":
				if (created.isBefore(now.with(DayOfWeek.MONDAY)))
					return false;
				break;

			case "last week":
				LocalDateTime startLastWeek = now.minusWeeks(1).with(DayOfWeek.MONDAY);
				LocalDateTime endLastWeek = now.with(DayOfWeek.MONDAY);
				if (created.isBefore(startLastWeek) || created.isAfter(endLastWeek))
					return false;
				break;
				
			case "last month":

			    LocalDateTime start = now.minusMonths(1)
			            .withDayOfMonth(1)
			            .withHour(0).withMinute(0).withSecond(0);

			    LocalDateTime end = start.plusMonths(1);

			    if (created.isBefore(start) || created.isAfter(end)) {
			        return false;
			    }
			    break;
			}
		}

		return true;
	}
}
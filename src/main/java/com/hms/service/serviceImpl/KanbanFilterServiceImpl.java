//package com.hms.service.serviceImpl;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.DayOfWeek;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.hms.service.entity.*;
//import com.hms.service.repository.*;
//import com.hms.service.request.FilterRequest;
//import com.hms.service.service.IKanbanService;
//import com.hms.service.wrappers.ApiResponse;
//import com.hms.service.wrappers.ResponseCode;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@Slf4j
//public class KanbanFilterServiceImpl implements IKanbanService {
//
//	@Autowired
//	private JobApplicationRepository jobApplicationRepository;
//
//	@Autowired
//	private ResumeAnalysisRepository resumeAnalysisRepository;
//
//	@Autowired
//	private InterviewAnalysisRepository interviewAnalysisRepository;
//
//	@Autowired
//	private CandidateCreationRepository candidateCreationRepository;
//
//	@Override
//	public ApiResponse<?> getFilteredData(FilterRequest request) {
//		
//		log.info("kanbanFilterServiceImpl: Inside getFilteredData method");
//		Map<String, Object> filters = request.getFilters();
//
//		String applicants = filters.get("applicants") != null ? filters.get("applicants").toString() : "ALL";
//
//		String dateFilter = filters.get("dateFilter") != null ? filters.get("dateFilter").toString() : "last month";
//		
//		LocalDateTime startDate = null;
//		LocalDateTime endDate = null;
//
//		if ("custom".equalsIgnoreCase(dateFilter)) {
//		    Object startObj = filters.get("startDate");
//		    Object endObj = filters.get("endDate");
//
//		    if (startObj != null && endObj != null) {
//		        startDate = java.time.LocalDate.parse(startObj.toString()).atStartOfDay();
//
//		        endDate = java.time.LocalDate.parse(endObj.toString())
//		                .atTime(23, 59, 59);
//		    }
//		}
//
//		List<String> sources = filters.get("sources") != null 
//			    ? (List<String>) filters.get("sources") 
//			    : List.of("ALL");
//
//			List<String> sla = filters.get("sla") != null 
//			    ? (List<String>) filters.get("sla") 
//			    : List.of("ALL");
//
//		List<Integer> jobIds = filters.get("jobIds") != null ? ((List<?>) filters.get("jobIds")).stream()
//				.map(id -> Integer.valueOf(id.toString())).collect(Collectors.toList()) : new ArrayList<>();
//
//		LocalDateTime now = LocalDateTime.now();
//
//		List<JobApplicationEntity> jobs;
//
//		if (jobIds != null && !jobIds.isEmpty()) {
//			jobs = jobApplicationRepository.findByJobIdInOrderByCreatedDateDesc(jobIds);
//		} else {
//			jobs = jobApplicationRepository.findAll();
//		}
//
//		List<Integer> appIds = jobs.stream().map(JobApplicationEntity::getId).toList();
//
//		Map<Integer, String> resumeStatusMap = new HashMap<>();
//		Map<Integer, Boolean> resumeSuccessMap = new HashMap<>();
//		Map<Integer, LocalDateTime> resumeDateMap = new HashMap<>();
//
//		List<Object[]> resumeData = resumeAnalysisRepository.findResumeDetails(appIds);
//		for (Object[] r : resumeData) {
//
//			Integer appId = ((Number) r[0]).intValue();
//			String status = (String) r[1];
//			LocalDateTime createdAt = (LocalDateTime) r[3];
//			resumeStatusMap.put(appId, status);
//			resumeDateMap.put(appId, createdAt);
//		}
//
//		Set<Integer> interviewSet = new HashSet<>(interviewAnalysisRepository.findInterviewIds(appIds));
//
//		Map<Integer, LocalDateTime> interviewDateMap = new HashMap<>();
//		List<Object[]> interviewData = interviewAnalysisRepository.findInterviewDates(appIds);
//
//		for (Object[] i : interviewData) {
//
//			Integer appId = (Integer) i[0];
//			LocalDateTime createdDate = (LocalDateTime) i[1];
//			interviewDateMap.put(appId, createdDate);
//		}
//
//		Map<Integer, String> candidateMap = new HashMap<>();
//		Map<Integer, LocalDateTime> candidateDateMap = new HashMap<>();
//
//		List<Object[]> candidateData = candidateCreationRepository.findCandidateDetails(appIds);
//
//		for (Object[] c : candidateData) {
//			Integer appId = (Integer) c[0];
//			String status = (String) c[1];
//			LocalDateTime acceptedDate = (LocalDateTime) c[2];
//			candidateMap.put(appId, status);
//			candidateDateMap.put(appId, acceptedDate);
//		}
//
//
//		Map<String, Integer> counts = new HashMap<>();
//		counts.put("shortlisted", 0);
//		counts.put("interview", 0);
//		counts.put("offer", 0);
//		counts.put("hired", 0);
//		counts.put("rejected", 0);
//
//		List<JobApplicationEntity> filtered = new ArrayList<>();
//
//		for (JobApplicationEntity job : jobs) {
//			
//			Integer id = job.getId();
//			String status = job.getCurrentStage();
//			LocalDateTime stageEntryDate = job.getStageEntryDate();
//			if (Boolean.TRUE.equals(job.getRejected())) {
//
//			    status = "REJECTED";
//
//			} else if (candidateMap.containsKey(id)) {
//			    String cStatus = candidateMap.get(id);
//
//			    if ("JOINED".equalsIgnoreCase(cStatus)) {
//			        status = "HIRED";
//			        stageEntryDate = candidateDateMap.get(id);
//
//			    } else if ("ACCEPTED".equalsIgnoreCase(cStatus)) {
//			        status = "OFFER";
//			        stageEntryDate = candidateDateMap.get(id);
//			    }
//
//			} else if (interviewSet.contains(id)) {
//			    status = "INTERVIEW";
//			    stageEntryDate = interviewDateMap.get(id);
//
//			} else if (resumeStatusMap.containsKey(id)) {
//			    String rStatus = resumeStatusMap.get(id);
//
//			    if ("SHORTLISTED".equalsIgnoreCase(rStatus)) {
//			        status = "SHORTLISTED";
//			        stageEntryDate = resumeDateMap.get(id);
//			    }
//			}
//
//			job.setCurrentStage(status);
//			job.setStageEntryDate(stageEntryDate);
//			
//			
//			if (stageEntryDate != null && !"HIRED".equalsIgnoreCase(status) && !"APPLIED".equalsIgnoreCase(status)
//					&& !"SCREENED".equalsIgnoreCase(status) && !"REJECTED".equalsIgnoreCase(status)) {
//
//				long hours = Duration.between(stageEntryDate, now).toHours();
//				Integer slaHours = getSlaHoursByStage(status);
//
//				if (slaHours != null) {
//
//					double days = hours / 24.0;
//					double slaDays = slaHours / 24.0;
//					double percentage = (days / slaDays) * 100;
//
//					job.setDaysInStage(Math.round(days));
//
//					if (percentage < 50) {
//						job.setSlaColor("GREEN");
//					} else if (percentage <= 100) {
//						job.setSlaColor("ORANGE");
//					} else {
//						job.setSlaColor("RED");
//					}
//				}
//				System.out.println("Candidate: " + job.getFirstName() + " | Stage: " + status + " | Source: "
//						+ job.getSource() + " | SLA: " + job.getSlaColor());
//			} else {
//				job.setSlaColor(null);
//				job.setDaysInStage(null);
//			}
//
//			if ("APPLIED".equalsIgnoreCase(status) || "SCREENED".equalsIgnoreCase(status)) {
//				continue;
//			}
//
////			if (!applyFilters(job, applicants, dateFilter, sources, sla, now))
//			if (!applyFilters(job, applicants, dateFilter, sources, sla, now, startDate, endDate)){
//				System.out.println("Filtering OUT: " + job.getFirstName());
//				continue;
//			}
//			
//			if (Boolean.TRUE.equals(job.getRejected())) {
//			    status = "REJECTED";
//			} else {
//			    status = job.getCurrentStage();
//			}
//			if ("SHORTLISTED".equalsIgnoreCase(status)) {
//			    counts.put("shortlisted", counts.get("shortlisted") + 1);
//
//			} else if ("INTERVIEW".equalsIgnoreCase(status)) {
//			    counts.put("interview", counts.get("interview") + 1);
//
//			} else if ("OFFER".equalsIgnoreCase(status)) {
//			    counts.put("offer", counts.get("offer") + 1);
//
//			} else if ("HIRED".equalsIgnoreCase(status)) {
//			    counts.put("hired", counts.get("hired") + 1);
//
//			} else if ("REJECTED".equalsIgnoreCase(status)) {
//			    counts.put("rejected", counts.get("rejected") + 1);
//			}
//			filtered.add(job);
//		}
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("jobApplications", filtered);
//		response.put("counts", counts);
//
//		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
//	}
//
//	private Integer getSlaHoursByStage(String stage) {
//		if (stage == null)
//			return null;
//
//		switch (stage.toUpperCase()) {
//		case "SHORTLISTED":
//			return 48;
//		case "INTERVIEW":
//			return 72;
//		case "OFFER":
//			return 120;
//		default:
//			return null;
//		}
//	}
//
//	private boolean applyFilters(JobApplicationEntity job, String applicants, String dateFilter, List<String> sources,
//			List<String> sla, LocalDateTime now, LocalDateTime startDate, LocalDateTime endDate) {
//
//		if (!sla.isEmpty()) {
//			if (job.getSlaColor() == null || sla.stream().map(String::toUpperCase)
//					.noneMatch(s -> s.equals(job.getSlaColor().toUpperCase()))) {
//				return false;
//			}
//		}
//		if (!sources.isEmpty()) {
//			if (job.getSource() == null
//					|| !sources.stream().map(String::toLowerCase).toList().contains(job.getSource().toLowerCase())) {
//				return false;
//			}
//		}
//
//		if ("referrals".equalsIgnoreCase(applicants)) {
//			if (!Boolean.TRUE.equals(job.getReferral()))
//				return false;
//		} else if ("non_referrals".equalsIgnoreCase(applicants)) {
//			if (Boolean.TRUE.equals(job.getReferral()))
//				return false;
//		}
//
//		if (dateFilter != null && job.getCreatedDate() != null) {
//
//			LocalDateTime created = job.getCreatedDate() ;
//
//			switch (dateFilter.toLowerCase()) {
//
//			case "today":
//				if (!created.toLocalDate().equals(now.toLocalDate()))
//					return false;
//				break;
//			case "this week":
//				LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
//				if (created.isBefore(startOfWeek)) {
//					return false;
//				}
//				break;
//			case "last week":
//				LocalDateTime startLastWeek = now.minusWeeks(1).with(DayOfWeek.MONDAY).withHour(0).withMinute(0)
//						.withSecond(0);
//
//				LocalDateTime endLastWeek = startLastWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
//				if (created.isBefore(startLastWeek) || created.isAfter(endLastWeek))
//					return false;
//				break;
//
//			case "last month":
//				LocalDateTime start = now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
//				LocalDateTime end = start.plusMonths(1).minusSeconds(1);
//				if (created.isBefore(start) || created.isAfter(end)) {
//					return false;
//				}
//				break;
//				
//			case "custom":
//			    if (startDate != null && endDate != null) {
//			        if (created.isBefore(startDate) || created.isAfter(endDate)) {
//			            return false;
//			        }
//			    }
//			    break;
//			}
//		}
//		log.info("kanbanFilterServiceImpl: Exit from getFilteredData method");
//		return true;
//	}
//	public void rejectCandidate(Integer applicationId) {
//
//	    JobApplicationEntity job = jobApplicationRepository.findById(applicationId)
//	        .orElseThrow(() -> new RuntimeException("Not found"));
//
//	    job.setCurrentStage("REJECTED");
//	    job.setStageEntryDate(LocalDateTime.now());
//
//	    jobApplicationRepository.save(job);
//	}
//}


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

        Map<String, Object> filters = request.getFilters();
        boolean isDefault = (filters == null || filters.isEmpty());
        String applicants = filters.get("applicants") != null
                ? filters.get("applicants").toString()
                : "ALL";

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

        List<String> sources = filters.get("sources") != null
                ? (List<String>) filters.get("sources")
                : List.of("ALL");

        List<String> sla = filters.get("sla") != null
                ? (List<String>) filters.get("sla")
                : List.of("ALL");

        List<Integer> jobIds = filters.get("jobIds") != null
                ? ((List<?>) filters.get("jobIds")).stream()
                        .map(id -> Integer.valueOf(id.toString()))
                        .collect(Collectors.toList())
                : new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        List<JobApplicationEntity> jobs = jobIds.isEmpty()
                ? jobApplicationRepository.findAll()
                : jobApplicationRepository.findByJobIdInOrderByCreatedDateDesc(jobIds);

        List<Integer> appIds = jobs.stream().map(JobApplicationEntity::getId).toList();

        Map<Integer, String> resumeStatusMap = new HashMap<>();
        Map<Integer, LocalDateTime> resumeDateMap = new HashMap<>();

        for (Object[] r : resumeAnalysisRepository.findResumeDetails(appIds)) {
            Integer appId = ((Number) r[0]).intValue();
            resumeStatusMap.put(appId, (String) r[1]);
            resumeDateMap.put(appId, (LocalDateTime) r[3]);
        }

        Set<Integer> interviewSet = new HashSet<>(interviewAnalysisRepository.findInterviewIds(appIds));

        Map<Integer, LocalDateTime> interviewDateMap = new HashMap<>();
        for (Object[] i : interviewAnalysisRepository.findInterviewDates(appIds)) {
            interviewDateMap.put((Integer) i[0], (LocalDateTime) i[1]);
        }

//        Map<Integer, String> candidateMap = new HashMap<>();
//        Map<Integer, LocalDateTime> candidateDateMap = new HashMap<>();
//
//        for (Object[] c : candidateCreationRepository.findCandidateDetails(appIds)) {
//            candidateMap.put((Integer) c[0], (String) c[1]);
//            candidateDateMap.put((Integer) c[0], (LocalDateTime) c[2]);
//        }
     
        // to get created date and updated from candidate info
        
        Map<Integer, String> candidateMap = new HashMap<>();
        Map<Integer, LocalDateTime> candidateCreatedDateMap = new HashMap<>();
        Map<Integer, LocalDateTime> candidateUpdatedDateMap = new HashMap<>();

        for (Object[] c : candidateCreationRepository.findCandidateDetails(appIds)) {

            Integer appId = (Integer) c[0];
            String status = (String) c[1];

            LocalDateTime createdDate = (LocalDateTime) c[2];
            LocalDateTime updatedDate = (LocalDateTime) c[3];

            candidateMap.put(appId, status);
            candidateCreatedDateMap.put(appId, createdDate);
            candidateUpdatedDateMap.put(appId, updatedDate);
        }

        Map<String, Integer> counts = new HashMap<>();
        counts.put("shortlisted", 0);
        counts.put("interview", 0);
        counts.put("offer", 0);
        counts.put("hired", 0);
        counts.put("rejected", 0);

        List<JobApplicationEntity> filtered = new ArrayList<>();

        for (JobApplicationEntity job : jobs) {
        	if (!jobIds.isEmpty() && !jobIds.contains(job.getJobId())) {
        	    continue;
        	}

            Integer id = job.getId();

            String status = job.getCurrentStage();
            LocalDateTime stageEntryDate = job.getStageEntryDate();

               if (Boolean.TRUE.equals(job.getRejected())) {
                   status = "REJECTED";
               }
            if ("APPLIED".equalsIgnoreCase(status) || "SCREENED".equalsIgnoreCase(status)) {
                continue;
            }
            
            if ("INTERVIEW".equalsIgnoreCase(status) && interviewSet.contains(id)) {
                stageEntryDate = interviewDateMap.get(id);

            } else if ("SHORTLISTED".equalsIgnoreCase(status) && resumeStatusMap.containsKey(id)) {
                stageEntryDate = resumeDateMap.get(id);

            } else if ("HIRED".equalsIgnoreCase(status) && candidateMap.containsKey(id)) {

                String cStatus = candidateMap.get(id);

                if ("JOINED".equalsIgnoreCase(cStatus)) {
                    stageEntryDate = candidateUpdatedDateMap.get(id);

                } else if ("ACCEPTED".equalsIgnoreCase(cStatus)) {
                    stageEntryDate = candidateCreatedDateMap.get(id);
                }
            }

            job.setCurrentStage(status);
            job.setStageEntryDate(stageEntryDate);

            if (stageEntryDate != null) {
                long days = ChronoUnit.DAYS.between(
                        stageEntryDate.toLocalDate(),
                        now.toLocalDate());

                job.setDaysInStage(days);

                Integer slaHours = getSlaHoursByStage(status);

                if (slaHours != null && slaHours > 0) {
                    double slaDays = slaHours / 24.0;
                    double percentage = (days / slaDays) * 100.0;
                    
                    job.setDaysInStage(days);

                    job.setSlaDays((int) days);

                    job.setSlaPercentage(Math.round(percentage * 10.0) / 10.0);

                    if (percentage < 50) {
                        job.setSlaColor("GREEN");
                    }
                    else if (percentage <= 100) {
                        job.setSlaColor("ORANGE");
                    }
                    else {
                        job.setSlaColor("RED");
                    }
                }
            } else {
                job.setDaysInStage(null);
                job.setSlaColor(null);
            }

            if (!applyFilters(job, applicants, dateFilter, sources, sla, now, startDate, endDate)) {
                continue;
            }

            if (Boolean.TRUE.equals(job.getRejected())) {
                status = "REJECTED";
            }

            switch (status.toUpperCase()) {
                case "SHORTLISTED" -> counts.put("shortlisted", counts.get("shortlisted") + 1);
                case "INTERVIEW" -> counts.put("interview", counts.get("interview") + 1);
                case "OFFER" -> counts.put("offer", counts.get("offer") + 1);
                case "HIRED" -> counts.put("hired", counts.get("hired") + 1);
                case "REJECTED" -> counts.put("rejected", counts.get("rejected") + 1);
            }
            filtered.add(job);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("jobApplications", filtered);
        response.put("counts", counts);

        return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
    }

    private Integer getSlaHoursByStage(String stage) {
        if (stage == null) return null;

        return switch (stage.toUpperCase()) {
            case "SHORTLISTED" -> 48;
            case "INTERVIEW" -> 72;
            case "OFFER" -> 120;
            default -> null;
        };
    }

    private boolean applyFilters(JobApplicationEntity job, String applicants, String dateFilter,
                                 List<String> sources, List<String> sla, LocalDateTime now,
                                 LocalDateTime startDate, LocalDateTime endDate) {

    	if (!sla.contains("ALL")) {
    	    if (job.getSlaColor() == null ||
    	        sla.stream().noneMatch(s -> s.equalsIgnoreCase(job.getSlaColor()))) {
    	        return false;
    	    }
    	}

        if (!sources.contains("ALL")) {
        	if (job.getSource() == null || 
        		    sources.stream().noneMatch(s -> s.equalsIgnoreCase(job.getSource()))) {
        		    return false;
        		
            }
        }

        if ("referral".equalsIgnoreCase(applicants)) {
            if (!Boolean.TRUE.equals(job.getReferral())) return false;
        } else if ("non-referral".equalsIgnoreCase(applicants)) {
            if (Boolean.TRUE.equals(job.getReferral())) return false;
        }

//        if (dateFilter != null && job.getStageEntryDate() != null) {
//
//            LocalDateTime created = job.getStageEntryDate();
//
//            switch (dateFilter.toLowerCase()) {
//
//                case "today" -> {
//                    if (!created.toLocalDate().equals(now.toLocalDate())) return false;
//                }
//
//                case "last week" -> {
//                    LocalDateTime start = now.minusWeeks(1).with(DayOfWeek.MONDAY).withHour(0).withMinute(0);
//                    LocalDateTime end = start.plusDays(6).withHour(23).withMinute(59);
//                    if (created.isBefore(start) || created.isAfter(end)) return false;
//                }
//
//                case "last month" -> {
//                    LocalDateTime start = now.minusMonths(1).withDayOfMonth(1).withHour(0);
//                    LocalDateTime end = start.plusMonths(1).minusSeconds(1);
//                    if (created.isBefore(start) || created.isAfter(end)) return false;
//                }
//
//                case "custom" -> {
//                    if (startDate != null && endDate != null) {
//                        if (created.isBefore(startDate) || created.isAfter(endDate)) return false;
//                    }
//                }
//            }
//        }
        
        if (dateFilter != null && job.getCreatedDate() != null) {

            LocalDateTime date = job.getCreatedDate();

            switch (dateFilter.toLowerCase()) {

                case "today" -> {
                    if (!date.toLocalDate().equals(now.toLocalDate())) return false;
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
                        if (date.isBefore(startDate) || date.isAfter(endDate)) return false;
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
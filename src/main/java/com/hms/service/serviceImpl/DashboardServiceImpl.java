package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hms.service.dto.CandidatePipelineDto;
import com.hms.service.dto.CandidateQualityDto;
import com.hms.service.dto.CandidateSourcePerformanceDto;
import com.hms.service.dto.ConversionFunnelDto;
import com.hms.service.dto.DashboardCardsDto;
import com.hms.service.dto.HiringDashboardCardsDto;
import com.hms.service.dto.HiringDashboardResponseDto;
import com.hms.service.dto.HiringHealthDto;
import com.hms.service.dto.HiringManagerAnalyticsResponseDto;
import com.hms.service.dto.HiringTrendDto;
import com.hms.service.dto.MyAssignedJobsDto;
import com.hms.service.dto.NegotiationFlowDto;
import com.hms.service.dto.OfferStatusFlowDto;
import com.hms.service.dto.RecruiterAnalyticsResponseDto;
import com.hms.service.dto.RecuriterDashboardDetailsDto;
import com.hms.service.dto.RecuriterPerformanceResponseDto;
import com.hms.service.dto.SourcePerformanceDto;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.OfferDetailsEntity;
import com.hms.service.entity.RecruiterAssignmentEntity;
import com.hms.service.entity.ResumeAnalysisEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.OfferDetailsRepository;
import com.hms.service.repository.RecruiterAssignmentRepository;
import com.hms.service.repository.ResumeAnalysisRepository;
import com.hms.service.repository.StaffingRequisitionRepository;
import com.hms.service.request.RecuriterPerformanceRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.response.RecruiterAssignmentDashboardResponse;
import com.hms.service.response.RecruiterDashboardCountResponse;
import com.hms.service.response.RecruiterDashboardResponse;
import com.hms.service.service.IDashboardService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DashboardServiceImpl implements IDashboardService {

	@Autowired
	private RecruiterAssignmentRepository recruiterAssignmentRepository;

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private StaffingRequisitionRepository staffingRequisitionRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private ResumeAnalysisRepository resumeAnalysisRepository;

	@Autowired
	private OfferDetailsRepository offerDetailsRepository;

	@Override
	public ApiResponse<?> getDashboard() {

		RecuriterDashboardDetailsDto response = new RecuriterDashboardDetailsDto();

		DashboardCardsDto cards = new DashboardCardsDto();

		List<MyAssignedJobsDto> dashboardList = new ArrayList<>();

		String authHeader = httpServletRequest.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Authorization token is missing.");
		}

		String token = authHeader.substring(7);

		Integer recruiterId = jwtService.extractUserId(token).intValue();

		List<RecruiterAssignmentEntity> assignments = recruiterAssignmentRepository
				.findByUserIdAndStatusIgnoreCase(recruiterId, "Accepted");

		if (assignments.isEmpty()) {

			cards.setMyApprovedSRs(0L);
			cards.setActiveCandidates(0L);
			cards.setTotalOpenings(0);
			cards.setYetToFill(0);
			cards.setInProgress(0);
			cards.setMy(0);
			cards.setTeam(0);
			cards.setFilled(0);

			response.setCards(cards);
			response.setMyAssignedJobsDto(new ArrayList<>());

			return ApiResponse.success(ResponseCode.SUCCESS, "Dashboard counts fetched successfully", response);
		}

		List<Integer> jobIds = assignments.stream().map(RecruiterAssignmentEntity::getJobId).distinct()
				.collect(Collectors.toList());

		List<String> srIds = assignments.stream().map(RecruiterAssignmentEntity::getSrId).distinct()
				.collect(Collectors.toList());

		List<CreateJobDetailsEntity> jobs = createJobDetailsRepository.findByJobIdIn(jobIds);

		List<SRPositionBasicsEntity> srPositions = staffingRequisitionRepository.findBySrIdIn(srIds);

		Map<Integer, CreateJobDetailsEntity> jobMap = jobs.stream()
				.collect(Collectors.toMap(CreateJobDetailsEntity::getJobId, Function.identity()));

		Map<String, SRPositionBasicsEntity> srPositionMap = srPositions.stream()
				.collect(Collectors.toMap(SRPositionBasicsEntity::getSrId, Function.identity()));

		List<JobApplicationEntity> applications = jobApplicationRepository.findByRecruiterId(recruiterId);

		Map<Integer, Long> candidateCountMap = applications.stream()
				.collect(Collectors.groupingBy(JobApplicationEntity::getJobId, Collectors.counting()));

		Long approvedSRCount = (long) assignments.size();

		Long activeCandidateCount = (long) applications.size();

		Integer totalOpenings = 0;

		for (RecruiterAssignmentEntity assignment : assignments) {

			CreateJobDetailsEntity job = jobMap.get(assignment.getJobId());

			if (job == null) {
				continue;
			}

			SRPositionBasicsEntity srPosition = srPositionMap.get(assignment.getSrId());

			String priority = null;

			if (srPosition != null) {
				priority = srPosition.getPriority();
			}

			totalOpenings += job.getOpenings();

			Integer myCandidates = candidateCountMap.getOrDefault(job.getJobId(), 0L).intValue();

			Long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), job.getTargetStartDate());

			String sla;

			if (daysRemaining < 0) {

				sla = "Overdue";

			} else {

				Long actualTimeline = ChronoUnit.DAYS.between(job.getCreatedAt().toLocalDate(),
						job.getTargetStartDate());

				if (actualTimeline <= 0) {
					actualTimeline = 1L;
				}

				double timePercentage = (daysRemaining.doubleValue() / actualTimeline.doubleValue()) * 100;

				Integer filledCandidates = myCandidates;

				Integer remainingHiring = Math.max(job.getOpenings() - filledCandidates, 0);

				double hiringPercentage = (remainingHiring.doubleValue() / job.getOpenings().doubleValue()) * 100;

				if (timePercentage < 50 && hiringPercentage < 50) {

					sla = "At Risk";

				} else {

					sla = "On Track";
				}
			}

			MyAssignedJobsDto dto = new MyAssignedJobsDto();

			dto.setJobId(job.getJobId());
			dto.setPosition(job.getJobTitle());

			// Priority from SR Position Basics
			dto.setPriority(priority);

			dto.setTotalOpenings(job.getOpenings());

			dto.setTargetStartDate(job.getTargetStartDate());

			dto.setMy(0);

			dto.setTeam(0);

			dto.setYetToFill(0);

			dto.setInProgress(0);

			dto.setDaysRemaining(daysRemaining);

			dto.setSlaStatus(sla);

			dashboardList.add(dto);
			cards.setMyApprovedSRs(approvedSRCount);

			cards.setActiveCandidates(activeCandidateCount);

			cards.setTotalOpenings(totalOpenings);

			// Yet to Fill - Not implemented
			cards.setYetToFill(0);

			// In Progress - Not implemented
			cards.setInProgress(0);

			response.setCards(cards);

			response.setMyAssignedJobsDto(dashboardList);

		}
		return ApiResponse.success(ResponseCode.SUCCESS, "Dashboard counts fetched successfully", response);

	}

	@Override
	public ApiResponse<?> getRecruiterAnalytics(Integer jobId, LocalDate fromDate, LocalDate toDate) {

		RecruiterAnalyticsResponseDto response = new RecruiterAnalyticsResponseDto();

		Integer recruiterId = getRecruiterIdFromToken();

		List<JobApplicationEntity> applications;

		if (fromDate == null && toDate == null) {

			applications = jobApplicationRepository.findByRecruiterIdAndJobId(recruiterId, jobId);

		} else {

			LocalDateTime fromDateTime;
			LocalDateTime toDateTime;

			if (fromDate != null) {
				fromDateTime = fromDate.atStartOfDay();
			} else {
				fromDateTime = LocalDate.of(1900, 1, 1).atStartOfDay();
			}

			if (toDate != null) {
				toDateTime = toDate.atTime(LocalTime.MAX);
			} else {
				toDateTime = LocalDate.of(9999, 12, 31).atTime(LocalTime.MAX);
			}

			applications = jobApplicationRepository.findRecruiterApplicationsByDate(recruiterId, jobId, fromDateTime,
					toDateTime);
		}
		if (applications.isEmpty()) {

			response.setConversionFunnel(
					buildConversionFunnel(Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));

			response.setOfferStatusFlow(buildOfferStatusFlow(Collections.emptyList()));

			response.setNegotiationFlow(buildNegotiationFlow(Collections.emptyList()));

			response.setSourcePerformance(buildSourcePerformance(Collections.emptyList()));

			return ApiResponse.success(ResponseCode.SUCCESS, "No data found.", response);
		}
		List<Integer> applicationIds = applications.stream().map(JobApplicationEntity::getId)
				.collect(Collectors.toList());

		List<ResumeAnalysisEntity> resumeAnalysis = resumeAnalysisRepository.findByApplicationIdIn(applicationIds);

		List<OfferDetailsEntity> offers = offerDetailsRepository.findByJobApplication_IdIn(applicationIds);

		response.setConversionFunnel(buildConversionFunnel(applications, resumeAnalysis, offers));

		response.setOfferStatusFlow(buildOfferStatusFlow(offers));

		response.setNegotiationFlow(buildNegotiationFlow(offers));

		response.setSourcePerformance(buildSourcePerformance(applications));

		return ApiResponse.success(ResponseCode.SUCCESS, "Recruiter analytics fetched successfully.", response);
	}

	private Integer getRecruiterIdFromToken() {

		String authHeader = httpServletRequest.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("Authorization token is missing.");
		}

		String token = authHeader.substring(7);

		return jwtService.extractUserId(token).intValue();
	}

	private ConversionFunnelDto buildConversionFunnel(List<JobApplicationEntity> applications,
			List<ResumeAnalysisEntity> resumeAnalysis, List<OfferDetailsEntity> offers) {

		ConversionFunnelDto dto = new ConversionFunnelDto();

		dto.setApplications((long) applications.size());

		dto.setScreening(
				applications.stream().filter(app -> "Screened".equalsIgnoreCase(app.getCurrentStage())).count());

		dto.setShortlisted(
				resumeAnalysis.stream().filter(resume -> "Shortlisted".equalsIgnoreCase(resume.getStatus())).count());

		dto.setInterview(applications.stream().filter(JobApplicationEntity::isInPersonInterviews).count());

		dto.setOffersAccepted(
				offers.stream().filter(offer -> "Accepted".equalsIgnoreCase(offer.getOfferStatus())).count());

		dto.setOffersReleased(offers.stream().filter(offer -> Boolean.TRUE.equals(offer.getOfferReleased())).count());

		dto.setHired(0L);

		return dto;
	}

	private OfferStatusFlowDto buildOfferStatusFlow(List<OfferDetailsEntity> offers) {

		OfferStatusFlowDto dto = new OfferStatusFlowDto();

		Map<Integer, OfferDetailsEntity> uniqueOffers = offers.stream().collect(Collectors
				.toMap(offer -> offer.getJobApplication().getId(), Function.identity(), (existing, latest) -> latest));

		List<OfferDetailsEntity> offerList = new ArrayList<>(uniqueOffers.values());

		dto.setOfferRequestByHR(offerList.stream()
				.filter(offer -> "Hired".equalsIgnoreCase(offer.getInterviewCompletionStatus())).count());

		dto.setUnderReviewApproval(
				offerList.stream().filter(offer -> Boolean.FALSE.equals(offer.getApprover3())).count());

		dto.setOfferReleased(offerList.stream().filter(offer -> Boolean.TRUE.equals(offer.getOfferReleased())).count());

		dto.setOfferAccepted(
				offerList.stream().filter(offer -> "Accepted".equalsIgnoreCase(offer.getOfferStatus())).count());

		dto.setOfferRejected(
				offerList.stream().filter(offer -> "Rejected".equalsIgnoreCase(offer.getOfferStatus())).count());

		return dto;
	}

	private NegotiationFlowDto buildNegotiationFlow(List<OfferDetailsEntity> offers) {

		NegotiationFlowDto dto = new NegotiationFlowDto();

		List<OfferDetailsEntity> negotiationOffers = offers.stream()
				.filter(offer -> Boolean.TRUE.equals(offer.getNegotiation()))
				.filter(offer -> offer.getNegotiationId() != null).collect(Collectors.toList());

		dto.setNegotiationRequest(negotiationOffers.stream()
				.filter(offer -> "Request For Negotiation".equalsIgnoreCase(offer.getOfferStatus())).count());

		dto.setHrReview(negotiationOffers.stream().filter(offer -> "Reviewed".equalsIgnoreCase(offer.getOfferStatus()))
				.count());

		dto.setUnderReview(
				negotiationOffers.stream().filter(offer -> Boolean.FALSE.equals(offer.getApprover3())).count());

		dto.setReReleaseOffer(negotiationOffers.stream().filter(offer -> offer.getReReleaseOfferId() != null).count());

		dto.setCandidateAccepted(negotiationOffers.stream()
				.filter(offer -> "Accepted".equalsIgnoreCase(offer.getOfferStatus())).count());

		dto.setCandidateRejected(negotiationOffers.stream()
				.filter(offer -> "Rejected".equalsIgnoreCase(offer.getOfferStatus())).count());

		return dto;
	}

	private SourcePerformanceDto buildSourcePerformance(List<JobApplicationEntity> applications) {

		SourcePerformanceDto dto = new SourcePerformanceDto();

		dto.setCompanyCareerPortal(
				applications.stream().filter(app -> Boolean.TRUE.equals(app.getCareerPortal())).count());

		dto.setLinkedIn(0L);
		dto.setNaukri(0L);
		dto.setEmployeeReferral(0L);
		dto.setIndeed(0L);
		dto.setOthers(0L);

		return dto;
	}

	@Override

	public ApiResponse<?> getHiringDashboard() {

		HiringDashboardResponseDto response = new HiringDashboardResponseDto();

		HiringDashboardCardsDto cards = new HiringDashboardCardsDto();

		List<MyAssignedJobsDto> dashboardList = new ArrayList<>();

		String authHeader = httpServletRequest.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {

			return ApiResponse.failure(ResponseCode.FAILURE, "Authorization token is missing.");

		}

		String token = authHeader.substring(7);

		Long userId = jwtService.extractUserId(token);

		List<SRPositionBasicsEntity> srList = staffingRequisitionRepository.findByUserId(userId);

		if (srList.isEmpty()) {

			cards.setAverageHiringAge(0L);

			cards.setInterviews(0L);

			cards.setOffers(0L);

			cards.setOpenSrs(0L);

			cards.setTotalCandidates(0L);

			response.setCards(cards);

			response.setMyRequisitions(new ArrayList<>());

			return ApiResponse.success(ResponseCode.SUCCESS,

					"Dashboard fetched successfully", response);

		}

		Long openSrCount = (long) srList.size();

		Long totalCandidates = 0L;

		Long interviewCount = 0L;

		Long offerCount = 0L;

		for (SRPositionBasicsEntity sr : srList) {

			CreateJobDetailsEntity job = createJobDetailsRepository.findBySrId(sr.getSrId());

			if (job == null) {

				continue;

			}

			Long candidateCount = (long) jobApplicationRepository.countByJobId(job.getJobId());

			totalCandidates += candidateCount;

			Long interviews = jobApplicationRepository

					.countByJobIdAndInPersonInterviewsTrue(job.getJobId());

			interviewCount += interviews;

			List<JobApplicationEntity> applications =

					jobApplicationRepository.findByJobId(job.getJobId());

			List<Integer> applicationIds = applications.stream()

					.map(JobApplicationEntity::getId)

					.toList();

			if (!applicationIds.isEmpty()) {

				offerCount += offerDetailsRepository.countReleasedOffers(applicationIds);

			}

			MyAssignedJobsDto dto = new MyAssignedJobsDto();

			dto.setJobId(job.getJobId());

			dto.setPosition(job.getJobTitle());

			dto.setTotalOpenings(job.getOpenings());

			dto.setTargetStartDate(job.getTargetStartDate());

			dto.setPriority(sr.getPriority());

			dto.setSrId(sr.getSrId());

			dto.setInProgress(null);

			dto.setYetToFill(job.getOpenings());

			LocalDate today = LocalDate.now();

			Long difference = ChronoUnit.DAYS.between(today, job.getTargetStartDate());

			dto.setDaysRemaining(difference);

			String sla;

			if (difference < 0) {

				sla = "Over Due";

			} else {

				Long actualTime = ChronoUnit.DAYS.between(

						job.getCreatedAt().toLocalDate(),

						job.getTargetStartDate());

				if (actualTime <= 0) {

					actualTime = 1L;

				}

				int openings = job.getOpenings();

				int hired = 0;

				int remaining = openings - hired;

				double timePercentage =

						(difference.doubleValue() / actualTime.doubleValue()) * 100;

				double remainingPercentage =

						((double) remaining / openings) * 100;

				if (timePercentage < 50 && remainingPercentage < 50) {

					sla = "At Risk";

				} else {

					sla = "On Track";

				}

			}

			dto.setSlaStatus(sla);

			dashboardList.add(dto);

		}

		cards.setAverageHiringAge(0L);

		cards.setInterviews(interviewCount);

		cards.setOffers(offerCount);

		cards.setOpenSrs(openSrCount);

		cards.setTotalCandidates(totalCandidates);

		response.setCards(cards);

		response.setMyRequisitions(dashboardList);

		return ApiResponse.success(ResponseCode.SUCCESS,

				"Dashboard fetched successfully", response);

	}

	@Override
	public ApiResponse<?> getHiringManagerAnalytics(String srId, LocalDate fromDate, LocalDate toDate) {

		HiringManagerAnalyticsResponseDto response = new HiringManagerAnalyticsResponseDto();

		CreateJobDetailsEntity job = createJobDetailsRepository.findBySrId(srId);

		if (job == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, List.of("Invalid SR Id"));
		}

		Integer jobId = job.getJobId();

		List<JobApplicationEntity> applications;

		if (fromDate != null && toDate != null) {

			LocalDateTime from = fromDate.atStartOfDay();

			LocalDateTime to = toDate.atTime(LocalTime.MAX);

			applications = jobApplicationRepository.findByJobIdAndCreatedDateBetween(jobId, from, to);

		} else {

			applications = jobApplicationRepository.findByJobId(jobId);
		}

		if (applications.isEmpty()) {

			response.setCandidatePipeline(new CandidatePipelineDto());
			response.setOfferStatusFlow(new OfferStatusFlowDto());
			response.setNegotiationFlow(new NegotiationFlowDto());
			response.setCandidateQuality(new CandidateQualityDto());
			response.setHiringHealth(new HiringHealthDto());

			return ApiResponse.success(ResponseCode.SUCCESS, "No Data Found", response);
		}

		List<Integer> applicationIds = applications.stream().map(JobApplicationEntity::getId).toList();

		List<ResumeAnalysisEntity> resumeAnalysis = resumeAnalysisRepository.findByApplicationIdIn(applicationIds);

		List<OfferDetailsEntity> offers = offerDetailsRepository.findByJobApplication_IdIn(applicationIds);

		response.setCandidatePipeline(buildCandidatePipeline(applications, resumeAnalysis, offers));

		response.setOfferStatusFlow(buildOfferStatusFlow(offers));

		response.setNegotiationFlow(buildNegotiationFlow(offers));

		response.setCandidateQuality(buildCandidateQuality(resumeAnalysis));

		response.setHiringHealth(buildHiringHealth(applications, resumeAnalysis, offers));

		return ApiResponse.success(ResponseCode.SUCCESS, "Hiring Manager Analytics fetched successfully.", response);
	}

	private CandidatePipelineDto buildCandidatePipeline(List<JobApplicationEntity> applications,
			List<ResumeAnalysisEntity> resumeAnalysis, List<OfferDetailsEntity> offers) {

		CandidatePipelineDto dto = new CandidatePipelineDto();

		long applied = applications.size();
		long screening = resumeAnalysis.size();

		long interview = 0;
		for (JobApplicationEntity application : applications) {
			if (application.isInPersonInterviews()) {
				interview++;
			}
		}

		long offer = 0;
		long hired = 0;

		for (OfferDetailsEntity offerDetails : offers) {

			if (Boolean.TRUE.equals(offerDetails.getOfferReleased())) {
				offer++;
			}

			
		}

		dto.setApplied(applied);
		dto.setScreening(screening);
		dto.setInterview(interview);
		dto.setOffer(offer);
		dto.setHired(hired);

		dto.setScreeningPercentage(calculatePercentage(applied, screening));
		dto.setInterviewPercentage(calculatePercentage(screening, interview));
		dto.setOfferPercentage(calculatePercentage(interview, offer));
		dto.setHiredPercentage(calculatePercentage(offer, hired));
		dto.setOverallConversionRate(calculatePercentage(applied, hired));

		return dto;
	}

	private Double calculatePercentage(long total, long value) {

		if (total == 0) {
			return 0.0;
		}

		return Math.round(((double) value / total) * 1000.0) / 10.0;
	}

	private CandidateQualityDto buildCandidateQuality(List<ResumeAnalysisEntity> resumeAnalysis) {

		CandidateQualityDto dto = new CandidateQualityDto();

		long excellent = 0;
		long good = 0;
		long average = 0;
		long needsReview = 0;

		for (ResumeAnalysisEntity resume : resumeAnalysis) {

			if (resume.getFinalScore() == null) {
				continue;
			}

			double score = resume.getFinalScore();

			if (score >= 90) {
				excellent++;
			} else if (score >= 80) {
				good++;
			} else if (score >= 70) {
				average++;
			} else {
				needsReview++;
			}
		}

		dto.setExcellent(excellent);
		dto.setGood(good);
		dto.setAverage(average);
		dto.setNeedsReview(needsReview);
		dto.setTotalCandidates((long) resumeAnalysis.size());

		return dto;
	}

	private HiringHealthDto buildHiringHealth(List<JobApplicationEntity> applications,
			List<ResumeAnalysisEntity> resumeAnalysis, List<OfferDetailsEntity> offers) {

		HiringHealthDto dto = new HiringHealthDto();

		long totalApplications = applications.size();
		long screened = resumeAnalysis.size();

		long interviews = 0;

		for (JobApplicationEntity application : applications) {
			if (application.isInPersonInterviews()) {
				interviews++;
			}
		}

		long offersReleased = 0;

		for (OfferDetailsEntity offer : offers) {
			if (Boolean.TRUE.equals(offer.getOfferReleased())) {
				offersReleased++;
			}
		}

		long qualityCandidates = 0;

		for (ResumeAnalysisEntity resume : resumeAnalysis) {

			if (resume.getFinalScore() != null && resume.getFinalScore() >= 80) {
				qualityCandidates++;
			}
		}

		dto.setPipelineCoverage(calculatePercentage(totalApplications, screened));

		dto.setOfferProgress(calculatePercentage(interviews, offersReleased));

		dto.setCandidateQuality(calculatePercentage(screened, qualityCandidates));

		dto.setRequisitionsOnTrack(calculatePercentage(totalApplications, interviews));

		dto.setAgingRequisitions(100 - calculatePercentage(totalApplications, interviews));

		return dto;
	}

	@Override

	public ApiResponse<?> getRecruiterDashboard(SpecificationFilterRequest request) {

		log.info("RecruiterDashboardServiceImpl : getRecruiterDashboard");
		

		Integer recruiterId = Integer.valueOf(request.getFilter("recruiterId"));

		Sort sort = request.getDirection().equalsIgnoreCase("ASC") ? Sort.by(request.getSortBy()).ascending()
				: Sort.by(request.getSortBy()).descending();

		List<RecruiterAssignmentEntity> assignments = recruiterAssignmentRepository
				.findAll(request.buildRecruiterDashboardSpecification(recruiterId), sort);

		RecruiterDashboardCountResponse counts = new RecruiterDashboardCountResponse();

		Long totalAssignments = (long) assignments.size();

		Long acceptedAssignments = assignments.stream()
				.filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("Accepted")).count();

		Long rejectedAssignments = assignments.stream()
				.filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("Rejected")).count();

		List<JobApplicationEntity> applications = jobApplicationRepository
				.findAll(request.buildRecruiterApplicationSpecification(recruiterId));

		Long applicationsAdded = (long) applications.size();

		List<Integer> applicationIds = applications.stream().map(JobApplicationEntity::getId).toList();

		List<OfferDetailsEntity> offers = applicationIds.isEmpty() ? Collections.emptyList()
				: offerDetailsRepository.findByJobApplication_IdIn(applicationIds);

		Long offersReleased = offers.stream()
		        .filter(o -> Boolean.TRUE.equals(o.getOfferReleased()))
		        .count();

		List<Object[]> slaResult = recruiterAssignmentRepository.getSlaCounts(recruiterId);

		Long onTrack = 0L;
		Long atRisk = 0L;
		Long overdue = 0L;

		if (!slaResult.isEmpty()) {

		    Object[] slaCounts = slaResult.get(0);

		    onTrack = slaCounts[0] != null ? ((Number) slaCounts[0]).longValue() : 0L;
		    atRisk = slaCounts[1] != null ? ((Number) slaCounts[1]).longValue() : 0L;
		    overdue = slaCounts[2] != null ? ((Number) slaCounts[2]).longValue() : 0L;
		}


		Set<Integer> jobIds = assignments.stream().map(RecruiterAssignmentEntity::getJobId).collect(Collectors.toSet());

		Map<Integer, CreateJobDetailsEntity> jobMap = createJobDetailsRepository.findByJobIdIn(jobIds).stream()
				.collect(Collectors.toMap(CreateJobDetailsEntity::getJobId, Function.identity()));
		
		Set<String> srIds = jobMap.values().stream().map(CreateJobDetailsEntity::getSrId).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		Map<String, SRPositionBasicsEntity> srMap = staffingRequisitionRepository.findBySrIdIn(srIds).stream()
				.collect(Collectors.toMap(SRPositionBasicsEntity::getSrId, Function.identity()));
		
		Map<Integer, Long> candidateCountMap = applications.stream()
				.collect(Collectors.groupingBy(JobApplicationEntity::getJobId, Collectors.counting()));

		
		List<RecruiterAssignmentDashboardResponse> jobs = new ArrayList<>();

		for (RecruiterAssignmentEntity assignment : assignments) {

			RecruiterAssignmentDashboardResponse response = new RecruiterAssignmentDashboardResponse();

			response.setJobId(assignment.getJobId());

			CreateJobDetailsEntity job = jobMap.get(assignment.getJobId());
			if (job != null) {

				response.setJobTitle(job.getJobTitle());

				response.setRequestedOpenings(job.getOpenings());

				response.setTargetDate(job.getTargetStartDate());

				response.setAssignmentStatus(assignment.getStatus());

				if (assignment.getRespondedAt() != null) {
					response.setAcceptedOn(assignment.getRespondedAt().toLocalDate());
				}

				SRPositionBasicsEntity sr = srMap.get(job.getSrId());

				if (sr != null) {
					response.setSrId(sr.getSrId());
					response.setPriority(sr.getPriority());
				}

				response.setFilled(0);
				response.setRemaining(0);

				Long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), job.getTargetStartDate());

				response.setDaysLeft(daysLeft);

				Integer myCandidates = candidateCountMap.getOrDefault(job.getJobId(), 0L).intValue();

				String sla;

				if (daysLeft < 0) {

					sla = "Overdue";

				} else {

					Long actualTimeline = ChronoUnit.DAYS.between(job.getCreatedAt().toLocalDate(),
							job.getTargetStartDate());

					if (actualTimeline <= 0) {
						actualTimeline = 1L;
					}

					double timePercentage = (daysLeft.doubleValue() / actualTimeline.doubleValue()) * 100;

					Integer remainingHiring = Math.max(job.getOpenings() - myCandidates, 0);

					double hiringPercentage = (remainingHiring.doubleValue() / job.getOpenings().doubleValue()) * 100;

					if (timePercentage < 50 && hiringPercentage < 50) {

						sla = "At Risk";
					

					} else {

						sla = "On Track";
						
					}
				}

				response.setSla(sla);
			}

			jobs.add(response);
		}
		
		Double slaCompliance = 0.0;

		Long totalSla = onTrack + atRisk + overdue;

		if (totalSla > 0) {
		    slaCompliance = ((onTrack + atRisk) * 100.0) / totalSla;
		}

		counts.setTotalAssignments(totalAssignments);
		counts.setAcceptedAssignments(acceptedAssignments);
		counts.setRejectedAssignments(rejectedAssignments);
		counts.setApplicationsAdded(applicationsAdded);
		counts.setOffersReleased(offersReleased);
		counts.setSlaCompliance(slaCompliance);

		// Keeping hired null for now
		counts.setHired(null);

		counts.setOnTrack(onTrack);
		counts.setAtRisk(atRisk);
		counts.setOverdue(overdue);

		RecruiterDashboardResponse dashboard = new RecruiterDashboardResponse();

		dashboard.setDashboardCounts(counts);
		dashboard.setAssignments(jobs);

		return ApiResponse.success(ResponseCode.SUCCESS, "Success", dashboard);

	}
	
	public ApiResponse<?> getRecruiterPerformance(RecuriterPerformanceRequest request) {

		RecuriterPerformanceResponseDto response = new RecuriterPerformanceResponseDto();

		LocalDateTime fromDateTime = request.getFromDate().atStartOfDay();

		LocalDateTime toDateTime = request.getToDate().atTime(LocalTime.MAX);

		List<JobApplicationEntity> applications = jobApplicationRepository.findApplicationsByRecruiterAndJob(
				request.getRecruiterId(), request.getJobId(), fromDateTime, toDateTime);

		if (applications.isEmpty()) {

			response.setCandidateSourcePerformance(new ArrayList<>());
			response.setRecruitmentFunnel(new ConversionFunnelDto());
			response.setHiringTrend(new ArrayList<>());

			return ApiResponse.success(ResponseCode.SUCCESS, "No data found.", response);
		}

		List<Integer> applicationIds = applications.stream().map(JobApplicationEntity::getId).toList();

		List<ResumeAnalysisEntity> resumeAnalysis = resumeAnalysisRepository.findByApplicationIdIn(applicationIds);

		List<OfferDetailsEntity> offers = offerDetailsRepository.findByJobApplication_IdIn(applicationIds);

		response.setCandidateSourcePerformance(buildCandidateSourcePerformance(applications, offers));

		response.setRecruitmentFunnel(buildConversionFunnel(applications, resumeAnalysis, offers));

		response.setHiringTrend(buildHiringTrend(request.getFromDate(), request.getToDate(), applications, offers));

		return ApiResponse.success(ResponseCode.SUCCESS, "Recruiter performance fetched successfully.", response);
	}

	private List<CandidateSourcePerformanceDto> buildCandidateSourcePerformance(List<JobApplicationEntity> applications,
			List<OfferDetailsEntity> offers) {

		List<CandidateSourcePerformanceDto> response = new ArrayList<>();

		Map<Integer, OfferDetailsEntity> offerMap = offers.stream().collect(Collectors
				.toMap(offer -> offer.getJobApplication().getId(), Function.identity(), (existing, latest) -> latest));

		List<JobApplicationEntity> careerPortalApplications = applications.stream()
				.filter(app -> Boolean.TRUE.equals(app.getCareerPortal())).toList();
		List<JobApplicationEntity> linkedInApplications = applications.stream()
				.filter(app -> "LinkedIn".equalsIgnoreCase(app.getSource())).toList();
		List<JobApplicationEntity> naukriApplications = applications.stream()
				.filter(app -> "Naukri".equalsIgnoreCase(app.getSource())).toList();
		List<JobApplicationEntity> indeedApplications = applications.stream()
				.filter(app -> "Indeed".equalsIgnoreCase(app.getSource())).toList();

		if (!careerPortalApplications.isEmpty()) {

			CandidateSourcePerformanceDto dto = new CandidateSourcePerformanceDto();

			dto.setSource("Career Portal");

			dto.setApplicantsAdded((long) careerPortalApplications.size());

			dto.setInterviewed(
					careerPortalApplications.stream().filter(JobApplicationEntity::isInPersonInterviews).count());

			dto.setOffered(careerPortalApplications.stream().filter(app -> {
				OfferDetailsEntity offer = offerMap.get(app.getId());
				return offer != null && Boolean.TRUE.equals(offer.getOfferReleased());
			}).count());

			dto.setHired(0L);

			response.add(dto);
		}

		return response;
	}

	private List<HiringTrendDto> buildHiringTrend(LocalDate fromDate, LocalDate toDate,
			List<JobApplicationEntity> applications, List<OfferDetailsEntity> offers) {

		List<HiringTrendDto> response = new ArrayList<>();

		Map<LocalDate, Long> candidatesAddedMap = applications.stream()
				.collect(Collectors.groupingBy(app -> app.getCreatedDate().toLocalDate(), Collectors.counting()));

		Map<LocalDate, Long> offersReleasedMap = offers.stream()
				.filter(offer -> Boolean.TRUE.equals(offer.getOfferReleased()))
				.filter(offer -> offer.getJobApplication() != null).collect(Collectors.groupingBy(
						offer -> offer.getJobApplication().getCreatedDate().toLocalDate(), Collectors.counting()));

		for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {

			HiringTrendDto dto = new HiringTrendDto();

			dto.setDate(date);

			dto.setCandidatesAdded(candidatesAddedMap.getOrDefault(date, 0L));

			dto.setOffersReleased(offersReleasedMap.getOrDefault(date, 0L));

			dto.setHired(0L);

			response.add(dto);
		}

		return response;

	}
}

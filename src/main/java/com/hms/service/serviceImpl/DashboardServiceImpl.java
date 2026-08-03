package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.dto.ConversionFunnelDto;
import com.hms.service.dto.DashboardCardsDto;
import com.hms.service.dto.MyAssignedJobsDto;
import com.hms.service.dto.NegotiationFlowDto;
import com.hms.service.dto.OfferStatusFlowDto;
import com.hms.service.dto.RecruiterAnalyticsResponseDto;
import com.hms.service.dto.RecuriterDashboardDetailsDto;
import com.hms.service.dto.SourcePerformanceDto;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.NegotiationOfferEntity;
import com.hms.service.entity.OfferDetailsEntity;
import com.hms.service.entity.RecruiterAssignmentEntity;
import com.hms.service.entity.ResumeAnalysisEntity;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.NegotiateOfferRepository;
import com.hms.service.repository.OfferDetailsRepository;
import com.hms.service.repository.RecruiterAssignmentRepository;
import com.hms.service.repository.ResumeAnalysisRepository;
import com.hms.service.service.IRecuriterDashboardService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DashboardServiceImpl implements IRecuriterDashboardService {

	@Autowired
	private RecruiterAssignmentRepository recruiterAssignmentRepository;

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private ResumeAnalysisRepository resumeAnalysisRepository;

	@Autowired
	private OfferDetailsRepository offerDetailsRepository;

	@Autowired
	private NegotiateOfferRepository negotiateOfferRepository;

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

			response.setCards(cards);
			response.setMyAssignedJobsDto(new ArrayList<>());

			return ApiResponse.success(ResponseCode.SUCCESS, "Dashboard counts fetched successfully", response);
		}

		List<Integer> jobIds = assignments.stream().map(RecruiterAssignmentEntity::getJobId).distinct()
				.collect(Collectors.toList());

		List<CreateJobDetailsEntity> jobs = createJobDetailsRepository.findByJobIdIn(jobIds);

		Map<Integer, CreateJobDetailsEntity> jobMap = jobs.stream()
				.collect(Collectors.toMap(CreateJobDetailsEntity::getJobId, Function.identity()));

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

			totalOpenings += job.getOpenings();

			Integer myCandidates = candidateCountMap.getOrDefault(job.getJobId(), 0L).intValue();

			Long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), job.getTargetStartDate());

			String sla;

			if (daysRemaining < 0) {

				sla = "Overdue";

			} else if (daysRemaining <= 5) {

				sla = "At Risk";

			} else {

				sla = "On Track";

			}

			MyAssignedJobsDto dto = new MyAssignedJobsDto();

			dto.setJobId(job.getJobId());

			dto.setPosition(job.getJobTitle());

			dto.setTotalOpenings(job.getOpenings());

			dto.setTargetStartDate(job.getTargetStartDate());

			dto.setMy(myCandidates);

			// Not implemented yet
			dto.setTeam(null);

			// Not implemented yet
			dto.setYetToFill(null);

			// Not implemented yet
			dto.setInProgress(null);

			dto.setDaysRemaining(daysRemaining);

			dto.setSlaStatus(sla);

			dashboardList.add(dto);

		}

		cards.setMyApprovedSRs(approvedSRCount);

		cards.setActiveCandidates(activeCandidateCount);

		cards.setTotalOpenings(totalOpenings);

		cards.setYetToFill(0);

		cards.setInProgress(0);

		response.setCards(cards);

		response.setMyAssignedJobsDto(dashboardList);

		return ApiResponse.success(ResponseCode.SUCCESS, "Dashboard counts fetched successfully", response);

	}

	@Override
	public ApiResponse<?> getRecruiterAnalytics(Integer jobId) {

		RecruiterAnalyticsResponseDto response = new RecruiterAnalyticsResponseDto();

		Integer recruiterId = getRecruiterIdFromToken();

		List<JobApplicationEntity> applications = jobApplicationRepository.findByRecruiterIdAndJobId(recruiterId,
				jobId);

		if (applications.isEmpty()) {

			response.setConversionFunnel(new ConversionFunnelDto());
			response.setOfferStatusFlow(new OfferStatusFlowDto());
			response.setNegotiationFlow(new NegotiationFlowDto());
			response.setSourcePerformance(new SourcePerformanceDto());

			return ApiResponse.success(ResponseCode.SUCCESS, "No data found.", response);
		}

		List<Integer> applicationIds = applications.stream().map(JobApplicationEntity::getId)
				.collect(Collectors.toList());

		List<ResumeAnalysisEntity> resumeAnalysis = resumeAnalysisRepository.findByApplicationIdIn(applicationIds);

		List<OfferDetailsEntity> offers = offerDetailsRepository.findByJobApplication_IdIn(applicationIds);

		List<NegotiationOfferEntity> negotiations = negotiateOfferRepository.findByApplicant_IdIn(applicationIds);

		response.setConversionFunnel(buildConversionFunnel(applications, resumeAnalysis, offers));

		response.setOfferStatusFlow(buildOfferStatusFlow(offers));

		response.setNegotiationFlow(buildNegotiationFlow(negotiations));

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

		dto.setScreening(applications.stream()
				.filter(app -> app.getCurrentStage() != null && app.getCurrentStage().equalsIgnoreCase("Screened"))
				.count());

		dto.setShortlisted(resumeAnalysis.stream()
				.filter(resume -> resume.getStatus() != null && resume.getStatus().equalsIgnoreCase("Shortlisted"))
				.count());

		dto.setInterview(applications.stream().filter(JobApplicationEntity::isInPersonInterviews).count());

		dto.setOffers(offers.stream()
				.filter(offer -> offer.getOfferStatus() != null && offer.getOfferStatus().equalsIgnoreCase("Accepted"))
				.count());

		dto.setHired(null);

		return dto;
	}

	private OfferStatusFlowDto buildOfferStatusFlow(List<OfferDetailsEntity> offers) {

		OfferStatusFlowDto dto = new OfferStatusFlowDto();

		dto.setOfferRequestByHR(

				offers.stream().filter(offer -> offer.getInterviewCompletionStatus() != null
						&& offer.getInterviewCompletionStatus().equalsIgnoreCase("Hired")).count());

		dto.setUnderReviewApproval(

				offers.stream().filter(offer -> Boolean.FALSE.equals(offer.getApprover3())).count());

		dto.setOfferReleased(

				offers.stream().filter(offer -> Boolean.TRUE.equals(offer.getOfferReleased())).count());

		dto.setOfferAccepted(

				offers.stream().filter(
						offer -> offer.getOfferStatus() != null && offer.getOfferStatus().equalsIgnoreCase("Accepted"))
						.count());

		dto.setOfferRejected(

				offers.stream().filter(
						offer -> offer.getOfferStatus() != null && offer.getOfferStatus().equalsIgnoreCase("Rejected"))
						.count());

		return dto;
	}

	private NegotiationFlowDto buildNegotiationFlow(List<NegotiationOfferEntity> negotiations) {

		NegotiationFlowDto dto = new NegotiationFlowDto();

		dto.setNegotiationRequest((long) negotiations.size());

		dto.setHrReview(null);

		dto.setUnderReview(null);

		dto.setReReleaseOffer(null);

		dto.setCandidateAccepted(null);

		dto.setCandidateRejected(null);

		return dto;
	}

	private SourcePerformanceDto buildSourcePerformance(List<JobApplicationEntity> applications) {

		SourcePerformanceDto dto = new SourcePerformanceDto();

		dto.setCompanyCareerPortal(

				applications.stream().filter(app -> Boolean.TRUE.equals(app.getCareerPortal())).count());

		dto.setLinkedIn(null);

		dto.setNaukri(null);

		dto.setEmployeeReferral(null);

		dto.setIndeed(null);

		dto.setOthers(null);

		return dto;
	}
}

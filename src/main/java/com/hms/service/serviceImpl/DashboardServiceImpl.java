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

import com.hms.service.dto.DashboardCardsDto;
import com.hms.service.dto.MyAssignedJobsDto;
import com.hms.service.dto.RecuriterDashboardDetailsDto;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.RecruiterAssignmentEntity;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.RecruiterAssignmentRepository;
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

}

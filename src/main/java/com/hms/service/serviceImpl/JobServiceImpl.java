package com.hms.service.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.InterviewCurrentStageEntity;
import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewScheduleEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.enums.FilterApplicantEnum;
import com.hms.service.exceptions.CustomSystemErrorException;
import com.hms.service.repository.CandidateCreationRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.InterviewAnalysisRepository;
import com.hms.service.repository.InterviewCurrentStageRepository;
import com.hms.service.repository.InterviewFeedbackRepository;
import com.hms.service.repository.InterviewPlanRepository;
import com.hms.service.repository.InterviewScheduleRepository;
import com.hms.service.repository.InterviewSessionRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.OfferRepository;
import com.hms.service.repository.ResumeAnalysisRepository;
import com.hms.service.response.JobApplicantsResponse;
import com.hms.service.response.JobsDashboardResponse;
import com.hms.service.service.IJobService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class JobServiceImpl implements IJobService {

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private InterviewPlanRepository interviewPlanRepository;

//	@Autowired
//	private JwtService jwtService;

	@Autowired
	private InterviewScheduleRepository interviewScheduleRepository;

	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private InterviewCurrentStageRepository interviewCurrentStageRepository;

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private InterviewAnalysisRepository interviewAnalysisRepository;
	
	@Autowired
	private InterviewFeedbackRepository interviewFeedbackRepository;

	@Autowired
	private CandidateCreationRepository candidateCreationRepository;

	@Autowired
	private ResumeAnalysisRepository resumeAnalysisRepository;

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private InterviewSessionRepository interviewSessionRepository;

//	@Autowired
//	private UserServiceImpl userService;
//
//	@Autowired
//	private HttpServletRequest httpServletRequest;

//	@Autowired
//	private InfospokeWebisteFeign infospokeWebsiteFeign;
//
//	@Autowired
//	private InfospokeATSFeign infospokeATSFeign;

//	@Autowired
//	private ActivityFeedRepository activityFeedRepository;
//

	@Override
	public ApiResponse<?> getAllJobApplicants() {

		log.info("JobsServiceImpl: Inside getAllJobApplicants method");

		List<JobApplicationEntity> jobApplicationEntity = jobApplicationRepository
				.findAll(Sort.by(Sort.Direction.DESC, Constants.CREATED_DATE));

		List<JobApplicantsResponse> jobApplicantsResponseList = new ArrayList<>();

		for (JobApplicationEntity entity : jobApplicationEntity) {
			JobApplicantsResponse jobApplicantsResponse = new JobApplicantsResponse();
			BeanUtils.copyProperties(entity, jobApplicantsResponse);
			jobApplicantsResponseList.add(jobApplicantsResponse);
		}

		log.info("JobsServiceImpl: Exit from getAllJobApplicants method");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", jobApplicantsResponseList);
	}

	@Override
	public ApiResponse<JobApplicantsResponse> getApplicantDetailsById(Integer id) {

		log.info("JobServiceImpl: Inside getApplicantDetailsById method");

		JobApplicationEntity entity = jobApplicationRepository.findById(id)
				.orElseThrow(() -> new CustomSystemErrorException(Constants.NO_APPLICANTS_FOUND));

		CreateJobDetailsEntity jobs = createJobDetailsRepository.findById(entity.getJobId())
				.orElseThrow(() -> new CustomSystemErrorException("Job not found"));

		InterviewPlanEntity interview = interviewPlanRepository.findById(jobs.getPlanId())
				.orElseThrow(() -> new CustomSystemErrorException("Interview plan not found"));

		InterviewScheduleEntity schedule = interviewScheduleRepository.findByApplicantId(entity.getId()).orElse(null);

		InterviewCurrentStageEntity current = interviewCurrentStageRepository.findByApplicationId(entity.getId());

		DepartmentsEntity department = departmentsRepository.findById(jobs.getDepartmentId()).orElse(null);

		JobApplicantsResponse response = new JobApplicantsResponse();
		BeanUtils.copyProperties(entity, response);

		response.setJobTitle(jobs.getJobTitle());
		response.setJobCode(jobs.getJobCode());
		response.setLocation(jobs.getLocation());
		response.setMinExperience(jobs.getMinExperience());
		response.setMaxExperience(jobs.getMaxExperience());
		response.setPlanName(interview.getPlanName());
		
		int totalStages = interview.getRounds() != null ? interview.getRounds().size() : 0;

		Integer completedStages = interviewFeedbackRepository.countByApplicantId(entity.getId());

		response.setNoOfStages(totalStages);
		response.setCompletedStages((int) completedStages);

		response.setDepartment(department != null ? department.getDepartmentName() : null);

		if (schedule != null) {
			response.setStartTime(schedule.getStartTime());
			response.setEndTime(schedule.getEndTime());
			response.setInterviewDate(schedule.getInterviewDate());
			response.setRescheduleDate(schedule.getRescheduleDate());
			response.setRescheduleStartTime(schedule.getRescheduleStartTime());
			response.setRescheduleEndTime(schedule.getRescheduleEndTime());
		}

		if (current != null) {
			response.setCurrentStageType(current.getCurrentStageType());
		}
		
		log.info("JobServiceImpl: Exit from getApplicantDetailsById method");

		return ApiResponse.success(ResponseCode.SUCCESS, "Applicant fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getAllJobsDashboardCounts() {

		JobsDashboardResponse response = new JobsDashboardResponse();

		long openJobs = createJobDetailsRepository.countByIsOpenTrue();

		long applicants = jobApplicationRepository.count();

		long interviews = interviewAnalysisRepository.count();

		long offersAccepted = offerRepository.count();

		response.setOpenJobs(openJobs);
		response.setCandidates(applicants);
		response.setInterviews(interviews);
		response.setOffersAccepted(offersAccepted);

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	public ApiResponse<?> getAllJobApplicants(Integer jobId, FilterApplicantEnum filter) {

		log.info("JobsServiceImpl: Inside getAllJobApplicants ");

		List<JobApplicationEntity> jobApplications = jobApplicationRepository.findByJobIdOrderByCreatedDateDesc(jobId);

		if (jobApplications.isEmpty()) {
			return ApiResponse.success(ResponseCode.SUCCESS, Collections.emptyList());
		}

		List<Integer> applicationIds = jobApplications.stream().map(JobApplicationEntity::getId)
				.collect(Collectors.toList());

		List<Object[]> screenedData = resumeAnalysisRepository.findScreenStatuses(applicationIds);
		Map<Integer, String> screenedStatusMap = new HashMap<>();

		for (Object[] obj : screenedData) {
			Integer appId = (Integer) obj[0];
			String status = (String) obj[1];
			screenedStatusMap.put(appId, status);
		}

		Set<Integer> screenedSet = screenedStatusMap.keySet();

		List<Object[]> interviewData = interviewSessionRepository.findApplicationIdAndStatus(applicationIds);

		Map<Integer, String> interviewStatus = new HashMap<>();

		for (Object[] obj : interviewData) {
			Integer appId = (Integer) obj[0];
			String status = (String) obj[1];
			interviewStatus.put(appId, status.toUpperCase().replace("_", " "));
		}

		log.info("Job Application IDs: {}", applicationIds);
		log.info("Screened IDs from DB: {}", screenedSet);

		List<Object[]> candidateData = candidateCreationRepository.findStatusByApplicationIds(applicationIds);

		Map<Integer, String> candidateStatusMap = new HashMap<>();

		for (Object[] obj : candidateData) {
			Integer appId = (Integer) obj[0];
			String dbStatus = (String) obj[1];
			candidateStatusMap.put(appId, dbStatus);
		}

		List<JobApplicantsResponse> result = new ArrayList<>();

		for (JobApplicationEntity entity : jobApplications) {

			Integer appId = entity.getId();

			if (!matchesFilter(filter, appId, screenedSet, interviewStatus, candidateStatusMap)) {
				continue;
			}

			JobApplicantsResponse response = new JobApplicantsResponse();
			BeanUtils.copyProperties(entity, response);
			response.setCurrentStage(entity.getCurrentStage());

			String screenedSubStatus = screenedStatusMap.get(appId);

			if (filter != null) {

				switch (filter) {

				case SCREENED:
					response.setStatus(Constants.SCREENED);
					response.setScreenedStatus(screenedSubStatus);
					break;

				case INTERVIEW:
					response.setStatus(interviewStatus.get(appId));
					break;

				case OFFER:
					response.setStatus(Constants.OFFER);
					break;

				case HIRED:
					response.setStatus(Constants.HIRED);
					break;

				case APPLIED:
					response.setStatus(getStatus(appId, screenedSet, interviewStatus, candidateStatusMap));
					response.setScreenedStatus(screenedSubStatus);
					break;

				default:
					response.setStatus(getStatus(appId, screenedSet, interviewStatus, candidateStatusMap));
					response.setScreenedStatus(screenedSubStatus);
				}

			} else {
				response.setStatus(getStatus(appId, screenedSet, interviewStatus, candidateStatusMap));
				response.setScreenedStatus(screenedSubStatus);
			}

			result.add(response);
		}

		log.info("JobsServiceImpl: Exit getAllJobApplicants");
		log.info("Interview Map: {}", interviewStatus);
		return ApiResponse.success(ResponseCode.SUCCESS, result);
	}

	private String getStatus(Integer appId, Set<Integer> screenedSet, Map<Integer, String> interviewStatus,
			Map<Integer, String> candidateStatusMap) {

		if (candidateStatusMap.containsKey(appId)) {
			String dbStatus = candidateStatusMap.get(appId);

			if (Constants.JOINED.equalsIgnoreCase(dbStatus)) {
				return Constants.HIRED;
			} else if (Constants.ACCEPTED.equalsIgnoreCase(dbStatus)) {
				return Constants.OFFER;
			}
		}

		if (interviewStatus.containsKey(appId)) {
			return interviewStatus.get(appId);
		}

		if (screenedSet.contains(appId)) {
			return Constants.SCREENED;
		}

		return Constants.APPLIED;
	}

	private boolean matchesFilter(FilterApplicantEnum filter, Integer appId, Set<Integer> screenedSet,
			Map<Integer, String> interviewStatus, Map<Integer, String> candidateStatusMap) {

		if (filter == null)
			return true;

		switch (filter) {

		case APPLIED:
			return true;

		case SCREENED:
			return screenedSet.contains(appId);

		case INTERVIEW:
			return interviewStatus.containsKey(appId);

		case OFFER:
			if (candidateStatusMap.containsKey(appId)) {
				String status = candidateStatusMap.get(appId);
				return Constants.ACCEPTED.equalsIgnoreCase(status) || Constants.JOINED.equalsIgnoreCase(status);
			}
			return false;

		case HIRED:
			if (candidateStatusMap.containsKey(appId)) {
				String status = candidateStatusMap.get(appId);
				return Constants.JOINED.equalsIgnoreCase(status);
			}
			return false;

		default:
			return false;
		}
	}

}

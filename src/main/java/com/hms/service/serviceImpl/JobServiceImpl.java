package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.service.constants.Constants;
import com.hms.service.entity.ActivityFeedEntity;
import com.hms.service.entity.InterviewQuestionsEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.JobDetailsEntity;
import com.hms.service.entity.JobSkillWeightageEntity;
import com.hms.service.entity.JobsEntity;
import com.hms.service.entity.QuestionEntity;
import com.hms.service.entity.SkillsEntity;
import com.hms.service.enums.FilterApplicantEnum;
import com.hms.service.exceptions.CustomSystemErrorException;
import com.hms.service.repository.ActivityFeedRepository;
import com.hms.service.repository.CandidateCreationRepository;
import com.hms.service.repository.InterviewAnalysisRepository;
import com.hms.service.repository.InterviewQuestionsRepository;
import com.hms.service.repository.InterviewSessionRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.JobDetailsRepository;
import com.hms.service.repository.JobSkillWeightageRepository;
import com.hms.service.repository.JobsRepository;
import com.hms.service.repository.OfferRepository;
import com.hms.service.repository.QuestionRepository;
import com.hms.service.repository.ResumeAnalysisRepository;
import com.hms.service.repository.SkillsRepository;
import com.hms.service.request.JobRequest;
import com.hms.service.request.JobSkillRequest;
import com.hms.service.response.JobApplicantsResponse;
import com.hms.service.response.JobsDashboardResponse;
import com.hms.service.response.JobsResponse;
import com.hms.service.response.SkillsResponse;
import com.hms.service.service.IJobService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class JobServiceImpl implements IJobService {

	@Autowired
	private JobsRepository jobsRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private JobDetailsRepository jobDetailsRepository;

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private InterviewAnalysisRepository interviewAnalysisRepository;

	@Autowired
	private CandidateCreationRepository candidateCreationRepository;

	@Autowired
	private ResumeAnalysisRepository resumeAnalysisRepository;

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private SkillsRepository skillsRepository;

	@Autowired
	private InterviewQuestionsRepository interviewQuestionsRepository;

	@Autowired
	private InterviewSessionRepository interviewSessionRepository;

	@Autowired
	private JobSkillWeightageRepository jobSkillWeightageRepository;

//	@Autowired
//	private UserServiceImpl userService;

	@Autowired
	private HttpServletRequest httpServletRequest;

//	@Autowired
//	private InfospokeWebisteFeign infospokeWebsiteFeign;
//
//	@Autowired
//	private InfospokeATSFeign infospokeATSFeign;

	@Autowired
	private ActivityFeedRepository activityFeedRepository;

	@Transactional
	@Override
	public ApiResponse<?> addJob(JobRequest request) {

		log.info("JobServiceImpl::Inside the addJob method");

		int jobCodeCount = jobsRepository.existsByJobCode(request.getJobCode());

		if (jobCodeCount > 0) {
			return ApiResponse.failure(Constants.JOB_CODE_ALREADY_EXISTS);
		}

		JobsEntity jobEntity = new JobsEntity();

		jobEntity.setExperience(request.getExperience());
		jobEntity.setJobCode(request.getJobCode());
		jobEntity.setJobCountry(request.getJobCountry());
		jobEntity.setJobTitle(request.getJobTitle());
		jobEntity.setJobLevel(request.getJobLevel());
		jobEntity.setJobLocation(request.getJobLocation());
		jobEntity.setJobMode(request.getJobMode());
		jobEntity.setJobType(request.getJobType());
		jobEntity.setJobInfo(request.getJobInfo());
		String authHeader = httpServletRequest.getHeader("Authorization");
		String userName = "";
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			userName = jwtService.extractUsernameFromClaims(token);
		}
		jobEntity.setCreatedBy(userName);
		jobEntity.setIsOpen(true);
		jobEntity.setCreatedDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));

		jobsRepository.save(jobEntity);

		String skillNames = request.getSkills().stream()
				.map(skill -> skillsRepository.findById(skill.getSkillId()).map(SkillsEntity::getSkillName).orElse(""))
				.collect(Collectors.joining(", "));

		JobDetailsEntity jobDetailsEntity = new JobDetailsEntity();

		jobDetailsEntity.setJobDescription(request.getJobDescription());
		jobDetailsEntity.setJobRequirements(request.getJobRequirements());
		jobDetailsEntity.setQualification(request.getQualification());
		jobDetailsEntity.setSkills(skillNames);
		jobDetailsEntity.setCreatedBy(request.getCreatedBy());
		jobDetailsEntity.setCreatedDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));
		jobDetailsEntity.setJobId(jobEntity.getJobId());

		jobDetailsRepository.save(jobDetailsEntity);

		for (JobSkillRequest jobSkillRequest : request.getSkills()) {

			JobSkillWeightageEntity jobSkillWeightageEntity = new JobSkillWeightageEntity();

			jobSkillWeightageEntity.setJobId(jobEntity.getJobId());
			jobSkillWeightageEntity.setSkillId(jobSkillRequest.getSkillId());
			jobSkillWeightageEntity.setCategoryId(jobSkillRequest.getCategoryId());
			jobSkillWeightageEntity.setExperienceLevel(jobSkillRequest.getExperienceLevel());
			jobSkillWeightageEntity.setWeightage(jobSkillRequest.getWeightage());

			jobSkillWeightageRepository.save(jobSkillWeightageEntity);

			List<QuestionEntity> questions = questionRepository.findExactQuestions(jobSkillRequest.getSkillId(),
					jobSkillRequest.getExperienceLevel(), jobSkillRequest.getWeightage());

			if (questions.isEmpty()) {
				throw new RuntimeException(Constants.NO_QUESTION_FOUND_FOR_SKILL_ID + jobSkillRequest.getSkillId()
						+ Constants.EXPERIENCE + jobSkillRequest.getExperienceLevel() + Constants.WEIGHTAGE
						+ jobSkillRequest.getWeightage());
			}

			QuestionEntity questionEntity = questions.get(new Random().nextInt(questions.size()));

			InterviewQuestionsEntity interviewQuestionsEntity = new InterviewQuestionsEntity();

			interviewQuestionsEntity.setJobId(jobEntity.getJobId());
			interviewQuestionsEntity.setQuestionId(questionEntity.getQuestionId());
			interviewQuestionsEntity.setAssignedWeightage(questionEntity.getQuestionWeightage());

			interviewQuestionsRepository.save(interviewQuestionsEntity);
		}

		ActivityFeedEntity activityFeedEntity = new ActivityFeedEntity();
		activityFeedEntity.setTimeStamp(LocalDateTime.now(ZoneId.of(Constants.REGION)));
		activityFeedEntity.setActivity(Constants.JOB + request.getJobTitle() + Constants.WAS_PUBLISHED);
		activityFeedRepository.save(activityFeedEntity);

		log.info("JobsServiceImpl:Job was stored in the activity logs");

		log.info("JobServiceImpl::Exit from the addJob method");

		return ApiResponse.success(Constants.JOB_ADDED_SUCCESSFULLY);
	}

	@Override
	public ApiResponse<?> getAllJobs(Boolean isOpen) {

		log.info("JobServiceImpl::Inside getAllJobs");

		List<JobsEntity> entities = jobsRepository.findAll(Sort.by(Sort.Direction.DESC, Constants.CREATED_DATE));

		List<JobsResponse> jobsResponse = entities.stream()

				.filter(job -> Boolean.TRUE.equals(isOpen) ? Boolean.TRUE.equals(job.getIsOpen()) : true)

				.map(job -> {
					

					JobsResponse response = new JobsResponse();
					BeanUtils.copyProperties(job, response);

					response.setApplicantCount(jobApplicationRepository.countByJobId(job.getJobId()));

					response.setInterview(interviewAnalysisRepository.countByJobId(job.getJobId()));

					JobDetailsEntity details = jobDetailsRepository.findByJobId(job.getJobId());

					if (details != null) {
						response.setQualification(details.getQualification());
						response.setJobRequirements(details.getJobRequirements());
						response.setJobDescription(details.getJobDescription());
					}

					List<JobSkillWeightageEntity> skillEntities = jobSkillWeightageRepository
							.findByJobId(job.getJobId());

					if (skillEntities != null && !skillEntities.isEmpty()) {

						List<Integer> skillIds = skillEntities.stream().map(JobSkillWeightageEntity::getSkillId)
								.collect(Collectors.toList());

						Map<Integer, String> skillMap = skillsRepository.findSkillNamesByIds(skillIds).stream()
								.collect(Collectors.toMap(obj -> (Integer) obj[0], obj -> (String) obj[1]));

						List<JobSkillRequest> skillList = skillEntities.stream().map(skill -> {

							JobSkillRequest skillReq = new JobSkillRequest();

							skillReq.setSkillId(skill.getSkillId());
							skillReq.setCategoryId(skill.getCategoryId());
							skillReq.setExperienceLevel(skill.getExperienceLevel());
							skillReq.setWeightage(skill.getWeightage());
							skillReq.setSkillName(skillMap.get(skill.getSkillId()));

							return skillReq;
						}).collect(Collectors.toList());

						response.setSkills(skillList);
					}

					return response;
				}).collect(Collectors.toList());

		log.info("JobServiceImpl::Exit getAllJobs");

		return ApiResponse.success(ResponseCode.SUCCESS, "Jobs fetched successfully", jobsResponse);
	}

	@Override
	@Transactional
	public ApiResponse<?> deleteJob(Integer jobId) {

		log.info("JobServiceImpl::Inside the deleteJob method");

		boolean result = jobsRepository.existsById(jobId);

		if (!result) {
			return ApiResponse.failure(Constants.NO_JOB_FOUND);
		}

		jobDetailsRepository.deleteByJobId(jobId);
		jobSkillWeightageRepository.deleteByJobId(jobId);
		interviewQuestionsRepository.deleteByJobId(jobId);
		jobsRepository.deleteById(jobId);

		log.info("JobServiceImpl::Exit from the deleteJob method");

		return ApiResponse.success(Constants.JOB_DELETED_SUCCESSFULLY);
	}

	@Override
	public ApiResponse<?> getJobDetailsById(Integer jobId) {

		log.info("JobServiceImpl::Inside getJobDetailsById");

		JobsResponse response = new JobsResponse();

		Optional<JobsEntity> optionalJob = jobsRepository.findByJobId(jobId);

		if (optionalJob.isEmpty()) {
			return ApiResponse.failure(Constants.NO_JOB_FOUND);
		}

		JobsEntity jobsEntity = optionalJob.get();
		BeanUtils.copyProperties(jobsEntity, response);

		JobDetailsEntity detailsEntity = jobDetailsRepository.findByJobId(jobsEntity.getJobId());

		if (detailsEntity != null) {

			response.setQualification(detailsEntity.getQualification());
			response.setJobRequirements(detailsEntity.getJobRequirements());
			response.setJobDescription(detailsEntity.getJobDescription());

			List<JobSkillWeightageEntity> skillEntities = jobSkillWeightageRepository
					.findByJobId(jobsEntity.getJobId());

			if (skillEntities != null && !skillEntities.isEmpty()) {

				List<Integer> skillIds = skillEntities.stream().map(JobSkillWeightageEntity::getSkillId)
						.collect(Collectors.toList());

				Map<Integer, String> skillMap = skillsRepository.findSkillNamesByIds(skillIds).stream()
						.collect(Collectors.toMap(obj -> (Integer) obj[0], obj -> (String) obj[1]));

				List<JobSkillRequest> skillList = skillEntities.stream().map(skillEntity -> {

					JobSkillRequest skill = new JobSkillRequest();

					skill.setSkillId(skillEntity.getSkillId());
					skill.setCategoryId(skillEntity.getCategoryId());
					skill.setExperienceLevel(skillEntity.getExperienceLevel());
					skill.setWeightage(skillEntity.getWeightage());

					skill.setSkillName(skillMap.get(skillEntity.getSkillId()));

					return skill;
				}).collect(Collectors.toList());

				response.setSkills(skillList);
			}
		}

		int totalApplicants = jobApplicationRepository.countByJobId(jobId);

		long interviewCompleted = interviewAnalysisRepository.countByJobId(jobId);

		long resumeCompleted = resumeAnalysisRepository.countByJobId(jobId);

		long shortlistedCount = resumeAnalysisRepository.countByJobIdAndStatusIgnoreCase(jobId, Constants.SHORTLISTED);

//	    long offerReleased = candidateCreationRepository.countByJobIdAndStatusNotIgnoreCase(
//	            jobId, Constants.OFFER_SENT); Temporary Changes
		long offerReleased = candidateCreationRepository.countByJobIdAndStatusIgnoreCase(jobId, Constants.JOINED);

		long hiredCount = candidateCreationRepository.countByJobIdAndStatusIgnoreCase(jobId, Constants.JOINED);

		response.setApplicantCount(totalApplicants);
		response.setResumeCount(resumeCompleted);
		response.setShortlisted(shortlistedCount);
		response.setInterviewCount(interviewCompleted);
		response.setOfferReleased(offerReleased);
		response.setHiredCount(hiredCount);

		log.info("JobServiceImpl::Exit getJobDetailsById");

		return ApiResponse.success(ResponseCode.SUCCESS, "sucess", response);
	}

	@Transactional
	@Override
	public ApiResponse<?> updateJobDetailsById(JobRequest request) {

		log.info("JobServiceImpl::Inside updateJobDetailsById method");

		Optional<JobsEntity> optionalJob = jobsRepository.findById(request.getJobId());

		if (optionalJob.isEmpty()) {
			return ApiResponse.failure(Constants.NO_JOB_FOUND);
		}

		JobsEntity jobEntity = optionalJob.get();

		jobEntity.setExperience(request.getExperience());
		jobEntity.setJobCode(request.getJobCode());
		jobEntity.setJobCountry(request.getJobCountry());
		jobEntity.setJobTitle(request.getJobTitle());
		jobEntity.setJobLevel(request.getJobLevel());
		jobEntity.setJobLocation(request.getJobLocation());
		jobEntity.setJobMode(request.getJobMode());
		jobEntity.setJobType(request.getJobType());
		jobEntity.setJobInfo(request.getJobInfo());
		jobEntity.setCreatedBy(request.getUpdatedBy());
		jobEntity.setIsOpen(request.getIsOpen());
		jobEntity.setCreatedDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));

		jobsRepository.save(jobEntity);

		JobDetailsEntity jobDetailsEntity = jobDetailsRepository.findByJobId(request.getJobId());

		String skillNames = request.getSkills().stream()
				.map(skill -> skillsRepository.findById(skill.getSkillId()).map(SkillsEntity::getSkillName).orElse(""))
				.collect(Collectors.joining(", "));

		jobDetailsEntity.setJobDescription(request.getJobDescription());
		jobDetailsEntity.setJobRequirements(request.getJobRequirements());
		jobDetailsEntity.setQualification(request.getQualification());
		jobDetailsEntity.setSkills(skillNames);
		jobDetailsEntity.setUpdatedBy(request.getUpdatedBy());
		jobDetailsEntity.setUpdatedDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));

		jobDetailsRepository.save(jobDetailsEntity);

		for (JobSkillRequest skillRequest : request.getSkills()) {

			JobSkillWeightageEntity jobSkillWeightageEntity = jobSkillWeightageRepository
					.findByJobIdAndSkillId(jobEntity.getJobId(), skillRequest.getSkillId());

			if (jobSkillWeightageEntity != null) {
				jobSkillWeightageEntity.setCategoryId(skillRequest.getCategoryId());
				jobSkillWeightageEntity.setExperienceLevel(skillRequest.getExperienceLevel());
				jobSkillWeightageEntity.setWeightage(skillRequest.getWeightage());
			} else {
				jobSkillWeightageEntity = new JobSkillWeightageEntity();
				jobSkillWeightageEntity.setJobId(jobEntity.getJobId());
				jobSkillWeightageEntity.setSkillId(skillRequest.getSkillId());
				jobSkillWeightageEntity.setCategoryId(skillRequest.getCategoryId());
				jobSkillWeightageEntity.setExperienceLevel(skillRequest.getExperienceLevel());
				jobSkillWeightageEntity.setWeightage(skillRequest.getWeightage());
			}

			jobSkillWeightageRepository.save(jobSkillWeightageEntity);

			List<QuestionEntity> questions = questionRepository.findExactQuestions(skillRequest.getSkillId(),
					skillRequest.getExperienceLevel(), skillRequest.getWeightage());

			if (questions.isEmpty()) {
				throw new RuntimeException(Constants.NO_QUESTION_FOUND_FOR_SKILL_ID + skillRequest.getSkillId()
						+ Constants.EXPERIENCE + skillRequest.getExperienceLevel() + Constants.WEIGHTAGE
						+ skillRequest.getWeightage());
			}

			QuestionEntity questionEntity = questions.get(new Random().nextInt(questions.size()));

			InterviewQuestionsEntity interviewQuestionsEntity = interviewQuestionsRepository
					.findByJobIdAndSkillId(jobEntity.getJobId(), skillRequest.getSkillId());

			if (interviewQuestionsEntity != null) {

				interviewQuestionsEntity.setQuestionId(questionEntity.getQuestionId());
				interviewQuestionsEntity.setAssignedWeightage(questionEntity.getQuestionWeightage());

				interviewQuestionsRepository.save(interviewQuestionsEntity);

			} else {

				InterviewQuestionsEntity interviewQuestions = new InterviewQuestionsEntity();

				interviewQuestions.setJobId(jobEntity.getJobId());
				interviewQuestions.setQuestionId(questionEntity.getQuestionId());
				interviewQuestions.setAssignedWeightage(questionEntity.getQuestionWeightage());

				interviewQuestionsRepository.save(interviewQuestions);
			}
		}

		log.info("JobServiceImpl::Exit updateJobDetailsById method");

		return ApiResponse.success(Constants.JOB_UPDATED_SUCCESSFULLY);
	}

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

		JobsEntity job = jobsRepository.findById(entity.getJobId())
				.orElseThrow(() -> new CustomSystemErrorException(Constants.JOB_MAPPED_TO_APPLICANT_NOT_FOUND));

		JobApplicantsResponse jobApplicantsResponse = new JobApplicantsResponse();
		BeanUtils.copyProperties(entity, jobApplicantsResponse);

		jobApplicantsResponse.setJobTitle(job.getJobTitle());
		jobApplicantsResponse.setJobCode(job.getJobCode());

		log.info("JobServiceImpl: Exit from getApplicantDetailsById method");

		return ApiResponse.success(ResponseCode.SUCCESS, "Applicant fetched", jobApplicantsResponse);
	}

	@Override
	public ApiResponse<?> getAllSkills() {

		log.info("SkillsServiceImpl::Inside the getAllSkills method");

		List<SkillsEntity> skillsEntities = skillsRepository.findAll(Sort.by(Sort.Direction.DESC, "skillId"));

		List<SkillsResponse> skills = skillsEntities.stream()
				.map(skill -> new SkillsResponse(skill.getSkillId(), skill.getSkillName())).toList();

		log.info("SkillsServiceImpl::Exit from get getAllSkills method");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", skills);
	}

	@Override
	public ApiResponse<?> getAllJobsDashboardCounts() {

		JobsDashboardResponse response = new JobsDashboardResponse();

		long openJobs = jobsRepository.countByIsOpenTrue();

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

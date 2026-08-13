package com.hms.service.serviceImpl;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.dto.CompletedStageDto;
import com.hms.service.entity.ActivityFeedEntity;
import com.hms.service.entity.CandidateCreationDetailsEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.InterviewCurrentStageEntity;
import com.hms.service.entity.InterviewFeedbackEntity;
import com.hms.service.entity.InterviewPlanEntity;
import com.hms.service.entity.InterviewRoundDropDownEntity;
import com.hms.service.entity.InterviewScheduleEntity;
import com.hms.service.entity.InterviewSessionEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.enums.FilterApplicantEnum;
import com.hms.service.exceptions.CustomSystemErrorException;
import com.hms.service.repository.ActivityFeedRepository;
import com.hms.service.repository.CandidateCreationDetailsRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.InterviewAnalysisRepository;
import com.hms.service.repository.InterviewCurrentStageRepository;
import com.hms.service.repository.InterviewFeedbackRepository;
import com.hms.service.repository.InterviewPlanRepository;
import com.hms.service.repository.InterviewRoundDropDownRepository;
import com.hms.service.repository.InterviewScheduleRepository;
import com.hms.service.repository.InterviewSessionRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.OfferDetailsRepository;
import com.hms.service.repository.ResumeAnalysisRepository;
import com.hms.service.request.JobApplicationRequest;
import com.hms.service.response.JobApplicantsResponse;
import com.hms.service.response.JobsDashboardResponse;
import com.hms.service.service.ICandidateService;
import com.hms.service.service.IJobService;
import com.hms.service.utils.JwtService;
import com.hms.service.utils.PasswordGenerator;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class JobServiceImpl implements IJobService {

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private InterviewPlanRepository interviewPlanRepository;
	
	@Autowired
	private OfferDetailsRepository offerDetailsRepository;

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
	private InterviewRoundDropDownRepository interviewRoundDropDownRepository;

	@Autowired
	private ResumeAnalysisRepository resumeAnalysisRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private InterviewSessionRepository interviewSessionRepository;

	@Autowired
	private MinioClient minioClient;

	@Autowired
	private ActivityFeedRepository activityFeedRepository;

	@Autowired
	private CandidateCreationDetailsRepository candidateCreationDetailsRepository;
	
	@Autowired
	private ICandidateService iCandidateService;

	@Autowired
	private MailServiceImpl mailService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${spring.mail.username}")
	private String fromEmail;

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

		InterviewScheduleEntity schedule = interviewScheduleRepository.findTopByApplicantIdOrderByIdDesc(entity.getId())
				.orElse(null);

		InterviewCurrentStageEntity current = interviewCurrentStageRepository
				.findTopByApplicationIdOrderByIdDesc(entity.getId());
		DepartmentsEntity department = departmentsRepository.findById(jobs.getDepartmentId()).orElse(null);

		JobApplicantsResponse response = new JobApplicantsResponse();

		BeanUtils.copyProperties(entity, response);

		response.setJobTitle(jobs.getJobTitle());
		response.setJobCode(jobs.getJobCode());
		response.setLocation(jobs.getLocation());
		response.setMinExperience(jobs.getMinExperience());
		response.setMaxExperience(jobs.getMaxExperience());

		response.setPlanName(interview.getPlanName());

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

		int totalStages = interview.getRounds() != null ? interview.getRounds().size() : 0;

		response.setNoOfStages(totalStages);

		List<InterviewFeedbackEntity> feedbackList = interviewFeedbackRepository.findByApplicantId(entity.getId());

		List<CompletedStageDto> completedStageDetails = new ArrayList<>();

		for (InterviewFeedbackEntity feedback : feedbackList) {

			CompletedStageDto dto = new CompletedStageDto();

			dto.setStageTypeId(feedback.getCurrentStageId());

			InterviewRoundDropDownEntity round = interviewRoundDropDownRepository.findById(feedback.getCurrentStageId())
					.orElse(null);

			dto.setStageName(round != null ? round.getRoundName() : null);

			completedStageDetails.add(dto);
		}

		Optional<InterviewSessionEntity> interviewSessionOpt = interviewSessionRepository
				.findFirstByApplicationIdAndStatusIgnoreCase(entity.getId(), "COMPLETED");

		if (interviewSessionOpt.isPresent()) {

			CompletedStageDto aiStage = new CompletedStageDto();
			aiStage.setStageTypeId(1);
			aiStage.setStageName("AI Interview");

			
			completedStageDetails.add(aiStage);
		}

		response.setCompletedStages(completedStageDetails.size());
		response.setCompletedStageDetails(completedStageDetails);

		log.info("JobServiceImpl: Exit from getApplicantDetailsById method");

		return ApiResponse.success(ResponseCode.SUCCESS, "Applicant fetched successfully", response);
	}

	@Override
	public ApiResponse<?> getAllJobsDashboardCounts() {

		JobsDashboardResponse response = new JobsDashboardResponse();

		long openJobs = createJobDetailsRepository.countByIsOpenTrue();

		long applicants = jobApplicationRepository.count();

		long interviews = interviewAnalysisRepository.count();
		
		long offersAccepted = offerDetailsRepository.countByOfferStatusIgnoreCase("Accepted");
		 

//		long offersAccepted = offerRepository.count();

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

	//	List<Object[]> candidateData = candidateCreationDetailsRepository.findStatusByApplicationIds(applicationIds);

		Map<Integer, String> candidateStatusMap = new HashMap<>();

//		for (Object[] obj : candidateData) {
//			Integer appId = (Integer) obj[0];
//			String dbStatus = (String) obj[1];
//			candidateStatusMap.put(appId, dbStatus);
//		}

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

//	// upload to s3 bucket
//	private String uploadToS3(MultipartFile file, Integer jobId, JobApplicationRequest request) throws IOException {
//		log.info("Uploading to S3 for job ID: {}", jobId);
//		String originalFileName = file.getOriginalFilename();
//
//		String fileKey = Constants.BUCKET_FOLDER + jobId + "_" + request.getFirstName() + "_" + originalFileName;
//
//		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(Constants.BUCKET).key(fileKey)
//				.contentType(file.getContentType()).contentLength(file.getSize()).build();
//
//		s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//
//		return fileKey;
//
//	}

	@Override
	public ApiResponse<?> jobApplication(JobApplicationRequest request, MultipartFile cv,
			MultipartFile additionalFile) {

		log.info("JobsServiceImpl : Inside jobApplication");

		try {

			String authHeader = httpServletRequest.getHeader("Authorization");
			String token = authHeader.substring(7);
			Long userId = jwtService.extractUserId(token);

			CandidateCreationDetailsEntity candidate = candidateCreationDetailsRepository
					.findByEmail(request.getEmail());

			if (candidate != null) {
				request.setCandidateCreation(false);
			}

			// Upload files first
			List<String> resumeKeys = null;
			List<String> additionalFileKeys = null;

			if (cv != null && !cv.isEmpty()) {
				resumeKeys = uploadToMinio(cv, request.getJobId(), request);
			}

			if (additionalFile != null && !additionalFile.isEmpty()) {
				additionalFileKeys = uploadToMinio(additionalFile, request.getJobId(), request);
			}

			String applicationResumeKey = resumeKeys != null ? resumeKeys.get(0) : null;
			String candidateResumeKey = (resumeKeys != null && resumeKeys.size() > 1) ? resumeKeys.get(1) : null;

			String applicationAdditionalFileKey = additionalFileKeys != null ? additionalFileKeys.get(0) : null;
			String candidateAdditionalFileKey = (additionalFileKeys != null && additionalFileKeys.size() > 1)
					? additionalFileKeys.get(1)
					: null;

			String username = request.getEmail();
			String temporaryPassword = null;

			// Candidate Exists
			if (candidate != null) {

				Optional<JobApplicationEntity> existingApplication = jobApplicationRepository
						.findByPhNoAndEmailAndJobId(request.getPhNo(), request.getEmail(), request.getJobId());

				if (existingApplication.isPresent()) {
					return ApiResponse.failure(ResponseCode.FAILURE,
							Constants.JOB_ALREADY_APPLIED_WITH_THE_SAME_EMAIL_AND_NUMBER);
				}

				return createJobApplication(request, applicationResumeKey, applicationAdditionalFileKey, userId,
						username, null,candidate);

			}

			// Candidate Doesn't Exist
			log.info("creating candidate");
			temporaryPassword = PasswordGenerator.generatePassword(8);
			String candidateId = iCandidateService.generateCandidateId();

			candidate = new CandidateCreationDetailsEntity();
			candidate.setCandidateId(candidateId);
			candidate.setFirstName(request.getFirstName());
			candidate.setLastName(request.getLastName());
			candidate.setEmail(request.getEmail());
			candidate.setPhoneNumber(request.getPhNo());
			candidate.setPassword(passwordEncoder.encode(temporaryPassword));
			candidate.setResume(candidateResumeKey);
			candidate.setAdditionalFile(candidateAdditionalFileKey);

			candidateCreationDetailsRepository.save(candidate);

			return createJobApplication(request, applicationResumeKey, applicationAdditionalFileKey, userId, username,
					temporaryPassword,candidate);

		} catch (Exception e) {

			log.error("Exception while applying job", e);

			return ApiResponse.failure(ResponseCode.FAILURE, Constants.FAILED_TO_SUBMIT_APPLICATION);
		}
	}

	private ApiResponse<?> createJobApplication(JobApplicationRequest request, String resumeKey,
			String additionalFileKey, Long recruiterId, String username, String temporaryPassword, CandidateCreationDetailsEntity candidate) {
           
			Optional<CreateJobDetailsEntity> job = createJobDetailsRepository.findById(request.getJobId());
			
			if(job.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Job not found for the requested jobId");
			}
			CreateJobDetailsEntity jobDetails=job.get();
			JobApplicationEntity entity = new JobApplicationEntity();

			entity.setJobId(request.getJobId());
			entity.setFirstName(request.getFirstName());
			entity.setLastName(request.getLastName());
			entity.setEmail(request.getEmail());
			entity.setPhNo(request.getPhNo());
			entity.setReferral(request.getReferral());
			entity.setCandidate(candidate);
			entity.setRecruiterId(recruiterId.intValue());
			entity.setCreatedDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));
			entity.setCurrentStage(Constants.APPLIED);
			entity.setStageEntryDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));

			entity.setResume(resumeKey);
			entity.setAdditionalFile(additionalFileKey);

			entity = jobApplicationRepository.save(entity);

			String subject = Constants.YOUR_JOB_APPLICATION_HAS_BEEN_RECEIVED + jobDetails.getJobTitle() + " ("
					+ jobDetails.getJobCode() + ")";

			String body = String.format(Constants.JOB_APPLICATION_CANDIDATE_MAIL_BODY, request.getFirstName(), // Dear
					jobDetails.getJobTitle(), // Job Title
					LocalDateTime.now(ZoneId.of(Constants.REGION)), // Registered On

					username, // Username
					temporaryPassword == null ? "" : temporaryPassword // Temporary Password
			);

			mailService.sendMail(fromEmail, request.getEmail(), null, subject, body, null);

			ActivityFeedEntity activity = new ActivityFeedEntity();
			activity.setTimeStamp(LocalDateTime.now(ZoneId.of(Constants.REGION)));
			activity.setActivity(Constants.APPLICATION_RECEIVED_FROM + request.getFirstName() + Constants.FOR_THE_JOB
					+ jobDetails.getJobTitle());

			activityFeedRepository.save(activity);

			log.info("Job application submitted successfully");

			return ApiResponse.success(ResponseCode.SUCCESS, "Success",
					Constants.JOB_APPLICATION_SUBMITTED_SUCCESSFULLY);
            }
 
	

	// upload to minio bucket
	private List<String> uploadToMinio(MultipartFile file, Integer jobId, JobApplicationRequest request)
			throws Exception {

		log.info("Uploading to MinIO for job ID: {}", jobId);

		String originalFileName = file.getOriginalFilename();

		List<String> filekeys = new ArrayList<>();

		String applicationfileKey = Constants.APPLICATION_FOLDER + jobId + "_" + originalFileName;
		filekeys.add(applicationfileKey);

		minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKETNAME).object(applicationfileKey)
				.stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());

		if ((Boolean.TRUE.equals(request.getCandidateCreation()))) {

			String candidateKey = Constants.CANDIDATE_BUCKET_FOLDER + originalFileName;

			minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKETNAME).object(candidateKey)
					.stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());

			filekeys.add(candidateKey);

		}
		log.info("the file keys are" + file);

		return filekeys;

	}

	// delete from minio

	private void deleteFromMinio(String key) {

		log.info("JobServiceImpl:Inside the deleteFromMinio method");

		try {

			minioClient.removeObject(RemoveObjectArgs.builder().bucket(Constants.BUCKETNAME).object(key).build());
			log.info("JobServiceImpl: Successfully deleted from MinIO");

		} catch (Exception e) {
			log.info("JobServiceImpl::exception occured in deleteFromMinio method" + e.getMessage());
			throw new RuntimeException("Failed to delete file from MinIO", e);
		}
		log.info("JobServiceImpl: Exit from deleteFromMinio method");
	}

	
}

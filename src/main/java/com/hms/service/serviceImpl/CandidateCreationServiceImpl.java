package com.hms.service.serviceImpl;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.entity.CandidateCreationDetailsEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.InterviewCurrentStageEntity;
import com.hms.service.entity.InterviewRoundDropDownEntity;
import com.hms.service.entity.InterviewRoundEntity;
import com.hms.service.entity.InterviewScheduleEntity;
import com.hms.service.entity.InterviewSessionEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.NegotiationOfferEntity;
import com.hms.service.entity.OfferDetailsEntity;
import com.hms.service.entity.ResumeAnalysisEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.enums.ReuploadStatus;
import com.hms.service.events.ResumeReuploadRequestedEvent;
import com.hms.service.exceptions.AlreadyExistsException;
import com.hms.service.exceptions.BadRequestException;
import com.hms.service.exceptions.CustomSystemErrorException;
import com.hms.service.exceptions.OperationNotAllowedException;
import com.hms.service.exceptions.ResourceNotFoundException;
import com.hms.service.repository.CandidateCreationDetailsRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.InterviewAnalysisRepository;
import com.hms.service.repository.InterviewCurrentStageRepository;
import com.hms.service.repository.InterviewRoundDropDownRepository;
import com.hms.service.repository.InterviewRoundRepository;
import com.hms.service.repository.InterviewScheduleRepository;
import com.hms.service.repository.InterviewSessionRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.NegotiateOfferRepository;
import com.hms.service.repository.OfferDetailsRepository;
import com.hms.service.repository.ResumeAnalysisRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.ApplyJobRequest;
import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.request.ChangePasswordRequest;
import com.hms.service.request.LoginRequest;
import com.hms.service.request.Negotiation;
import com.hms.service.request.NegotiationFieldRequest;
import com.hms.service.request.ResumeReuploadRequest;
import com.hms.service.response.ApplicationTimeLineResponse;
import com.hms.service.response.CandidateDetailsResponse;
import com.hms.service.response.CandidateInterviewResponse;
import com.hms.service.response.CandidateOfferResponse;
import com.hms.service.response.LoginResponse;
import com.hms.service.response.MyApplicationResponse;
import com.hms.service.service.ICandidateService;
import com.hms.service.utils.JwtService;
import com.hms.service.utils.PasswordGenerator;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class CandidateCreationServiceImpl implements ICandidateService {

	@Autowired
	private CandidateCreationDetailsRepository candidateCreationDetailsRepository;

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private InterviewCurrentStageRepository interviewCurrentStageRepository;

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private InterviewRoundDropDownRepository interviewRoundDropDownRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InterviewScheduleRepository interviewScheduleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private InterviewSessionRepository interviewSessionRepository;

	@Autowired
	private InterviewAnalysisRepository interviewAnalysisRepository;

	@Autowired
	private MinioClient minioClient;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private NegotiateOfferRepository negotiateOfferRepository;

	@Autowired
	private InterviewRoundRepository interviewRoundRepository;

	@Autowired
	private ResumeAnalysisRepository resumeAnalysisRepository;

	@Autowired
	private OfferDetailsRepository offerDetailsRepository;

	@Value("${minio.bucketName}")
	private String bucketName;

	@Autowired
	private MailServiceImpl mailService;

	@Autowired
	private JwtService jwtService;

	@Value("${spring.mail.username}")
	private String fromEmail;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	@Transactional
	public ApiResponse<?> createCandidate(CandidateCreationRequest request, MultipartFile resume,
			MultipartFile additionalFile) {

		if (candidateCreationDetailsRepository.existsByEmailIgnoreCase(request.getEmail())) {
			throw new RuntimeException("Email already exists.");
		}

		if (candidateCreationDetailsRepository.existsByPhoneNumber(request.getPhoneNumber())) {
			throw new RuntimeException("Phone Number already exists.");
		}

		if (!request.getPassword().equals(request.getConfirmPassword())) {
			throw new RuntimeException("Password and Confirm Password do not match.");
		}

		if (resume == null || resume.isEmpty()) {
			throw new RuntimeException("Resume is mandatory.");
		}

		// Generate Candidate ID
		String candidateId = generateCandidateId();

		String resumePath = null;
		String additionalFilePath = null;

		try {

			// Upload Resume

			String resumeExtension = resume.getOriginalFilename()
					.substring(resume.getOriginalFilename().lastIndexOf("."));

			String resumeObjectName = "candidate-documents/" + candidateId + "_resume" + resumeExtension;

			minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKETNAME).object(resumeObjectName)
					.stream(resume.getInputStream(), resume.getSize(), -1).contentType(resume.getContentType())
					.build());

			resumePath = resumeObjectName;

			log.info("Resume uploaded successfully : {}", resumeObjectName);

			// Upload Cover Letter

			if (additionalFile != null && !additionalFile.isEmpty()) {

				String coverExtension = additionalFile.getOriginalFilename()
						.substring(additionalFile.getOriginalFilename().lastIndexOf("."));

				String coverObjectName = "candidate-documents/" + candidateId + "_coverletter" + coverExtension;

				minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKETNAME).object(coverObjectName)
						.stream(additionalFile.getInputStream(), additionalFile.getSize(), -1)
						.contentType(additionalFile.getContentType()).build());

				additionalFilePath = coverObjectName;

				log.info("Cover Letter uploaded successfully : {}", coverObjectName);
			}

		} catch (Exception e) {

			log.error("Error uploading files to MinIO", e);
			throw new RuntimeException("Unable to upload files to MinIO.");

		}

		CandidateCreationDetailsEntity entity = new CandidateCreationDetailsEntity();

		entity.setCandidateId(candidateId);
		entity.setFirstName(request.getFirstName().trim());
		entity.setLastName(request.getLastName().trim());
		entity.setPhoneNumber(request.getPhoneNumber());
		entity.setEmail(request.getEmail().trim());
		entity.setPassword(passwordEncoder.encode(request.getPassword()));
		entity.setResume(resumePath);
		entity.setAdditionalFile(additionalFilePath);

		candidateCreationDetailsRepository.save(entity);

		return ApiResponse.success(ResponseCode.SUCCESS, "Candidate registered successfully.", candidateId);
	}

	@Override
	public ApiResponse<?> candidateOffers() {

		String authHeader = httpServletRequest.getHeader("Authorization");

		String candidateId = "";

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);
			candidateId = jwtService.extractCandidateId(token);

		}

		List<Integer> applicantIds = jobApplicationRepository.findApplicantIdsByCandidateId(candidateId);

		List<OfferDetailsEntity> offers = offerDetailsRepository.findByJobApplication_IdIn(applicantIds);

		List<CandidateOfferResponse> response = offers.stream().map(offer -> {

			CandidateOfferResponse dto = new CandidateOfferResponse();

			dto.setOfferId(offer.getId());

			JobApplicationEntity application = offer.getJobApplication();

			dto.setApplicantId(application.getId());

			CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

			dto.setJobId(job.getJobId());

			dto.setJobTitle(job.getJobTitle());

			dto.setEmploymentType(job.getEmploymentType());

			dto.setJobLocation(job.getLocation());

			dto.setTotalCtc(offer.getTotalCtc());

			LocalDate dueDate = offer.getOfferReleasedAt().toLocalDate().plusDays(7);

			dto.setDueDate(dueDate);

			return dto;

		}).toList();

		return ApiResponse.success(ResponseCode.SUCCESS, "Offers fetched successfully", response);
	}

	@Override
	public String generateCandidateId() {

		Long sequence = candidateCreationDetailsRepository.getNextCandidateSequence();

		return String.format("CID-%d-%04d", Year.now().getValue(), sequence);
	}

	@Override
	@Transactional
	public ApiResponse<LoginResponse> login(LoginRequest request) {

		try {

			log.info("Candidate Login Started");

			if (request == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid Request");
			}

			if (request.getEmail() == null || request.getEmail().isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Email is required");
			}

			if (request.getPassword() == null || request.getPassword().isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Password is required");
			}

			Optional<CandidateCreationDetailsEntity> optionalCandidate = candidateCreationDetailsRepository
					.findByEmailIgnoreCase(request.getEmail());

			if (optionalCandidate.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid Credentials");
			}

			CandidateCreationDetailsEntity candidate = optionalCandidate.get();

			if (Boolean.TRUE.equals(candidate.getAccountLocked())) {

				if (candidate.getLockTime() != null
						&& candidate.getLockTime().plusMinutes(2).isAfter(LocalDateTime.now())) {

					return ApiResponse.failure(ResponseCode.FAILURE,
							"Account is locked. Please try again after 2 minutes.");
				}

				candidate.setAccountLocked(false);
				candidate.setFailedAttempts(0);
				candidate.setLockTime(null);

				candidateCreationDetailsRepository.save(candidate);
			}

			if (!passwordEncoder.matches(request.getPassword(), candidate.getPassword())) {

				Integer attempts = candidate.getFailedAttempts() == null ? 0 : candidate.getFailedAttempts();

				attempts++;

				candidate.setFailedAttempts(attempts);

				if (attempts >= 5) {

					candidate.setAccountLocked(true);
					candidate.setLockTime(LocalDateTime.now());

					candidateCreationDetailsRepository.save(candidate);

					return ApiResponse.failure(ResponseCode.FAILURE,
							"Account locked for 2 minutes due to multiple failed attempts.");
				}

				candidateCreationDetailsRepository.save(candidate);

				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid Credentials");
			}

			boolean isTemporaryPassword = Boolean.TRUE.equals(candidate.getTemporaryPassword());

			if (isTemporaryPassword) {

				if (candidate.getTemporaryPasswordExpiry() == null
						|| LocalDateTime.now().isAfter(candidate.getTemporaryPasswordExpiry())) {

					candidate.setTemporaryPassword(false);
					candidate.setTemporaryPasswordExpiry(null);

					candidateCreationDetailsRepository.save(candidate);

					return ApiResponse.failure(ResponseCode.FAILURE,
							"Temporary password has expired. Please use Forgot Password again.");
				}
			}

			candidate.setFailedAttempts(0);
			candidate.setAccountLocked(false);
			candidate.setLockTime(null);

			String token = jwtService.generateCandidateToken(candidate.getCandidateId(), candidate.getFirstName(),
					candidate.getLastName(), candidate.getEmail());

			candidate.setToken(token);
			candidate.setLoggedIn(true);
			candidate.setLastLogin(LocalDateTime.now());

			candidateCreationDetailsRepository.save(candidate);

			LoginResponse response = new LoginResponse();

			response.setToken(token);

			if (isTemporaryPassword) {

				return ApiResponse.success(ResponseCode.SUCCESS,
						"Login successful. Temporary password verified. Please change your password immediately.",
						response);
			}

			return ApiResponse.success(ResponseCode.SUCCESS, "Login Successful", response);

		} catch (Exception e) {

			log.error("Candidate Login Failed", e);

			return ApiResponse.failure(ResponseCode.FAILURE, "Login Failed : " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ApiResponse<?> forgotPassword(LoginRequest request) {

		try {

			log.info("Candidate Forgot Password Started");

			if (request == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid Request");
			}

			if (request.getEmail() == null || request.getEmail().isBlank()) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Email is required");
			}

			Optional<CandidateCreationDetailsEntity> optionalCandidate = candidateCreationDetailsRepository
					.findByEmailIgnoreCase(request.getEmail());

			if (optionalCandidate.isEmpty()) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Candidate not found");
			}

			CandidateCreationDetailsEntity candidate = optionalCandidate.get();

			String temporaryPassword = PasswordGenerator.generatePassword(8);

			candidate.setPassword(passwordEncoder.encode(temporaryPassword));

			candidate.setPasswordUpdatedAt(LocalDateTime.now());

			candidate.setFailedAttempts(0);
			candidate.setAccountLocked(false);
			candidate.setLockTime(null);

			candidate.setToken(null);
			candidate.setLoggedIn(false);

			candidate.setTemporaryPassword(true);

			candidate.setForcePasswordReset(true);

			candidate.setTemporaryPasswordExpiry(LocalDateTime.now().plusMinutes(15));

			candidateCreationDetailsRepository.save(candidate);

			sendForgotPasswordMail(candidate, temporaryPassword);

			return ApiResponse.success(ResponseCode.SUCCESS, "Success",
					"Temporary password sent successfully. It is valid for 15 minutes.");

		} catch (Exception e) {

			log.error("Forgot Password Failed", e);

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}

	private void sendForgotPasswordMail(CandidateCreationDetailsEntity candidate, String password) {

		String subject = "Candidate Portal - Forgot Password";

		String body = String.format(Constants.CANDIDATE_FORGOT_PASSWORD_BODY, candidate.getFirstName(),
				candidate.getCandidateId(), candidate.getEmail(), password);

		mailService.sendMail(fromEmail, candidate.getEmail(), null, subject, body, null);

	}

	@Override
	@Transactional
	public ApiResponse<?> logout(String token) {

		try {

			if (token == null || token.isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Authorization token is required.");
			}

			if (token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			Optional<CandidateCreationDetailsEntity> optionalCandidate = candidateCreationDetailsRepository
					.findByToken(token);

			if (optionalCandidate.isEmpty()) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid Token");
			}

			CandidateCreationDetailsEntity candidate = optionalCandidate.get();

			candidate.setToken(null);
			candidate.setLoggedIn(false);
			candidate.setLastLogout(LocalDateTime.now());

			candidateCreationDetailsRepository.save(candidate);

			return ApiResponse.success(ResponseCode.SUCCESS, "Logout Successful", null);

		} catch (Exception e) {

			log.error("Logout Failed", e);

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}

	public ApiResponse<?> getCandidateInterviews() {

		log.info("InterviewCurrentStageServiceImpl :: Inside getCandidateInterviews");

		List<CandidateInterviewResponse> responseList = new ArrayList<>();

		try {

			// Extract Candidate Id from JWT Token
			String authHeader = httpServletRequest.getHeader("Authorization");

			String candidateId = "";

			if (authHeader != null && authHeader.startsWith("Bearer ")) {

				String token = authHeader.substring(7);

				candidateId = jwtService.extractCandidateId(token);
			}

			// Candidate Validation

			CandidateCreationDetailsEntity candidate = candidateCreationDetailsRepository.findByCandidateId(candidateId)
					.orElse(null);

			if (candidate == null) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Candidate Not Found");
			}

			// Get all applications

			List<JobApplicationEntity> applications = jobApplicationRepository.findByCandidate(candidate);

			if (applications.isEmpty()) {

				return ApiResponse.success(ResponseCode.SUCCESS, "No Upcoming Interviews Found", responseList);
			}

			// Loop every application

			for (JobApplicationEntity application : applications) {

				// Current Pending Interview Stages

				List<InterviewCurrentStageEntity> currentStages = interviewCurrentStageRepository
						.findByApplicationIdAndInterviewCompletedFalse(application.getId());

				if (currentStages.isEmpty()) {
					continue;
				}

				for (InterviewCurrentStageEntity stage : currentStages) {

					CandidateInterviewResponse response = new CandidateInterviewResponse();

					response.setApplicationId(application.getId());

					response.setCurrentStageId(stage.getCurrentStageType());

					response.setInterviewDate(stage.getInterviewDate());

					response.setStartTime(stage.getStartTime());

					response.setEndTime(stage.getEndTime());

					response.setDuration(calculateDuration(stage.getStartTime(), stage.getEndTime()));

					InterviewRoundDropDownEntity round = interviewRoundDropDownRepository
							.findById(stage.getCurrentStageType()).orElse(null);

					log.info("Current Stage Type : {}", stage.getCurrentStageType());

					if (round != null) {

						response.setInterviewType(round.getRoundName());

						if ("AI Interview Round".equalsIgnoreCase(round.getRoundName())) {

							InterviewSessionEntity session = interviewSessionRepository
									.findByApplicationId(application.getId()).orElse(null);

							if (session != null) {
								response.setMeetingLink(session.getInterviewLink());
							}

						} else {

							InterviewScheduleEntity schedule = interviewScheduleRepository
									.findByApplicantIdAndRoundId(application.getId(), stage.getCurrentStageType())
									.orElse(null);

							log.info("Application Id : {}", application.getId());
							log.info("Round Id : {}", stage.getCurrentStageType());
							log.info("Schedule : {}", schedule);

							if (schedule != null) {

								response.setMeetingLink(schedule.getMeetingLink());
								response.setVenueDetails(schedule.getVenueDetails());
							}

						}
					}
					CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

					if (job != null) {
						response.setJobTitle(job.getJobTitle());
					}

					// Recruiter Name
					UserEntity recruiter = userRepository.findByUserId(stage.getInterviewerId()).orElse(null);

					if (recruiter != null) {
						response.setRecruiterName(recruiter.getFirstName() + " " + recruiter.getLastName());
					}

					responseList.add(response);
				}

			}

			log.info("InterviewCurrentStageServiceImpl :: Exit getCandidateInterviews");

			return ApiResponse.success(ResponseCode.SUCCESS, "Candidate Interviews fetched successfully", responseList);

		} catch (Exception e) {

			log.error("Exception : ", e);

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}

	}

	private String calculateDuration(LocalTime startTime, LocalTime endTime) {

		if (startTime == null || endTime == null) {
			return "";
		}

		Duration duration = Duration.between(startTime, endTime);

		long minutes = duration.toMinutes();

		if (minutes >= 60) {

			long hours = minutes / 60;

			long mins = minutes % 60;

			if (mins == 0) {

				return hours + " hr";
			}

			return hours + " hr " + mins + " mins";
		}

		return minutes + " mins";
	}

	@Transactional
	@Override
	public ApiResponse<?> negotiateOffer(NegotiationFieldRequest request, List<MultipartFile> files) {

		String authHeader = httpServletRequest.getHeader("Authorization");
		String candidateId = "";

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			candidateId = jwtService.extractCandidateId(token);
		}

		CandidateCreationDetailsEntity candidate = candidateCreationDetailsRepository.findByCandidateId(candidateId)
				.orElseThrow(() -> new RuntimeException("Candidate not found"));

		OfferDetailsEntity offer = offerDetailsRepository.findById(request.getOfferId())
				.orElseThrow(() -> new RuntimeException("Offer not found"));

		CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(request.getJobId());

		Optional<JobApplicationEntity> applicant = jobApplicationRepository.findById(request.getApplicantId());
		JobApplicationEntity applicants = applicant.get();
		List<String> documentNames = new ArrayList<>();

		if (files != null && !files.isEmpty()) {

			for (MultipartFile file : files) {

				String originalFileName = file.getOriginalFilename();
				String extension = "";

				if (originalFileName != null && originalFileName.contains(".")) {
					extension = originalFileName.substring(originalFileName.lastIndexOf("."));
				}

				try {
					String objectName = Constants.NEGOTIATION_DOCUMENTS + candidateId + "-" + job.getJobId()
							+ extension;

					minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKETNAME).object(objectName)
							.stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType())
							.build());

					documentNames.add(objectName);

				} catch (Exception e) {
					throw new RuntimeException("Failed to upload document", e);
				}
			}
		}
		Long totalRequestedAmount = request.getNegotiation().stream().filter(Objects::nonNull)
				.map(Negotiation::getRequestedAmount).filter(Objects::nonNull).reduce(0L, Long::sum);

		NegotiationOfferEntity entity = new NegotiationOfferEntity();

		entity.setCandidate(candidate);
		entity.setOffer(offer);
		entity.setJob(job);
		entity.setApprovalStatus("PENDING");
		entity.setOfferNegotiatedDate(LocalDate.now());
		entity.setSupportingDocuments(documentNames);
		entity.setOverallJustification(request.getOverallJustification());
		entity.setOthers(request.getOthers());
		entity.setNegotiation(request.getNegotiation());
		entity.setJoiningDate(request.getJoiningDate());
		entity.setApplicant(applicants);
		entity.setTotalRequestedAmount(totalRequestedAmount);

		negotiateOfferRepository.save(entity);
		
		offer.setNegotiation(true);
		offer.setNegotiationId(entity.getId());
		offer.setOfferStatus("Requested for Negotiation");
		offerDetailsRepository.save(offer);

		return ApiResponse.success(ResponseCode.SUCCESS, "Negotiation requested successfully", "success");
	}

	@Override
	public ApiResponse<?> getMyApplications() {

		log.info("JobServiceImpl : Inside getMyApplications");

		String authHeader = httpServletRequest.getHeader("Authorization");
		String candidateId = "";
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			candidateId = jwtService.extractCandidateId(token);

		}

		try {

			List<JobApplicationEntity> applications = jobApplicationRepository.findByCandidateCandidateIdOrderByCreatedDateDesc(candidateId);

			if (applications.isEmpty()) {
				return ApiResponse.failure(ResponseCode.SUCCESS, Constants.NO_DATA_FOUND);
			}

			List<Integer> applicationIds = applications.stream().map(JobApplicationEntity::getId).toList();

			List<Integer> jobIds = applications.stream().map(JobApplicationEntity::getJobId).distinct().toList();

			List<CreateJobDetailsEntity> jobs = createJobDetailsRepository.findByJobIdIn(jobIds);

			List<ResumeAnalysisEntity> resumeList = resumeAnalysisRepository.findByApplicationIdIn(applicationIds);

			List<InterviewSessionEntity> sessionList = interviewSessionRepository.findByApplicationIdIn(applicationIds);

			List<InterviewCurrentStageEntity> stageList = interviewCurrentStageRepository
					.findByApplicationIdInOrderByRoundOrder(applicationIds);

			List<InterviewRoundDropDownEntity> roundDropDownList = interviewRoundDropDownRepository.findAll();

			Map<Integer, CreateJobDetailsEntity> jobMap = jobs.stream()
					.collect(Collectors.toMap(CreateJobDetailsEntity::getJobId, Function.identity()));

			Map<Integer, ResumeAnalysisEntity> resumeMap = resumeList.stream()
					.collect(Collectors.toMap(ResumeAnalysisEntity::getApplicationId, Function.identity()));

			Map<Integer, InterviewSessionEntity> sessionMap = sessionList.stream()
					.collect(Collectors.toMap(InterviewSessionEntity::getApplicationId, Function.identity()));

			Map<Integer, List<InterviewCurrentStageEntity>> stageMap = stageList.stream()
					.collect(Collectors.groupingBy(InterviewCurrentStageEntity::getApplicationId));

			List<Integer> planIds = jobs.stream().map(CreateJobDetailsEntity::getPlanId).filter(Objects::nonNull)
					.distinct().toList();

			List<InterviewRoundEntity> roundList = interviewRoundRepository
					.findByInterviewPlanIdInOrderByRoundOrder(planIds);

			Map<Integer, List<InterviewRoundEntity>> roundMap = roundList.stream()
					.collect(Collectors.groupingBy(round -> round.getInterviewPlan().getId()));

			Map<Integer, InterviewRoundDropDownEntity> roundDropDownMap = roundDropDownList.stream()
					.collect(Collectors.toMap(InterviewRoundDropDownEntity::getId, Function.identity()));

			List<MyApplicationResponse> responseList = new ArrayList<>();

			for (JobApplicationEntity application : applications) {

				MyApplicationResponse response = new MyApplicationResponse();

				CreateJobDetailsEntity job = jobMap.get(application.getJobId());

				ResumeAnalysisEntity resumeAnalysis = resumeMap.get(application.getId());

				InterviewSessionEntity interviewSession = sessionMap.get(application.getId());

				List<InterviewCurrentStageEntity> currentStages = stageMap.getOrDefault(application.getId(),
						new ArrayList<>());

				List<InterviewRoundEntity> interviewRounds = new ArrayList<>();
				if (job != null && job.getPlanId() != null) {

					interviewRounds = roundMap.getOrDefault(job.getPlanId(), new ArrayList<>());
					response.setJobId(job.getJobId());
					response.setJobTitle(job.getJobTitle());
					response.setLocation(job.getLocation());
					response.setEmploymentType(job.getEmploymentType());
					if (application.getReuploadStatus() == ReuploadStatus.REQUESTED) {
						response.setReuploadStatus(application.getReuploadStatus().name());
					}
				}

				response.setApplicationId(application.getId());
				response.setAppliedDate(application.getCreatedDate());

				response.setDaysAfterApplied(
						ChronoUnit.DAYS.between(application.getCreatedDate().toLocalDate(), LocalDate.now()));

				response.setTotalRounds(interviewRounds.size());

				response.setCompletedRounds((int) currentStages.stream()
						.filter(stage -> Boolean.TRUE.equals(stage.getInterviewCompleted())).count());

				response.setCurrentRound(
						getCurrentRound(resumeAnalysis, interviewSession, currentStages, roundDropDownMap));

				response.setTimeline(
						buildTimeline(application, resumeAnalysis, interviewSession, currentStages, interviewRounds));

				responseList.add(response);
			}

			return ApiResponse.success(ResponseCode.SUCCESS, "Data fetched successfully", responseList);

		} catch (Exception e) {

			log.error("Exception while fetching my applications", e);

			return ApiResponse.failure(ResponseCode.FAILURE, "Failed to fetch applications");
		}
	}

	private List<ApplicationTimeLineResponse> buildTimeline(JobApplicationEntity application,
			ResumeAnalysisEntity resumeAnalysis, InterviewSessionEntity interviewSession,
			List<InterviewCurrentStageEntity> currentStages, List<InterviewRoundEntity> interviewRounds) {

		List<ApplicationTimeLineResponse> timeline = new ArrayList<>();

		// Applied
		ApplicationTimeLineResponse applied = new ApplicationTimeLineResponse();
		applied.setRoundName("Applied");
		applied.setCompletedDate(application.getCreatedDate());
		timeline.add(applied);

		// Resume Screening
		ApplicationTimeLineResponse screening = new ApplicationTimeLineResponse();
		screening.setRoundName("Resume Screening");

		if (resumeAnalysis != null) {
			screening.setCompletedDate(resumeAnalysis.getCreatedAt());
		}

		timeline.add(screening);

		// Configured Interview Rounds
		for (InterviewRoundEntity round : interviewRounds) {

			ApplicationTimeLineResponse response = new ApplicationTimeLineResponse();
			response.setRoundName(round.getStageName());

			if ("AI Interview".equalsIgnoreCase(round.getStageName())) {

				if (interviewSession != null) {

					if (Boolean.TRUE.equals(interviewSession.getIsScheduled())) {
						response.setScheduledDate(interviewSession.getInterviewScheduledDateTime());
					}

					if ("completed".equalsIgnoreCase(interviewSession.getStatus())) {
						response.setCompletedDate(interviewSession.getInterviewScheduledDateTime());
					}
				}
			}

			for (InterviewCurrentStageEntity stage : currentStages) {

				if (stage.getRoundOrder().equals(round.getRoundOrder())) {

					if (Boolean.TRUE.equals(stage.getInterviewCompleted())) {
						response.setCompletedDate(stage.getInterviewCompletedOn());
					}

					if (Boolean.TRUE.equals(stage.getToSchedule()) && stage.getInterviewDate() != null
							&& stage.getStartTime() != null) {

						response.setScheduledDate(stage.getInterviewDate().atTime(stage.getStartTime()));
					}

					break;
				}
			}

			timeline.add(response);
		}

		return timeline;
	}

	private String getCurrentRound(ResumeAnalysisEntity resumeAnalysis, InterviewSessionEntity interviewSession,
			List<InterviewCurrentStageEntity> currentStages,
			Map<Integer, InterviewRoundDropDownEntity> roundDropDownMap) {

		// Applied
		if (resumeAnalysis == null) {
			return "Applied";
		}

		// Resume Screening
		if (interviewSession == null) {
			return "Resume Screening";
		}

		// AI Interview is in progress (scheduled or not scheduled)
		if (!"completed".equalsIgnoreCase(interviewSession.getStatus())) {
			return "AI Interview";
		}

		// AI Interview completed but no interview rounds created yet
		if (currentStages == null || currentStages.isEmpty()) {
			return "AI Interview";
		}

		// Current interview round
		for (InterviewCurrentStageEntity stage : currentStages) {

			if (!Boolean.TRUE.equals(stage.getInterviewCompleted())) {

				InterviewRoundDropDownEntity round = roundDropDownMap.get(stage.getCurrentStageType());
				return round != null ? round.getRoundName() : "Interview";
			}
		}

		// All interview rounds completed
		InterviewCurrentStageEntity lastStage = currentStages.get(currentStages.size() - 1);

		InterviewRoundDropDownEntity round = roundDropDownMap.get(lastStage.getCurrentStageType());

		return round != null ? round.getRoundName() : "Completed";

	}

	@Override
	@Transactional
	public ApiResponse<?> changePassword(ChangePasswordRequest request, String authHeader) {

		try {

			log.info("Candidate Change Password Started");

			String token = authHeader.replace("Bearer ", "");

			String email = jwtService.extractUsername(token);

			Optional<CandidateCreationDetailsEntity> optionalCandidate = candidateCreationDetailsRepository
					.findByEmailIgnoreCase(email);

			if (optionalCandidate.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Candidate not found");
			}

			CandidateCreationDetailsEntity candidate = optionalCandidate.get();

			if (!passwordEncoder.matches(request.getOldPassword(), candidate.getPassword())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Old password is incorrect");
			}

			if (passwordEncoder.matches(request.getNewPassword(), candidate.getPassword())) {

				return ApiResponse.failure(ResponseCode.FAILURE, "New password cannot be same as old password");
			}

			candidate.setPassword(passwordEncoder.encode(request.getNewPassword()));

			candidate.setPasswordUpdatedAt(LocalDateTime.now());

			candidate.setTemporaryPassword(false);

			candidate.setTemporaryPasswordExpiry(null);

			candidate.setToken(null);

			candidate.setLoggedIn(false);

			candidateCreationDetailsRepository.save(candidate);

			log.info("Password Changed Successfully");

			return ApiResponse.success(ResponseCode.SUCCESS, "Password changed successfully", null);

		} catch (Exception e) {

			log.error("Change Password Failed", e);

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}

	@Override
	public ApiResponse<?> getCandidateById(String candidateId) {

		try {

			if (candidateId == null || candidateId.isBlank()) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Candidate Id is required");
			}

			Optional<CandidateCreationDetailsEntity> optionalCandidate = candidateCreationDetailsRepository
					.findByCandidateId(candidateId);

			if (optionalCandidate.isEmpty()) {

				return ApiResponse.failure(ResponseCode.FAILURE, "Candidate not found");
			}

			CandidateCreationDetailsEntity candidate = optionalCandidate.get();

			CandidateDetailsResponse response = new CandidateDetailsResponse();

			response.setCandidateId(candidate.getCandidateId());
			response.setFirstName(candidate.getFirstName());
			response.setLastName(candidate.getLastName());
			response.setEmail(candidate.getEmail());
			response.setPhoneNumber(candidate.getPhoneNumber());
			response.setResume(candidate.getResume());
			response.setAdditionalFile(candidate.getAdditionalFile());
			
			return ApiResponse.success(ResponseCode.SUCCESS, "Candidate Details Retrieved Successfully", response);

		} catch (Exception e) {

			log.error("Error while fetching candidate details", e);

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}

//	private void deleteResumeFromMinio(String objectName) {
//
//		try {
//
//			if (objectName != null && !objectName.isBlank()) {
//
//				minioClient.removeObject(
//						RemoveObjectArgs.builder().bucket(Constants.BUCKETNAME).object(objectName).build());
//
//				log.info("Deleted Resume : {}", objectName);
//			}
//
//		} catch (Exception e) {
//
//			log.error("Unable to delete resume from MinIO", e);
//
//			throw new RuntimeException("Unable to delete existing resume.");
//		}
//	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ApiResponse<?> raiseReuploadRequest(Integer applicationId) {

		log.info("Resume re-upload request initiated for applicationId : {}", applicationId);

		JobApplicationEntity application = jobApplicationRepository.findById(applicationId)
				.orElseThrow(() -> new ResourceNotFoundException("Application not found."));

		if (ReuploadStatus.REQUESTED.equals(application.getReuploadStatus())) {
			throw new AlreadyExistsException("Resume re-upload request has already been raised.");
		}

		if (interviewAnalysisRepository.existsByApplicationId(applicationId)) {
			throw new OperationNotAllowedException(
					"Interview is already completed. Resume re-upload request cannot be raised.");
		}

		String resumePath = application.getResume();

		if (resumePath != null && !resumePath.isBlank()) {

			try {

				minioClient.removeObject(
						RemoveObjectArgs.builder().bucket(Constants.BUCKETNAME).object(resumePath).build());

				log.info("Resume deleted successfully from MinIO : {}", resumePath);

				application.setResume(null);

			} catch (Exception ex) {

				log.error("Failed to delete resume from MinIO : {}", resumePath, ex);

				throw new CustomSystemErrorException("Unable to delete resume from MinIO.");
			}

		}
		interviewSessionRepository.findByApplicationId(applicationId).ifPresent(interviewSessionRepository::delete);

		resumeAnalysisRepository.findByApplicationId(applicationId).ifPresent(resumeAnalysisRepository::delete);

		application.setReuploadStatus(ReuploadStatus.REQUESTED);

		jobApplicationRepository.save(application);
		applicationEventPublisher.publishEvent(new ResumeReuploadRequestedEvent(applicationId));

		return ApiResponse.success("Resume re-upload request has been raised successfully.");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ApiResponse<?> uploadReuploadedResume(ResumeReuploadRequest request, MultipartFile resume) {

		log.info("Inside uploadReuploadedResume() for applicationId : {}", request.getApplicationId());

		if (resume == null || resume.isEmpty()) {
			throw new BadRequestException("Resume is required.");
		}

		JobApplicationEntity application = jobApplicationRepository.findById(request.getApplicationId())
				.orElseThrow(() -> new ResourceNotFoundException("Application not found."));

		if (application.getReuploadStatus() != ReuploadStatus.REQUESTED) {
			throw new BadRequestException("Resume re-upload request has not been raised for this application.");
		}

		String applicationResumePath = uploadApplicationResume(resume, application.getJobId());

		application.setResume(applicationResumePath);
		application.setReuploadStatus(ReuploadStatus.UPLOADED);

		if (Boolean.TRUE.equals(request.getUpdateProfileResume())) {

			CandidateCreationDetailsEntity candidate = application.getCandidate();

			if (candidate == null) {
				throw new ResourceNotFoundException("Candidate not found.");
			}

			if (candidate.getResume() != null && !candidate.getResume().isBlank()) {
				deleteCandidateResume(candidate.getResume());
			}

			String candidateResumePath = uploadCandidateResume(resume, candidate.getCandidateId());

			candidate.setResume(candidateResumePath);

			candidateCreationDetailsRepository.save(candidate);
		}

		jobApplicationRepository.save(application);

		log.info("Resume uploaded successfully for applicationId : {}", application.getId());

		return ApiResponse.success("Resume uploaded successfully.");
	}

	private String uploadApplicationResume(MultipartFile file, Integer jobId) {

		log.info("Uploading application resume to MinIO for jobId : {}", jobId);

		try {

			String originalFileName = file.getOriginalFilename();

			String objectName = Constants.APPLICATION_FOLDER + jobId + "_" + originalFileName;

			minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKETNAME).object(objectName)
					.stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());

			log.info("Application resume uploaded successfully : {}", objectName);

			return objectName;

		} catch (Exception ex) {

			log.error("Failed to upload application resume to MinIO.", ex);

			throw new CustomSystemErrorException("Unable to upload application resume.");
		}
	}

	private String uploadCandidateResume(MultipartFile resume, String candidateId) {

		log.info("Uploading candidate resume to MinIO for candidateId : {}", candidateId);

		try {

			String extension = resume.getOriginalFilename().substring(resume.getOriginalFilename().lastIndexOf("."));

			String objectName = "candidate-documents/" + candidateId + "_resume" + extension;

			minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKETNAME).object(objectName)
					.stream(resume.getInputStream(), resume.getSize(), -1).contentType(resume.getContentType())
					.build());

			log.info("Candidate resume uploaded successfully : {}", objectName);

			return objectName;

		} catch (Exception ex) {

			log.error("Failed to upload candidate resume to MinIO.", ex);

			throw new CustomSystemErrorException("Unable to upload candidate resume.");
		}
	}

	private void deleteCandidateResume(String resumePath) {

		log.info("Deleting candidate resume from MinIO : {}", resumePath);

		try {

			minioClient
					.removeObject(RemoveObjectArgs.builder().bucket(Constants.BUCKETNAME).object(resumePath).build());

			log.info("Candidate resume deleted successfully : {}", resumePath);

		} catch (Exception ex) {

			log.error("Failed to delete candidate resume : {}", resumePath, ex);

			throw new CustomSystemErrorException("Unable to delete candidate resume from MinIO.");
		}
	}

	@Override
	@Transactional
	public ApiResponse<?> applyJob(ApplyJobRequest request, MultipartFile resume) {

		try {

			log.info("Apply Job Started");

			if (request == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid Request");
			}

			if (request.getCandidateId() == null || request.getCandidateId().isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Candidate Id is required");
			}

			if (request.getJobId() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Job Id is required");
			}

			CandidateCreationDetailsEntity candidate = candidateCreationDetailsRepository
					.findByCandidateId(request.getCandidateId()).orElse(null);

			if (candidate == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Candidate not found");
			}

			CreateJobDetailsEntity job = createJobDetailsRepository.findById(request.getJobId()).orElse(null);

			if (job == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Job not found");
			}

			boolean alreadyApplied = jobApplicationRepository
					.existsByCandidate_CandidateIdAndJobId(request.getCandidateId(), request.getJobId());

			if (alreadyApplied) {
				return ApiResponse.failure(ResponseCode.FAILURE, "You have already applied for this job.");
			}

			String resumePath;

			ReuploadStatus status;

			if (resume != null && !resume.isEmpty()) {

				resumePath = uploadJobApplicationResume(resume, candidate.getCandidateId(), request.getJobId());

				status = ReuploadStatus.UPLOADED;

			} else {

				resumePath = copyCandidateResumeToJobApplication(candidate.getResume(), candidate.getCandidateId(),
						request.getJobId());

				status = ReuploadStatus.NOT_REUPLOADED;
			}

			JobApplicationEntity application = new JobApplicationEntity();

			application.setCandidate(candidate);
			application.setJobId(job.getJobId());

			application.setFirstName(candidate.getFirstName());
			application.setLastName(candidate.getLastName());
			application.setEmail(candidate.getEmail());
			application.setPhNo(candidate.getPhoneNumber());

			application.setResume(resumePath);
			application.setAdditionalFile(candidate.getAdditionalFile());

			application.setCreatedDate(LocalDateTime.now());
			application.setStageEntryDate(LocalDateTime.now());

			application.setCurrentStage("Application Submitted");
			application.setJobStatus("Applied");

			application.setRejected(false);

			application.setReuploadStatus(status);

			jobApplicationRepository.save(application);

			return ApiResponse.success(ResponseCode.SUCCESS, "success", "Job Applied Successfully.");

		} catch (Exception e) {

			log.error("Apply Job Failed", e);

			throw new RuntimeException(e);
		}
	}

	private String copyCandidateResumeToJobApplication(String candidateResumePath, String candidateId, Integer jobId) {

		try {

			String extension = candidateResumePath.substring(candidateResumePath.lastIndexOf("."));

			String objectName = "job-applications/" + candidateId + "_" + jobId + "_resume" + extension;

			try (InputStream inputStream = minioClient.getObject(
					GetObjectArgs.builder().bucket(Constants.BUCKETNAME).object(candidateResumePath).build())) {

				minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKETNAME).object(objectName)
						.stream(inputStream, -1, 10485760).build());
			}

			return objectName;

		} catch (Exception e) {

			throw new RuntimeException("Unable to copy candidate resume.", e);
		}
	}

	private String uploadJobApplicationResume(MultipartFile resume, String candidateId, Integer jobId) {

		try {

			String extension = resume.getOriginalFilename().substring(resume.getOriginalFilename().lastIndexOf("."));

			String objectName = "job-applications/" + candidateId + "_" + jobId + "_resume" + extension;

			minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKETNAME).object(objectName)
					.stream(resume.getInputStream(), resume.getSize(), -1).contentType(resume.getContentType())
					.build());

			return objectName;

		} catch (Exception e) {

			throw new RuntimeException("Unable to upload resume.", e);
		}
	}
}
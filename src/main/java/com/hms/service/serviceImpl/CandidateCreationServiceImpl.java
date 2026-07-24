package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;

import java.time.Duration;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.entity.CandidateCreationDetailsEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.InterviewCurrentStageEntity;
import com.hms.service.entity.InterviewRoundDropDownEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.CandidateCreationDetailsRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.InterviewCurrentStageRepository;
import com.hms.service.repository.InterviewRoundDropDownRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.CandidateCreationRequest;

import com.hms.service.request.LoginRequest;
import com.hms.service.response.LoginResponse;

import com.hms.service.request.CandidateInterviewRequest;
import com.hms.service.response.CandidateInterviewResponse;

import com.hms.service.service.ICandidateService;
import com.hms.service.utils.JwtService;
import com.hms.service.utils.PasswordGenerator;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
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
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MinioClient minioClient;

	@Value("${minio.bucketName}")
	private String bucketName;

	@Autowired
	private MailServiceImpl mailService;

	@Autowired
	private JwtService jwtService;

	@Value("${spring.mail.username}")
	private String fromEmail;

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

	private String generateCandidateId() {

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

			boolean validPassword = passwordEncoder.matches(request.getPassword(), candidate.getPassword());

			if (!validPassword) {

				int attempts = candidate.getFailedAttempts() == null ? 0 : candidate.getFailedAttempts();

				attempts++;

				candidate.setFailedAttempts(attempts);

				if (attempts >= 5) {

					candidate.setAccountLocked(true);
					candidate.setLockTime(LocalDateTime.now());

					candidateCreationDetailsRepository.save(candidate);

					return ApiResponse.failure(ResponseCode.FAILURE, "Account locked for 2 minutes.");
				}

				candidateCreationDetailsRepository.save(candidate);

				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid Credentials");
			}

			if (Boolean.TRUE.equals(candidate.getTemporaryPassword())) {

				if (candidate.getTemporaryPasswordExpiry() == null
						|| LocalDateTime.now().isAfter(candidate.getTemporaryPasswordExpiry())) {

					candidate.setTemporaryPassword(false);
					candidate.setTemporaryPasswordExpiry(null);

					candidateCreationDetailsRepository.save(candidate);

					return ApiResponse.failure(ResponseCode.FAILURE,
							"Temporary password expired. Please use Forgot Password again.");
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

			if (Boolean.TRUE.equals(candidate.getTemporaryPassword())) {

				return ApiResponse.success(ResponseCode.SUCCESS,
						"Temporary password verified. Please change your password.", response);
			}

			return ApiResponse.success(ResponseCode.SUCCESS, "Login Successful", response);

		} catch (Exception e) {

			log.error("Candidate Login Failed", e);

			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
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

	public ApiResponse<?> getCandidateInterviews(CandidateInterviewRequest request) {

		log.info("InterviewCurrentStageServiceImpl :: Inside getCandidateInterviews");

		List<CandidateInterviewResponse> responseList = new ArrayList<>();

		try {

			// Candidate Validation

			CandidateCreationDetailsEntity candidate = candidateCreationDetailsRepository
					.findByCandidateId(String.valueOf(request.getCandidateId())).orElse(null);

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

				if (currentStages.isEmpty()) {
					continue;
				}

					CandidateInterviewResponse response = new CandidateInterviewResponse();

					response.setApplicationId(application.getId());

					response.setCurrentStageId(stage.getId());

					response.setInterviewDate(stage.getInterviewDate());

					response.setStartTime(stage.getStartTime());

					response.setEndTime(stage.getEndTime());

					response.setDuration(calculateDuration(stage.getStartTime(), stage.getEndTime()));

					InterviewRoundDropDownEntity round = interviewRoundDropDownRepository
							.findById(stage.getCurrentStageType()).orElse(null);

				     if (round != null) {
						    response.setInterviewType(round.getRoundName());
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

}
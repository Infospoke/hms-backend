package com.hms.service.serviceImpl;

import java.time.Duration;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.service.entity.CandidateCreationDetailsEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.InterviewCurrentStageEntity;
import com.hms.service.entity.InterviewRoundEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.OfferDetailsEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.CandidateCreationDetailsRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.InterviewCurrentStageRepository;
import com.hms.service.repository.InterviewRoundRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.OfferDetailsRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.request.CandidateInterviewRequest;
import com.hms.service.response.CandidateInterviewResponse;
import com.hms.service.service.ICandidateService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.MinioClient;
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
	private OfferDetailsRepository offerDetailsRepository;
	
	@Autowired
	private InterviewCurrentStageRepository interviewCurrentStageRepository;
	
	@Autowired
	private InterviewRoundRepository interviewRoundRepository;
	
	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MinioClient minioClient;

	@Value("${minio.bucketName}")
	private String bucketName;

	@Override
	public ApiResponse<?> createCandidate(CandidateCreationRequest request) {

		// Email Duplicate Validation
		if (candidateCreationDetailsRepository.existsByEmailIgnoreCase(request.getEmail())) {
			throw new RuntimeException("Email already exists.");
		}

		// Phone Number Duplicate Validation
		if (candidateCreationDetailsRepository.existsByPhoneNumber(request.getPhoneNumber())) {
			throw new RuntimeException("Phone Number already exists.");
		}

		// Password Validation
		if (!request.getPassword().equals(request.getConfirmPassword())) {
			throw new RuntimeException("Password and Confirm Password do not match.");
		}

		// Resume Mandatory
		if (request.getResume() == null || request.getResume().isEmpty()) {
			throw new RuntimeException("Resume is mandatory.");
		}

		// Upload Resume
//        String resumePath = minioClient.uploadFile(
//                request.getResume(),
//                "candidate-resume");

		// Upload Cover Letter (Optional)
		String additionalFilePath = null;

		if (request.getAdditionalFile() != null && !request.getAdditionalFile().isEmpty()) {
//
//            additionalFilePath = minioClient.uploadFile(
//                    request.getAdditionalFile(),
//                    "candidate-cover-letter");
		}

		CandidateCreationDetailsEntity entity = new CandidateCreationDetailsEntity();

		entity.setFirstName(request.getFirstName());
		entity.setLastName(request.getLastName());
		entity.setPhoneNumber(request.getPhoneNumber());
		entity.setEmail(request.getEmail());

		entity.setPassword(passwordEncoder.encode(request.getPassword()));

		// entity.setResume(resumePath);
		entity.setAdditionalFile(additionalFilePath);

		// Save first to generate DB ID
		entity = candidateCreationDetailsRepository.save(entity);

		// Generate Candidate ID
		String candidateId = String.format("CID-%d-%04d", Year.now().getValue(), entity.getId());

		entity.setCandidateId(candidateId);

		candidateCreationDetailsRepository.save(entity);
		return ApiResponse.success(ResponseCode.SUCCESS, "Dashboard counts fetched successfully", candidateId);
	}

	@Override
	public ApiResponse<?> getCandidateInterviews(CandidateInterviewRequest request) {

	    log.info("InterviewCurrentStageServiceImpl :: Inside getCandidateInterviews");

	    List<CandidateInterviewResponse> responseList = new ArrayList<>();

	    try {

	        // Candidate Validation

			CandidateCreationDetailsEntity candidate = candidateCreationDetailsRepository
					.findByCandidateId(String.valueOf(request.getCandidateId())).orElse(null);

	        if (candidate == null) {

	            return ApiResponse.failure(ResponseCode.FAILURE,
	                    "Candidate Not Found");
	        }

	        // Get all applications

	        List<JobApplicationEntity> applications = jobApplicationRepository.findByCandidate(candidate);

			if (applications.isEmpty()) {

				return ApiResponse.success(ResponseCode.SUCCESS, "No Upcoming Interviews Found", responseList);
			}

	        // Loop every application

	        for (JobApplicationEntity application : applications) {

	            // Check Offer Details

	            OfferDetailsEntity offerDetails = offerDetailsRepository.findTopByJobApplicationOrderByIdDesc(application);

				if (offerDetails != null && ("HIRED".equalsIgnoreCase(offerDetails.getInterviewCompletionStatus())
						|| "REJECTED".equalsIgnoreCase(offerDetails.getInterviewCompletionStatus()))) {

					continue;
				}

	            // Current Pending Interview Stages

				List<InterviewCurrentStageEntity> currentStages = interviewCurrentStageRepository
						.findByApplicationIdAndInterviewCompletedFalse(application.getId());

	            if (currentStages.isEmpty()) {
	                continue;
	            }

	            for (InterviewCurrentStageEntity stage : currentStages) {

	            	CandidateInterviewResponse response = new CandidateInterviewResponse();

	                response.setApplicationId(application.getId());

	                response.setCurrentStageId(stage.getId());

	                response.setInterviewDate(stage.getInterviewDate());

	                response.setStartTime(stage.getStartTime());

	                response.setEndTime(stage.getEndTime());

					response.setDuration(calculateDuration(stage.getStartTime(), stage.getEndTime()));

					InterviewRoundEntity round = interviewRoundRepository.findByStageTypeId(stage.getCurrentStageType())
							.orElse(null);

					if (round != null) {
						response.setInterviewType(round.getStageName());
					}
					
					CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

					if (job != null) {
						response.setJobTitle(job.getJobTitle());
					}
					
					 // Recruiter Name
					UserEntity recruiter = userRepository.findByUserId(stage.getInterviewerId()).orElse(null);

				    if (recruiter != null) {
				        response.setRecruiterName(
				                recruiter.getFirstName() + " " + recruiter.getLastName());
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
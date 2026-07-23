package com.hms.service.serviceImpl;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.service.entity.CandidateCreationDetailsEntity;
import com.hms.service.repository.CandidateCreationDetailsRepository;
import com.hms.service.request.CandidateCreationRequest;
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
}
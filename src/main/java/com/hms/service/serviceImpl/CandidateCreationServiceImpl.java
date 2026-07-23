package com.hms.service.serviceImpl;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.entity.CandidateCreationDetailsEntity;
import com.hms.service.repository.CandidateCreationDetailsRepository;
import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.service.ICandidateService;
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
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MinioClient minioClient;

	@Value("${minio.bucketName}")
	private String bucketName;

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
}
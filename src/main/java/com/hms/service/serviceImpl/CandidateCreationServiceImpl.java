package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

		if (request.getResume() == null || request.getResume().isEmpty()) {
			throw new RuntimeException("Resume is mandatory.");
		}
		CandidateCreationDetailsEntity entity = new CandidateCreationDetailsEntity();

		entity.setFirstName(request.getFirstName().trim());
		entity.setLastName(request.getLastName().trim());
		entity.setPhoneNumber(request.getPhoneNumber());
		entity.setEmail(request.getEmail().trim());
		entity.setPassword(passwordEncoder.encode(request.getPassword()));

		entity = candidateCreationDetailsRepository.save(entity);

		String candidateId = String.format("CID-%d-%04d", Year.now().getValue(), entity.getId());

		entity.setCandidateId(candidateId);

		String candidateName = entity.getFirstName() + "_" + entity.getLastName();

		String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

		String resumePath = null;
		String additionalFilePath = null;

		try {

			String resumeExtension = request.getResume().getOriginalFilename()
					.substring(request.getResume().getOriginalFilename().lastIndexOf("."));

			String resumeObjectName = "candidate-resumes/" + candidateName + "_" + candidateId + "_" + date + "_resume"
					+ resumeExtension;

			minioClient.putObject(PutObjectArgs.builder().bucket(bucketName.trim()).object(resumeObjectName)
					.stream(request.getResume().getInputStream(), request.getResume().getSize(), -1)
					.contentType(request.getResume().getContentType()).build());

			resumePath = resumeObjectName;

			log.info("Resume uploaded successfully : {}", resumeObjectName);

			if (request.getAdditionalFile() != null && !request.getAdditionalFile().isEmpty()) {

				String coverExtension = request.getAdditionalFile().getOriginalFilename()
						.substring(request.getAdditionalFile().getOriginalFilename().lastIndexOf("."));

				String coverObjectName = "candidate-cover-letters/" + candidateName + "_" + candidateId + "_" + date
						+ "_coverletter" + coverExtension;

				minioClient.putObject(PutObjectArgs.builder().bucket(bucketName.trim()).object(coverObjectName)
						.stream(request.getAdditionalFile().getInputStream(), request.getAdditionalFile().getSize(), -1)
						.contentType(request.getAdditionalFile().getContentType()).build());

				additionalFilePath = coverObjectName;

				log.info("Cover Letter uploaded successfully : {}", coverObjectName);
			}

		} catch (Exception e) {

			log.error("Error uploading files to MinIO", e);

			throw new RuntimeException("Unable to upload files to MinIO.");

		}

		// Save MinIO Paths

		entity.setResume(resumePath);
		entity.setAdditionalFile(additionalFilePath);

		candidateCreationDetailsRepository.save(entity);

		return ApiResponse.success(ResponseCode.SUCCESS, "Candidate registered successfully.", candidateId);
	}
}
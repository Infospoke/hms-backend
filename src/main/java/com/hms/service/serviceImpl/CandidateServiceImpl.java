package com.hms.service.serviceImpl;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.service.constants.Constants;
import com.hms.service.entity.ActivityFeedEntity;
import com.hms.service.entity.BGVEntity;
import com.hms.service.entity.CandidateInfoEntity;
import com.hms.service.entity.JobsEntity;
import com.hms.service.entity.OfferEntity;
import com.hms.service.repository.ActivityFeedRepository;
import com.hms.service.repository.BGVRepository;
import com.hms.service.repository.CandidateCreationRepository;
import com.hms.service.repository.JobsRepository;
import com.hms.service.repository.OfferRepository;
import com.hms.service.request.CandidateCreationRequest;
import com.hms.service.request.CandidateUpdateRequest;
import com.hms.service.response.CandidateInfoResponse;
import com.hms.service.response.CandidateUpdateResponse;
import com.hms.service.response.JobTitleResponse;
import com.hms.service.service.CandidateService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class CandidateServiceImpl implements CandidateService {

	@Autowired
	private CandidateCreationRepository candidateRepository;

	@Autowired
	private JobsRepository jobRepository;

	@Autowired
	private BGVRepository bgvrepository;

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private MinioClient minioClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ActivityFeedRepository activityFeedRepository;

	
	@Override
	public ApiResponse<?> addCandidate(CandidateCreationRequest candidateCreationRequest, MultipartFile offerLetter) {
		log.info("CandidateServiceImpl:: Inside the addCandidate Method");
 
		CandidateInfoEntity candidateInfoEntity = new CandidateInfoEntity();
 
		Integer candidates = candidateRepository.findByEmail(candidateCreationRequest.getEmail());
		if (candidates > 0) {
 
			return ApiResponse.failure(
					ResponseCode.FAILURE,
					Constants.CANDIDATE_ALREADY_EXISTS,
					List.of("Candidate already exists")
			);
		}
 
		candidateInfoEntity.setFirstName(candidateCreationRequest.getFirstName());
		candidateInfoEntity.setLastName(candidateCreationRequest.getLastName());
		candidateInfoEntity.setPhoneNumber(candidateCreationRequest.getPhoneNumber());
		candidateInfoEntity.setDepartment(candidateCreationRequest.getDepartment());
		candidateInfoEntity.setEmail(candidateCreationRequest.getEmail());
		candidateInfoEntity.setJobTitle(candidateCreationRequest.getJobTitle());
		log.info("jobTitle is"+candidateCreationRequest.getJobTitle());
		candidateInfoEntity.setStatus(candidateCreationRequest.getStatus());
		candidateInfoEntity.setCreatedDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));
		candidateInfoEntity.setJobCountry(candidateCreationRequest.getJobCountry());
		candidateInfoEntity.setGithubURL(candidateCreationRequest.getGithubURL());
		candidateInfoEntity.setLinkedinURL(candidateCreationRequest.getLinkedinURL());
		candidateInfoEntity.setApplicationId(candidateCreationRequest.getApplicationId());
 
		JobsEntity data = jobRepository.findByJobTitle(candidateCreationRequest.getJobTitle()).orElse(null);
 
		if (candidateCreationRequest.getStatus().equals(Constants.OFFER_SENT)) {
			candidateRepository.save(candidateInfoEntity);
 
			try {
				String fileKey = Constants.BUCKET_FOLDER + data.getJobId() + Constants.UNDER_SCORE
						+ candidateInfoEntity.getId() + Constants.UNDER_SCORE + candidateCreationRequest.getFirstName()
						+ Constants.UNDER_SCORE + offerLetter.getOriginalFilename();
 
				uploadToMinio(offerLetter, fileKey);
 
				OfferEntity offerEntity = new OfferEntity();
				offerEntity.setCandidateId(candidateInfoEntity);
				offerEntity.setCtc(candidateCreationRequest.getCtc());
				offerEntity.setOfferLetterPath(fileKey);
				offerEntity.setIssueDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));
				offerEntity.setStatus(candidateCreationRequest.getStatus());
				offerRepository.save(offerEntity);
 
				ActivityFeedEntity activityFeedEntity = new ActivityFeedEntity();
				activityFeedEntity.setTimeStamp(LocalDateTime.now());
				activityFeedEntity.setActivity("Offer sent to "+candidateCreationRequest.getFirstName()+" "+candidateCreationRequest.getLastName());
				activityFeedRepository.save(activityFeedEntity);
				log.info("CandidateServiceImpl :: Data Stored in the Activity Feed");
 
			} catch (Exception e) {
				log.info("CandidateServiceImpl::Error Ocurred in Uploading to Minio Method" + e.getMessage());
			}
 
			String from = null;
			if (candidateCreationRequest.getJobCountry().equals(Constants.INDIA)) {
				from = Constants.NOREPLY_INDIA;
			} else if (candidateCreationRequest.getJobCountry().equals(Constants.USA)) {
				from = Constants.NOREPLY_USA;
			}
 
			String mailbody = String.format(Constants.OFFER_LETTER_MAIL_BODY,
					candidateCreationRequest.getFirstName(),
					candidateInfoEntity.getJobTitle());
 
//			mailServiceImpl.sendMail(from, candidateCreationRequest.getEmail(), null,
//					Constants.CANDIDATE_CREATION_SUBJECT, mailbody, offerLetter);
		}
 
		if (candidateCreationRequest.getStatus().equals(Constants.JOINED)) {
			String from;
			if (candidateCreationRequest.getJobCountry().equals(Constants.INDIA)) {
				from = Constants.CAREERS_INDIA;
			} else {
				from = Constants.CAREERS_USA;
			}
 
			String mailbody = String.format(Constants.IT_MAIL_BODY,
					candidateCreationRequest.getFirstName(),
					candidateInfoEntity.getJobTitle());
 
//			mailServiceImpl.sendMail(from, Constants.IT_MAIL_ID, null,
//					Constants.CANDIDATE_JOINED_SUBJECT, mailbody, null);
		}
 
		candidateRepository.save(candidateInfoEntity);
 
		ActivityFeedEntity activityFeedEntity = new ActivityFeedEntity();
		activityFeedEntity.setTimeStamp(LocalDateTime.now(ZoneId.of(Constants.REGION)));
		activityFeedEntity.setActivity(candidateCreationRequest.getFirstName()+" "+candidateCreationRequest.getLastName()
				+ Constants.CONVERTED_FROM_APPLICANT_TO_CANDIDATE);
		activityFeedRepository.save(activityFeedEntity);
 
		log.info("CandidateServiceImpl :: Data Stored in the Activity Feed");
 
		log.info("CandidateServiceImpl:: Exit from the addCandidate Method");
 
		return ApiResponse.success(
				ResponseCode.SUCCESS,
				"success",
				Constants.CANDIDATE_CREATION
				
		);
	}
	@Override
	public ApiResponse<?> updateCandidate(Map<String, MultipartFile> files, String data) {
		log.info("CandidateServiceImpl::Inside the updateCandidate Method");
 
		CandidateUpdateRequest candidateUpdateRequest = null;
 
		try {
			candidateUpdateRequest = objectMapper.readValue(data, CandidateUpdateRequest.class);
		} catch (Exception e) {
			log.info("CandidateServiceImpl::Exception Occured in updateCandidate method"+ e.getMessage());
 
			return ApiResponse.failure(
					ResponseCode.FAILURE,
					Constants.EXCEPTION_OCCURED,
					List.of(e.getMessage())
			);
		}
 
		CandidateInfoEntity candidateInfoEntity = candidateRepository.findById(candidateUpdateRequest.getId()).get();
 
		if (candidateInfoEntity == null) {
 
			return ApiResponse.failure(
					ResponseCode.FAILURE,
					Constants.CANDIDATE_NOT_FOUND,
					List.of("Candidate not found")
			);
		}
 
		CandidateUpdateResponse candidateUpdateResponse = new CandidateUpdateResponse();
 
		BeanUtils.copyProperties(candidateUpdateRequest, candidateUpdateResponse,
				getNullPropertyNames(candidateUpdateRequest));
 
		BeanUtils.copyProperties(candidateUpdateResponse, candidateInfoEntity,
				getNullPropertyNames(candidateUpdateRequest));
 
		candidateInfoEntity.setUpdatedDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));
 
		if (candidateUpdateRequest.getStatus() != null) {
 
			if (candidateUpdateRequest.getStatus().equals(Constants.ACCEPTED)) {
				CandidateInfoEntity candidateEntity = candidateRepository.findById(candidateUpdateRequest.getId()).get();
				candidateEntity.setAcceptedDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));
				candidateEntity.setStatus(Constants.ACCEPTED);
				candidateRepository.save(candidateEntity);
			}
 
			JobsEntity jobsEntity = jobRepository.findByJobTitle(candidateUpdateRequest.getJobTitle()).orElse(null);
 
			if (candidateUpdateRequest.getStatus().equals(Constants.OFFER_SENT)) {
				candidateRepository.save(candidateInfoEntity);
 
				MultipartFile offerLetter = null;
 
				if (files != null && files.containsKey(Constants.OFFERLETTER)) {
					offerLetter = files.get(Constants.OFFERLETTER);
				}
 
				try {
					String fileKey = Constants.BUCKET_FOLDER + jobsEntity.getJobId() + Constants.UNDER_SCORE
							+ candidateInfoEntity.getId() + Constants.UNDER_SCORE
							+ candidateUpdateRequest.getFirstName() + Constants.UNDER_SCORE
							+ offerLetter.getOriginalFilename();
 
					uploadToMinio(offerLetter, fileKey);
 
					OfferEntity offerEntity = new OfferEntity();
					offerEntity.setCandidateId(candidateInfoEntity);
					offerEntity.setCtc(candidateUpdateRequest.getCtc());
					offerEntity.setOfferLetterPath(fileKey);
					offerEntity.setIssueDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));
					offerEntity.setStatus(candidateUpdateRequest.getStatus());
					offerRepository.save(offerEntity);
 
				} catch (Exception e) {
					log.info("CandidateServiceImpl::Error Ocurred in Uploading to Minio Method" + e.getMessage());
				}
			}
 
			if (candidateUpdateRequest.getStatus().equals(Constants.REJECTED)
					|| candidateUpdateRequest.getStatus().equals(Constants.OFFER_REJECTED)) {
				candidateInfoEntity.setDescription(candidateUpdateRequest.getDescription());
			}
 
			if (candidateUpdateRequest.getStatus().equals(Constants.BGV_CLEARED)
					|| candidateUpdateRequest.getStatus().equals(Constants.BGV_REJECTED)) {
 
				MultipartFile bgvFile = null;
 
				if (files != null && files.containsKey(Constants.BGV_FILE)) {
					bgvFile = files.get(Constants.BGV_FILE);
				}
 
				Optional<BGVEntity> candidate = bgvrepository.findByCandidateId_Id(candidateUpdateRequest.getId());
 
				if (candidate.isPresent()) {
					BGVEntity bgv = candidate.get();
					uploadToBGV(candidateInfoEntity, candidateUpdateRequest, bgvFile, bgv);
					log.info("CandidateServiceImpl::BGV is succesfuly Updated");
				} else if (!candidate.isPresent()) {
					BGVEntity bgv = new BGVEntity();
					uploadToBGV(candidateInfoEntity, candidateUpdateRequest, bgvFile, bgv);
					log.info("CandidateServiceImpl::BGV is succesfuly Initiated");
				}
			}
 
			if (candidateUpdateRequest.getStatus().equals(Constants.JOINED)) {
				String from;
				String jobCountry;
 
				if (candidateUpdateRequest.getJobCountry() != null) {
					jobCountry = candidateUpdateRequest.getJobCountry();
				} else {
					jobCountry = candidateInfoEntity.getJobCountry();
				}
 
				if (jobCountry.equals(Constants.INDIA)) {
					from = Constants.CAREERS_INDIA;
				} else {
					from = Constants.CAREERS_USA;
				}
 
				String mailbody = String.format(Constants.IT_MAIL_BODY,
						candidateInfoEntity.getFirstName(),
						candidateInfoEntity.getJobTitle());
 
//				mailServiceImpl.sendMail(from, Constants.IT_MAIL_ID, null, Constants.CANDIDATE_JOINED_SUBJECT, mailbody,
//						null);
 
				ActivityFeedEntity activityFeedEntity = new ActivityFeedEntity();
				activityFeedEntity.setTimeStamp(LocalDateTime.now());
				activityFeedEntity.setActivity(candidateInfoEntity.getFirstName()+" "+candidateInfoEntity.getLastName()
						+ Constants.JOINED_IN_THE_ORGANIZATION);
				activityFeedRepository.save(activityFeedEntity);
 
				log.info("CandidateServiceImpl :: Data Stored in the Activity Feed");
			}
		}
 
		candidateRepository.save(candidateInfoEntity);
 
		log.info("CandidateServiceImpl::Exit from the updateCandidate Method");
 
		return ApiResponse.success(
				ResponseCode.SUCCESS,
				"success",
				Constants.CANDIDATE_UPDATED_SUCCESSFULLY
				
		);
	}
 
	@Override
	public ApiResponse<?> getAllCandidates() {
		log.info("CandidateServiceImpl::Inside the getAllCandidates method");
 
		List<CandidateInfoEntity> entities = candidateRepository
				.findAll(Sort.by(Sort.Direction.DESC, Constants.CREATED_DATE));
 
		List<CandidateInfoResponse> candidateRespone = entities.stream().map(entity -> {
			CandidateInfoResponse candidateInfoResponse = new CandidateInfoResponse();
			BeanUtils.copyProperties(entity, candidateInfoResponse);
			return candidateInfoResponse;
		}).collect(Collectors.toList());
 
		log.info("CandidateServiceImpl::Exit from the getAllCandidates method");
 
		return ApiResponse.success(
				ResponseCode.SUCCESS,
				"Candidates fetched successfully",
				candidateRespone
		);
	}
	@Override
	public ApiResponse<?> getCandidateById(int id) {
		log.info("CandidateServiceImpl::Inside the getCandidateById method");
 
		CandidateInfoResponse candidateInfoResponse = new CandidateInfoResponse();
 
		CandidateInfoEntity candidateInfoEntity = candidateRepository.findById(id).get();
 
		if (candidateInfoEntity == null) {
			candidateInfoResponse.setResponseMessage(Constants.CANDIDATE_NOT_FOUND);
			candidateInfoResponse.setResponseCode(Constants.STATUS_CODE_FAILURE);
 
			return ApiResponse.failure(
					ResponseCode.FAILURE,
					Constants.CANDIDATE_NOT_FOUND,
					List.of("Candidate not found")
			);
		}
 
		BeanUtils.copyProperties(candidateInfoEntity, candidateInfoResponse);
 
		List<OfferEntity> offer = offerRepository.getByCandidateId_Id(candidateInfoEntity.getId());
		offer.sort(Comparator.comparing(OfferEntity::getIssueDate).reversed());
 
		List<Map<String, String>> offersList = new ArrayList<>();
 
		for (int i = 0; i < offer.size(); i++) {
			OfferEntity offerEntity = offer.get(i);
 
			Map<String, String> map = new HashMap<>();
 
			map.put(Constants.CTC, String.valueOf(offerEntity.getCtc()));
			map.put(Constants.OFFERLETTER, offerEntity.getOfferLetterPath());
			map.put(Constants.ISSUED_DATE, offerEntity.getIssueDate().toString());
 
			offersList.add(map);
		}
 
		candidateInfoResponse.setOffers(offersList);
 
		Optional<BGVEntity> bgv = bgvrepository.findByCandidateId_Id(id);
 
		if (bgv.isPresent()) {
			BGVEntity verification = bgv.get();
 
			candidateInfoResponse.setReportUrl(verification.getReportUrl());
			candidateInfoResponse.setFinalStatus(verification.getFinalStatus());
			candidateInfoResponse.setVendorStatus(verification.getVendorStatus());
		}
 
		log.info("CandidateServiceImpl::Exit from the getCandidateById method");
 
		return ApiResponse.success(
				ResponseCode.SUCCESS,
				"Candidate fetched successfully",
				candidateInfoResponse
		);
	}
//	@Override
//	@Transactional
//	public Response deleteCandidatebyId(int id) {
//		log.info("CandidateServiceImpl::Inside the deleteCandidatebyId method");
//		Response response = new Response();
//
//		ResponseMessage responseMessage = new ResponseMessage();
//		String email = candidateRepository.findById(id).get().getEmail();
//
//		if (email == null) {
//			response.setResponseMessage(Constants.CANDIDATE_NOT_FOUND);
//			response.setResponseCode(Constants.STATUS_CODE_SUCCESS);
//			return response;
//		}
//		String applicantdDeleteByEmailResponse = infospokeWebsiteFeign.getApplicantDetailsByEmail(email).getBody();
//
//		if (applicantdDeleteByEmailResponse != null) {
//			jobServiceImpl.deleteApplicantById(Integer.parseInt(applicantdDeleteByEmailResponse));
//		}
//		List<OfferEntity> offer = offerRepository.getByCandidateId_Id(id);
//		for (int i = 0; i < offer.size(); i++) {
//
//			deleteFromMinio(offer.get(i).getOfferLetterPath());
//		}
//		offerRepository.deleteByCandidateId_Id(id);
//
//		preOnBoardingServiceImpl.deletePreOnBoardingCandidateById(id);
//		Optional<BGVEntity> bGVEntity = bgvrepository.findByCandidateId_Id(id);
//		if (bGVEntity.isPresent()) {
//			String reportUrl = bGVEntity.get().getReportUrl();
//			deleteFromMinio(reportUrl);
//		}
//		bgvrepository.deleteByCandidateId_Id(id);
//		candidateRepository.deleteById(id);
//		response.setResponseMessage(Constants.CANDIDATE_DELETED_SUCCESSFULLY);
//		response.setResponseCode(Constants.STATUS_CODE_SUCCESS);
//
//		log.info("CandidateServiceImpl::Exit from the deleteCandidatebyId method");
//		return response;
//	}
	@Override
	public ApiResponse<JobTitleResponse> jobsByCountry(String country) {
		log.info("CandidateServiceImpl::Inside the jobsByCountry method");
 
		List<JobsEntity> entities = jobRepository.findByJobCountry(country);
 
		List<String> jobTitles = entities.stream()
				.map(JobsEntity::getJobTitle)
				.collect(Collectors.toList());
 
		log.info("CandidateServiceImpl::Exit from the jobsByCountry method");
 
		return ApiResponse.success(
				ResponseCode.SUCCESS,
				"Jobs fetched successfully",
				new JobTitleResponse(jobTitles)
		);
	}
 
	// upload to s3 bucket
//	private void uploadToS3(MultipartFile offerLetter, String fileKey) throws Exception {
//		log.info("CandidateServiceImpl::Inside the uploadToS3 method");
//		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(Constants.BUCKET).key(fileKey)
//				.contentType(offerLetter.getContentType()).build();
//		try {
//			s3Client.putObject(putObjectRequest,
//					RequestBody.fromInputStream(offerLetter.getInputStream(), offerLetter.getSize()));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		log.info("CandidateServiceImpl::Exit from the uploadToS3 method");
//	}
 
	// upload to minio bucket
	private void uploadToMinio(MultipartFile offerLetter, String fileKey) throws Exception {
 
		log.info("CandidateServiceImpl::Inside uploadToMinio method");
 
		minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKET).object(fileKey)
				.stream(offerLetter.getInputStream(), offerLetter.getSize(), -1)
				.contentType(offerLetter.getContentType()).build());
 
		log.info("CandidateServiceImpl::Exit from uploadToMinio method");
	}
 
//	public void deleteFromS3(String Key) {
//		log.info("Inside the deleteFromS3 method");
//		DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(Constants.BUCKET).key(Key).build();
//		s3Client.deleteObject(request);
//		log.info("Exit from the deleteFromS3 method");
//
//	}
 
	public void deleteFromMinio(String key) {
 
		log.info("Inside the deleteFromMinio method");
 
		try {
 
			minioClient.removeObject(RemoveObjectArgs.builder().bucket(Constants.BUCKET).object(key).build());
			log.info("Successfully deleted from MinIO");
 
		} catch (Exception e) {
			log.error("Error occurred while deleting from MinIO", e.getMessage());
			throw new RuntimeException(Constants.FAILED_TO_DELETE_FILE_FROM_MINIO, e);
		}
 
		log.info("Exit from the deleteFromMinio method");
	}
 
	// uploading to BGV
	private void uploadToBGV(CandidateInfoEntity candidateInfoEntity, CandidateUpdateRequest candidateUpdateRequest,
			MultipartFile bgvFile, BGVEntity bgv) {
		log.info("CandidateServiceImpl::Inside the uploadToBGV method");
		bgv.setCandidateId(candidateInfoEntity);
		if (candidateUpdateRequest.getVendorStatus() != null) {
			bgv.setVendorStatus(candidateUpdateRequest.getVendorStatus());
		}
		if (candidateUpdateRequest.getFinalStatus() != null) {
			bgv.setFinalStatus(candidateUpdateRequest.getFinalStatus());
		}
		bgv.setReportUrl(candidateUpdateRequest.getId() + Constants.UNDER_SCORE + candidateInfoEntity.getFirstName()
				+ Constants.UNDER_SCORE + bgvFile.getOriginalFilename());
		bgvrepository.save(bgv);
		try {
			String fileKey = Constants.BUCKET_FOLDER + candidateUpdateRequest.getId() + Constants.UNDER_SCORE
					+ candidateUpdateRequest.getFirstName() + Constants.UNDER_SCORE + bgvFile.getOriginalFilename();
			uploadToMinio(bgvFile, fileKey);
		} catch (Exception e) {
			log.info("CandidateServiceImpl::Exception occured in uploadToBGV method"+ e.getMessage());
		}
		log.info("CandidateServiceImpl::Exit from the uploadToBGV method");
	}
 
	private String[] getNullPropertyNames(CandidateUpdateRequest request) {
		log.info("Inside the getNullPropertyNames method");
		BeanWrapper src = new BeanWrapperImpl(request);
		log.info("Exit from the getNullPropertyNames method");
		return Arrays.stream(src.getPropertyDescriptors()).map(PropertyDescriptor::getName)
				.filter(name -> src.getPropertyValue(name) == null).toArray(String[]::new);
 
	}
	@Override
	public void downloadOfferLetter(LocalDateTime issueDate, String type, String action, HttpServletResponse response) {
		log.info("CandidateServiceImpl:Inside downloadOfferLetter method");
 
		OfferEntity offerEntity = offerRepository.findByIssueDate(issueDate);
 
		String objectKey;
 
		if (Constants.OFFERLETTER.equalsIgnoreCase(type)) {
			objectKey = offerEntity.getOfferLetterPath();
		} else {
			throw new RuntimeException(Constants.INVALID_FILE_TYPE);
		}
		String fileName = Paths.get(objectKey).getFileName().toString();
 
		try {
 
			InputStream minioStream = minioClient
					.getObject(GetObjectArgs.builder().bucket(Constants.BUCKET_FOLDER).object(objectKey).build());
 
			String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
 
			response.setContentType("application/octet-stream");
			response.setCharacterEncoding("UTF-8");
 
			response.setHeader("Content-Disposition",
					(Constants.VIEW.equalsIgnoreCase(action) ? "inline" : "attachment") + "; filename*=UTF-8''"
							+ encodedFileName);
 
			IOUtils.copy(minioStream, response.getOutputStream());
			response.flushBuffer();
 
			minioStream.close();
 
		} catch (Exception e) {
			log.info("CandidateServiceImpl::exception occured in downloadOfferLetter method"+e.getMessage());
			throw new RuntimeException("Error downloading file from MinIO", e);
		}
	}
}

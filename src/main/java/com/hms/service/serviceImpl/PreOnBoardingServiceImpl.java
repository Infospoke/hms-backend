package com.hms.service.serviceImpl;


import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.service.constants.Constants;
import com.hms.service.entity.ActivityFeedEntity;
import com.hms.service.entity.CandidateInfoEntity;
import com.hms.service.entity.PreOnBoardingEntity;
import com.hms.service.repository.ActivityFeedRepository;
import com.hms.service.repository.CandidateCreationRepository;
import com.hms.service.repository.PreOnBoardingRepository;
import com.hms.service.request.DashboardRequest;
import com.hms.service.request.PreOnBoardingRequest;
import com.hms.service.response.DashboardResponse;
import com.hms.service.response.DateCalculationResponse;
import com.hms.service.response.PreOnBoardingResponse;
import com.hms.service.service.IPreOnBoardingService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.jsonwebtoken.io.IOException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

//import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
@Slf4j
public class PreOnBoardingServiceImpl implements IPreOnBoardingService {

	@Autowired
	private PreOnBoardingRepository preOnBoardingRepository;

	@Autowired
	private CandidateCreationRepository candidateRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MinioClient minioClient;

//	@Value("${cloud.aws.credentials.secret-key}")
//	private String secretKey;
//
//	@Value("${cloud.aws.credentials.access-key}")
//	private String accessKey;
//
//	@Value("${cloud.aws.region.static}")
//	private String region;

//	@Autowired
//	private S3Presigner s3Presigner;


	@Autowired
	private ActivityFeedRepository activityFeedRepository;
	
	@Override
	public ApiResponse<?> addPreOnBoarding(Map<String, MultipartFile> files, String data) {
 
	    log.info("PreOnBoardingServiceImpl::Inside the addPreOnBoarding method");
	    PreOnBoardingRequest request = null;
 
	    try {
	        request = objectMapper.readValue(data, PreOnBoardingRequest.class);
 
	    } catch (Exception e) {
	        log.error("PreOnBoardingServiceImpl::error occured at addPreOnBoarding " + e.getMessage());
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.EXCEPTION_OCCURED,
	                List.of(e.getMessage())
	        );
	    }
 
	    PreOnBoardingEntity preOnBoardingEntity = new PreOnBoardingEntity();
 
	    Optional<CandidateInfoEntity> candidateInfoEntity =
	            candidateRepository.findById(request.getCandidateId());
 
	    if (candidateInfoEntity.isEmpty()) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.CANDIDATE_NOT_FOUND,
	                List.of("Candidate not found")
	        );
	    }
 
	    CandidateInfoEntity candidateEntity = candidateInfoEntity.get();
 
	    if (!Constants.ACCEPTED.equalsIgnoreCase(candidateEntity.getStatus())) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.PRE_ONBOARDING_ALLOWED_ONLY_FOR_APPROVED_CANDIDATES,
	                List.of("Candidate is not in accepted state")
	        );
	    }
 
	    Optional<PreOnBoardingEntity> preOnboarding =
	            preOnBoardingRepository.findByCandidateId_Id(request.getCandidateId());
 
	    if (!preOnboarding.isEmpty()) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.CANDIDATE_ALREADY_PREONBOARDED,
	                List.of("Candidate already pre-onboarded")
	        );
	    }
	    preOnBoardingEntity.setCandidateId(candidateEntity);
	    preOnBoardingEntity.setFirstName(request.getFirstName());
	    preOnBoardingEntity.setLastName(request.getLastName());
	    preOnBoardingEntity.setMiddleName(request.getMiddleName());
	    preOnBoardingEntity.setGender(request.getGender());
	    preOnBoardingEntity.setDateOfBirth(request.getDateOfBirth());
	    preOnBoardingEntity.setNationality(request.getNationality());
	    preOnBoardingEntity.setAadharNumber(request.getAadharNumber());
 
	    preOnBoardingEntity.setCity(request.getCity());
	    preOnBoardingEntity.setAddress1(request.getAddress1());
	    preOnBoardingEntity.setState(request.getState());
	    preOnBoardingEntity.setPincode(request.getPincode());
	    preOnBoardingEntity.setCountry(request.getCountry());
 
	    preOnBoardingEntity.setHighestEducationQualification(request.getHighestEducationQualification());
	    preOnBoardingEntity.setCgpa(request.getCgpa());
	    preOnBoardingEntity.setYear(request.getYear());
	    preOnBoardingEntity.setIsFresher(request.getIsFresher());
	    preOnBoardingEntity.setPhoneNumber(request.getPhoneNumber());
	    preOnBoardingEntity.setEmail(request.getEmail());
 
	    preOnBoardingEntity.setPersonalInfo(request.getPersonalInfo());
	    preOnBoardingEntity.setAddressInfo(request.getAddressInfo());
	    preOnBoardingEntity.setCreatedDate(LocalDateTime.now(ZoneId.of(Constants.REGION)));
 
	    preOnBoardingEntity.setRemarks(request.getRemarks());
	    preOnBoardingEntity.setExperience(request.getExperience());
 
	    candidateEntity.setStatus(Constants.ONBOARDING);
	    candidateRepository.save(candidateEntity);
 
	    if (!request.getIsFresher()) {
	        try {
	            List<Map<String, String>> experienceList = new ArrayList<>();
	            ObjectMapper objectMapper = new ObjectMapper();
 
	            List<String> organizationNames = request.getOrganizationNames();
 
	            if (organizationNames != null && !organizationNames.isEmpty()) {
 
	                for (String orgName : organizationNames) {
 
	                    Map<String, String> experienceMap = new HashMap<>();
	                    experienceMap.put(Constants.ORGANISATION_NAME, orgName);
 
	                    String fileKey = Constants.RELIEVING_LETTER
	                            + orgName.replaceAll("\\s+", Constants.EMPTY_STRING);
 
	                    MultipartFile relievingFile = files.get(fileKey);
 
	                    if (relievingFile != null && !relievingFile.isEmpty()) {
	                        String minioKey = uploadToMinio(relievingFile, request);
	                        experienceMap.put(Constants.RELIEVING_LETTER, minioKey);
	                    }
 
	                    String offerFileKey = Constants.OFFER_LETTER
	                            + orgName.replaceAll("\\s+", Constants.EMPTY_STRING);
 
	                    MultipartFile offerFile = files.get(offerFileKey);
 
	                    if (offerFile != null && !offerFile.isEmpty()) {
	                        String minioKey = uploadToMinio(offerFile, request);
	                        experienceMap.put(Constants.OFFER_LETTER, minioKey);
	                    }
 
	                    experienceList.add(experienceMap);
	                }
 
	                String experienceJson = objectMapper.writeValueAsString(experienceList);
	                preOnBoardingEntity.setOrganizationDetails(experienceJson);
	            }
 
	        } catch (JsonProcessingException e) {
	            log.info("exception in addPreOnBoarding " + e.getMessage());
	            throw new RuntimeException(Constants.FAILED_TO_PROCESS_EXPERIENCED_DETAILS);
	        }
	    }
 
	    MultipartFile payslipFile = files.get(Constants.PAY_SLIP);
	    if (payslipFile != null && !payslipFile.isEmpty()) {
	        try {
	            String payslipKey = uploadToMinio(payslipFile, request);
	            preOnBoardingEntity.setPaySlips(payslipKey);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
 
	    Map<String, Consumer<String>> fieldSetters = Map.of(
	            Constants.AADHAR, preOnBoardingEntity::setAadharPhoto,
	            Constants.EDUCATION, preOnBoardingEntity::setEducationDocument
	    );
 
	    for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
	        String key = entry.getKey().toLowerCase();
	        MultipartFile file = entry.getValue();
 
	        try {
	            String originalFileName = file.getOriginalFilename();
 
	            if (originalFileName == null || !originalFileName.contains(".")) continue;
 
	            fieldSetters.getOrDefault(key, k -> {}).accept(uploadToMinio(file, request));
 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
 
	    preOnBoardingRepository.save(preOnBoardingEntity);
 
	    ActivityFeedEntity activityFeedEntity = new ActivityFeedEntity();
	    activityFeedEntity.setTimeStamp(LocalDateTime.now());
	    activityFeedEntity.setActivity(
	            "" + Constants.PREONBOARDING_INITIATED_FOR +
	                    request.getFirstName() + " " + request.getLastName()
	    );
	    activityFeedRepository.save(activityFeedEntity);
 
	    log.info("CandidateServiceImpl :: Data Stored in the Activity Feed");
 
	    log.info("PreOnBoardingServiceImpl::Exit from the addPreOnBoarding method");
 
	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            "success",
	            Constants.CANDIDATE_PRE_ONBOARDED_SUCCESSFULLY
	            
	    );
	}
 
 
 
 

//	private String uploadToS3(MultipartFile file, PreOnBoardingRequest request) throws IOException {
//		log.info("PreOnBoardingServiceImpl::Inside the uploadtoS3 method");
//		String originalFileName = file.getOriginalFilename();
//
//		String fileKey = Constants.BUCKET_FOLDER + request.getCandidateId() + Constants.UNDER_SCORE
//				+ request.getFirstName() + Constants.UNDER_SCORE + originalFileName;
//
//		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(Constants.BUCKET).key(fileKey)
//				.contentType(file.getContentType()).build();
//
//		try {
//			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//			log.info("PreOnBoardingServiceImpl::Sucessfully uploaded to the s3");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		log.info("PreOnBoardingServiceImpl::Exit from the uploadtoS3 method");
//		return fileKey;
//	}

	private String uploadToMinio(MultipartFile file, PreOnBoardingRequest request) throws IOException {

		log.info("PreOnBoardingServiceImpl::Inside uploadToMinio method");

		String originalFileName = file.getOriginalFilename();

		String fileKey = Constants.BUCKET_FOLDER + request.getCandidateId() + Constants.UNDER_SCORE
				+ request.getFirstName() + Constants.UNDER_SCORE + originalFileName;

		try {

			minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKET).object(fileKey)
					.stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());

			log.info("PreOnBoardingServiceImpl::Successfully uploaded to MinIO");

		} catch (Exception e) {
			log.error("Error occurred while uploading to MinIO", e);
			e.printStackTrace();
		}

		log.info("PreOnBoardingServiceImpl::Exit from uploadToMinio method");

		return fileKey;
	}

//	private void deleteFromS3(String Key) {
//		log.info("PreOnBoardingServiceImpl:Inside the deleteFromS3 method");
//		DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(Constants.BUCKET).key(Key).build();
//		s3Client.deleteObject(request);
//		log.info("PreOnBoardingServiceImpl:Exit from the deleteFromS3 method");
//
//	}

	private void deleteFromMinio(String key) {

		log.info("PreOnBoardingServiceImpl: Inside deleteFromMinio method");

		try {

			minioClient.removeObject(RemoveObjectArgs.builder().bucket(Constants.BUCKET).object(key).build());
			log.info("PreOnBoardingServiceImpl: Successfully deleted from MinIO");

		} catch (Exception e) {
			throw new RuntimeException("Failed to delete file from MinIO", e);
		}
		log.info("PreOnBoardingServiceImpl: Exit from deleteFromMinio method");
	}

	@Override
	@Transactional
	public ApiResponse<?> deletePreOnBoardingCandidateById(Integer candidateId) {
 
	    log.info("PreOnBoardingServiceImpl:: Inside the deletePreOnBoardingCandidateById method");
 
	    Optional<PreOnBoardingEntity> preOnBoardingEntity =
	            preOnBoardingRepository.findByCandidateId_Id(candidateId);
 
	    if (preOnBoardingEntity.isEmpty()) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.CANDIDATE_NOT_FOUND,
	                List.of("Candidate not found")
	        );
	    }
 
	    PreOnBoardingEntity entity = preOnBoardingEntity.get();
 
	    try {
 
	        if (entity.getAadharPhoto() != null && !entity.getAadharPhoto().isEmpty()) {
	            deleteFromMinio(entity.getAadharPhoto());
	        }
 
//	      if (entity.getBankPhoto() != null && !entity.getBankPhoto().isEmpty()) {
//	          deleteFromMinio(entity.getBankPhoto());
//	      }
 
	        if (entity.getEducationDocument() != null && !entity.getEducationDocument().isEmpty()) {
	            deleteFromMinio(entity.getEducationDocument());
	        }
 
	        if (entity.getPaySlips() != null && !entity.getPaySlips().isEmpty()) {
	            deleteFromMinio(entity.getPaySlips());
	        }
 
	        if (entity.getOrganizationDetails() != null && !entity.getOrganizationDetails().isEmpty()) {
 
	            ObjectMapper mapper = new ObjectMapper();
 
	            List<Map<String, String>> experienceList =
	                    mapper.readValue(entity.getOrganizationDetails(), List.class);
 
	            for (Map<String, String> exp : experienceList) {
 
	                if (exp.get(Constants.RELIEVING_LETTER) != null &&
	                        !exp.get(Constants.RELIEVING_LETTER).isEmpty()) {
 
	                    deleteFromMinio(exp.get(Constants.RELIEVING_LETTER));
	                }
 
	                if (exp.get(Constants.OFFER_LETTER) != null &&
	                        !exp.get(Constants.OFFER_LETTER).isEmpty()) {
 
	                    deleteFromMinio(exp.get(Constants.OFFER_LETTER));
	                }
	            }
	        }
 
	        preOnBoardingRepository.deleteByCandidateId_Id(candidateId);
 
	        return ApiResponse.success(
	                ResponseCode.SUCCESS,
	                "success",
	                Constants.CANDIDATE_DELETED_SUCCESSFULLY
	                
	        );
 
	    } catch (Exception e) {
 
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.FAILED_TO_DELETE_PRE_ONBOARDING_DOCUMENTS,
	                List.of(e.getMessage())
	        );
	    }
	}
 
	@Override
	public ApiResponse<?> getAllPreOnBoardingList() {
 
	    log.info("PreOnBoardingServiceImpl:: Inside the getAllPreOnBoardingList method");
 
	    List<PreOnBoardingEntity> preOnBoardingEntity = preOnBoardingRepository
	            .findAll(Sort.by(Sort.Direction.DESC, Constants.CREATED_DATE));
 
	    List<PreOnBoardingResponse> preOnBoardingResponseList = preOnBoardingEntity.stream().map(entity -> {
 
	        PreOnBoardingResponse preOnBoardingResponse = new PreOnBoardingResponse();
	        BeanUtils.copyProperties(entity, preOnBoardingResponse);
 
	        preOnBoardingResponse.setCandidateId(entity.getCandidateId().getId());
 
	        try {
	            ObjectMapper objectMapper = new ObjectMapper();
 
	            if (entity.getOrganizationDetails() != null && !entity.getOrganizationDetails().isBlank()) {
 
	                List<Map<String, String>> orgList = objectMapper.readValue(
	                        entity.getOrganizationDetails(),
	                        new TypeReference<List<Map<String, String>>>() {}
	                );
 
	                preOnBoardingResponse.setOrganizationExperience(orgList);
	            }
 
	        } catch (Exception e) {
	            log.error(
	                    "PreOnBoardingServiceImpl::Error parsing organization details for candidateId {}: {}",
	                    entity.getCandidateId() != null ? entity.getCandidateId().getId() : null,
	                    e.getMessage()
	            );
	        }
 
	        return preOnBoardingResponse;
 
	    }).collect(Collectors.toList());
 
	    log.info("PreOnBoardingServiceImpl:: Exit from the getAllPreOnBoardingList method");
	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            preOnBoardingResponseList
	    );
	}
 

	@Override
	public ApiResponse<?> updatePreOnBoarding(Map<String, MultipartFile> files, String data) {
 
	    log.info("PreOnBoardingServiceImpl::Inside the updatePreOnBoarding method");
	    PreOnBoardingRequest request = null;
 
	    try {
	        request = objectMapper.readValue(data, PreOnBoardingRequest.class);
 
	    } catch (Exception e) {
	        e.printStackTrace();
 
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.EXCEPTION_OCCURED,
	                List.of(e.getMessage())
	        );
	    }
	    Optional<PreOnBoardingEntity> preOnBoardingEntity =
	            preOnBoardingRepository.findByCandidateId_Id(request.getCandidateId());
	    if (preOnBoardingEntity.isEmpty()) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.CANDIDATE_NOT_FOUND,
	                List.of("Candidate not found")
	        );
	    }
 
	    PreOnBoardingEntity entity = preOnBoardingEntity.get();
	    entity.getCandidateId().setId(request.getCandidateId());
	    entity.setFirstName(request.getFirstName());
	    entity.setLastName(request.getLastName());
	    entity.setMiddleName(request.getMiddleName());
	    entity.setGender(request.getGender());
	    entity.setDateOfBirth(request.getDateOfBirth());
	    entity.setNationality(request.getNationality());
	    entity.setAadharNumber(request.getAadharNumber());
 
	    entity.setCity(request.getCity());
	    entity.setAddress1(request.getAddress1());
	    entity.setState(request.getState());
	    entity.setPincode(request.getPincode());
	    entity.setCountry(request.getCountry());
 
	    entity.setHighestEducationQualification(request.getHighestEducationQualification());
	    entity.setCgpa(request.getCgpa());
	    entity.setYear(request.getYear());
	    entity.setIsFresher(request.getIsFresher());
	    entity.setPhoneNumber(request.getPhoneNumber());
	    entity.setEmail(request.getEmail());
 
	    entity.setRemarks(request.getRemarks());
	    entity.setExperience(request.getExperience());
 
	    if (!request.getIsFresher()) {
 
	        try {
	            List<Map<String, String>> experienceList = new ArrayList<>();
	            ObjectMapper objectMapper = new ObjectMapper();
 
	            List<String> organizationNames = request.getOrganizationNames();
	            if (organizationNames != null && !organizationNames.isEmpty()) {
 
	                for (String orgName : organizationNames) {
	                    Map<String, String> experienceMap = new HashMap<>();
	                    experienceMap.put(Constants.ORGANISATION_NAME, orgName);
	                    String fileKey = Constants.RELIEVING_LETTER
	                            + orgName.replaceAll("\\s+", Constants.EMPTY_STRING);
	                    MultipartFile relievingFile = files.get(fileKey);
	                    if (relievingFile != null && !relievingFile.isEmpty()) {
	                        String minioKey = uploadToMinio(relievingFile, request);
	                        experienceMap.put(Constants.RELIEVING_LETTER, minioKey);
	                    }
	                    String offerFileKey = Constants.OFFER_LETTER
	                            + orgName.replaceAll("\\s+", Constants.EMPTY_STRING);
	                    MultipartFile offerFile = files.get(offerFileKey);
	                    if (offerFile != null && !offerFile.isEmpty()) {
	                        String minioKey = uploadToMinio(offerFile, request);
	                        experienceMap.put(Constants.OFFER_LETTER, minioKey);
	                    }
	                    experienceList.add(experienceMap);
	                }
	                String experienceJson = objectMapper.writeValueAsString(experienceList);
	                entity.setOrganizationDetails(experienceJson);
	            }
	        } catch (JsonProcessingException e) {
	            throw new RuntimeException(Constants.FAILED_TO_PROCESS_EXPERIENCED_DETAILS);
	        }
	    }
	    MultipartFile payslipFile = files.get(Constants.PAY_SLIP);
	    if (payslipFile != null && !payslipFile.isEmpty()) {
	        try {
	            String payslipKey = uploadToMinio(payslipFile, request);
	            entity.setPaySlips(payslipKey);
 
	        } catch (Exception e) {
	            throw new RuntimeException(Constants.FAILED_TO_UPLOAD_PAYSLIPS);
	        }
	    }
 
	    Map<String, Consumer<String>> fieldSetters = Map.of(
	            Constants.AADHAR, entity::setAadharPhoto,
	            Constants.EDUCATION, entity::setEducationDocument
	    );
 
	    for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
 
	        String key = entry.getKey().toLowerCase();
	        MultipartFile file = entry.getValue();
 
	        try {
	            String originalFileName = file.getOriginalFilename();
 
	            if (originalFileName == null || !originalFileName.contains(".")) continue;
 
	            fieldSetters.getOrDefault(key, k -> {})
	                    .accept(uploadToMinio(file, request));
 
	        } catch (Exception e) {
	            throw new RuntimeException(Constants.FAILED_TO_UPLOAD_DOCUMENTS);
	        }
	    }
	    preOnBoardingRepository.save(entity);
	    log.info("PreOnBoardingServiceImpl:Exit from the updatePreOnBoarding method");
	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            "success",
	            Constants.CANDIDATE_UPDATED_SUCCESSFULLY
	            
	    );
	}
 
	@Override
	public ApiResponse<?> getOnboardingDashboardDetails(DashboardRequest request) {
 
	    log.info("PreOnBoardingServiceImpl:: Inside the getOnboardingDashboardDetails method");
 
	    DateCalculationResponse dateCalculationResponse = dateCalulcation(request);
	    DashboardResponse dashboardResponse = new DashboardResponse();
	    dashboardResponse.setOnBoarding(count(request, Constants.ONBOARDING));
	    dashboardResponse.setOffer_sent(count(request, Constants.OFFER_SENT));
	    dashboardResponse.setOffer_rejected(count(request, Constants.OFFER_REJECTED));
	    dashboardResponse.setBGV_initated(count(request, Constants.BGV_INITIATED));
	    dashboardResponse.setBGV_Cleared(count(request, Constants.BGV_CLEARED));
	    dashboardResponse.setBGV_rejected(count(request, Constants.BGV_REJECTED));
	    dashboardResponse.setAccepted(count(request, Constants.ACCEPTED));
	    dashboardResponse.setJoined(count(request, Constants.JOINED));
 
	    dashboardResponse.setTotalCandidates(
	            candidateRepository.totalCandidates(
	                    dateCalculationResponse.getFrom(),
	                    dateCalculationResponse.getTo()
	            )
	    );
 
	    dashboardResponse.setReportMap(getDailyReport(request));
 
	    log.info("PreOnBoardingServiceImpl:: Exit from the getOnboardingDashboardDetails method ");
	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            dashboardResponse
	    );
	}
 
	public DateCalculationResponse dateCalulcation(DashboardRequest request) {
		log.info("PreOnBoardingServiceImpl::Inside the dateCalulcation method");
		LocalDateTime from;
		LocalDateTime to = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
		DateCalculationResponse dateCalculationResponse = new DateCalculationResponse();
		if (request.getMonthCode().equals(Constants.ZERO_FILTER)) {
			from = LocalDate.now(ZoneId.of("Asia/Kolkata")).atStartOfDay();
		} else if (request.getMonthCode().equals(Constants.FIRST_MONTH)) {
			from = to.minusMonths(1);
		} else if (request.getMonthCode().equals(Constants.SECOND_MONTH)) {
			from = to.minusMonths(2);
		} else if (request.getMonthCode().equals(Constants.THIRD_MONTH)) {
			from = to.minusMonths(3);
		} else if (request.getMonthCode().equals(Constants.WEEK_FILTERING)) {
			from = to.minusWeeks(1);
		} else if (request.getMonthCode().equals(Constants.CUSTOM_FILTERING)) {
			from = request.getFromDate();
			to = request.getToDate();
		} else {
			throw new IllegalArgumentException();
		}
		dateCalculationResponse.setFrom(from);
		dateCalculationResponse.setTo(to);
		log.info("PreOnBoardingServiceImpl::Exit from the dateCalulcation method ");
		return dateCalculationResponse;
	}
 
	public int count(DashboardRequest request, String status) {
		log.info("PreOnBoardingServiceImpl::Inside the count method");
		DateCalculationResponse dateCalculationResponse = new DateCalculationResponse();
		dateCalculationResponse = dateCalulcation(request);
		LocalDateTime from = dateCalculationResponse.getFrom();
		LocalDateTime to = dateCalculationResponse.getTo();
		long count = preOnBoardingRepository.countByStatusAndDate(status, from, to);
		log.info("PreOnBoardingServiceImpl::Exit from the count method");
		return (int) count;
	}
 
	public Map<String, Long> getDailyReport(DashboardRequest request) {
		log.info("PreOnBoardingServiceImpl:: Inside the getDailyReport method");
		DateCalculationResponse dateCalculationResponse = new DateCalculationResponse();
		Map<String, Long> reportMap = new LinkedHashMap<>();
		dateCalculationResponse = dateCalulcation(request);
		LocalDate from = dateCalculationResponse.getFrom().toLocalDate();
		LocalDate to = dateCalculationResponse.getTo().toLocalDate();
 
		while (!from.isAfter(to)) {
 
			LocalDateTime startOfDay = from.atStartOfDay();
			LocalDateTime endOfDay = from.atTime(23, 59, 59, 999_999_999);
			Long dayCount = candidateRepository.totalCandidates(startOfDay, endOfDay);
			reportMap.put(from.toString(), dayCount);
			from = from.plusDays(1);
		}
		log.info("PreOnBoardingServiceImpl:: Exit from the getDailyReport method");
		return reportMap;
	}
 
	@Override
	public ResponseEntity<byte[]> viewDocument(String key) {
		log.info("PreOnBoardingServiceImpl:: Inside the viewDocument method");

		byte[] fileBytes = downloadFile(key);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(detectMediaType(key));
		headers.setContentDisposition(ContentDisposition.inline().filename(key).build());
		log.info("PreOnBoardingServiceImpl:: Exit from the viewDocument method");

		return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
	}

	private byte[] downloadFile(String key) {

		log.info("PreOnBoardingServiceImpl::Inside downloadFile method");

		String fullKey = Constants.BUCKET_FOLDER + key;

		try (InputStream minioObject = minioClient
				.getObject(GetObjectArgs.builder().bucket(Constants.BUCKET).object(fullKey).build())) {

			log.info("PreOnBoardingServiceImpl::Exit from downloadFile method");

			return minioObject.readAllBytes();

		} catch (Exception e) {
			throw new RuntimeException(Constants.FAILED_TO_DOWNLOAD_FILE_FROM_MINIO, e);
		}
	}

//	private byte[] downloadFile(String key) {
//		log.info("PreOnBoardingServiceImpl::Inside the downloadFile method");
//		String fullKey = Constants.BUCKET_FOLDER + key;
//
//		try (ResponseInputStream<GetObjectResponse> s3Object = s3Client
//				.getObject(GetObjectRequest.builder().bucket(Constants.BUCKET).key(fullKey).build())) {
//			log.info("PreOnBoardingServiceImpl::Exit from the downloadFile method");
//			return s3Object.readAllBytes();
//
//		} catch (Exception e) {
//			throw new RuntimeException(Constants.FAILED_TO_DOWNLOAD_FILE_FROM_S3, e);
//
//		}
//
//	}
	private MediaType detectMediaType(String fileName) {
		log.info("PreOnBoardingServiceImpl::Inside the detectMediaType method");

		if (fileName == null) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}

		if (fileName.endsWith(Constants.PDF)) {
			return MediaType.APPLICATION_PDF;
		}
		if (fileName.endsWith(Constants.PNG)) {
			return MediaType.IMAGE_PNG;
		}
		if (fileName.endsWith(Constants.JPG) || fileName.endsWith(Constants.JPEG)) {
			return MediaType.IMAGE_JPEG;
		}
		log.info("PreOnBoardingServiceImpl:: Exit from the detectMediaType method");
		return MediaType.APPLICATION_OCTET_STREAM;
	}


	@Override
	@Transactional
	public ApiResponse<?> getPreOnBoardingCandidateDetailsById(Integer id) {
		
	    log.info("PreOnBoardingServiceImpl::Inside getPreOnBoardingCandidateDetailsById method");
	    PreOnBoardingResponse preOnBoardingResponse = new PreOnBoardingResponse();
	    Optional<PreOnBoardingEntity> preOnBoardingEntity = preOnBoardingRepository.findById(id);
	    PreOnBoardingEntity entity = preOnBoardingEntity.get();
 
	    BeanUtils.copyProperties(entity, preOnBoardingResponse);
	    preOnBoardingResponse.setCandidateId(entity.getCandidateId().getId());
 
	    try {
	        ObjectMapper objectMapper = new ObjectMapper();
 
	        if (entity.getOrganizationDetails() != null && !entity.getOrganizationDetails().isBlank()) {
 
	            List<Map<String, String>> orgList = objectMapper.readValue(
	                    entity.getOrganizationDetails(),
	                    new TypeReference<List<Map<String, String>>>() {}
	            );
 
	            preOnBoardingResponse.setOrganizationExperience(orgList);
	        }
 
	    } catch (Exception e) {
	        log.error(
	                "PreOnBoardingServiceImpl::Error parsing organization details for candidateId {}: {}",
	                entity.getCandidateId() != null ? entity.getCandidateId().getId() : null,
	                e.getMessage()
	        );
	    }
 
	    log.info("PreOnBoardingServiceImpl::Exit from getPreOnBoardingCandidateDetailsById method");
	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            preOnBoardingResponse
	    );
	}
 

}
	

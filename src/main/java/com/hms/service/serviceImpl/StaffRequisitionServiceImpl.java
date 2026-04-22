package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.dto.StaffingRequisitionResponseDto;
import com.hms.service.entity.ApprovalEntity;
import com.hms.service.entity.BudgetAndCompensationEntity;
import com.hms.service.entity.BusinessJustificationEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.RolesAndRequirementsEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.entity.SourcingStrategyEntity;
import com.hms.service.entity.StaffingRequisitionEntitys;
import com.hms.service.repository.ApprovalRepository;
import com.hms.service.repository.BudgetAndCompensationRepository;
import com.hms.service.repository.BusinessJustificationRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RolesAndRequirementsRepository;
import com.hms.service.repository.SourceStrategyRepository;
import com.hms.service.repository.Staffing;
import com.hms.service.repository.StaffingRequisitionRepository;
import com.hms.service.request.BudgetAndCompensationRequest;
import com.hms.service.request.BusinessJustificationRequest;
import com.hms.service.request.PositonBascicsRequest;
import com.hms.service.request.RolesAndRequirementsRequest;
import com.hms.service.request.SRFilterRequest;
import com.hms.service.request.SourcingStrategyRequest;
import com.hms.service.request.StaffingRequisitionRequest;
import com.hms.service.response.BudgetAndCompensationResponse;
import com.hms.service.response.BusinessJustificationResponse;
import com.hms.service.response.PositonBasicsResponse;
import com.hms.service.response.RolesAndRequirementsResponse;
import com.hms.service.response.SourcingStrategyResponse;
import com.hms.service.response.StaffingRequisitionResponse;
import com.hms.service.service.IStaffingRequisitionService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StaffRequisitionServiceImpl implements IStaffingRequisitionService {

	@Autowired
	private StaffingRequisitionRepository staffingRequisitionRepository;

	@Autowired
	private Staffing staffing;

	@Autowired
	private MinioClient minioClient;
	
	@Autowired
	private PositionBasicsRepository positionBasicsRepository;

	@Autowired
	private BusinessJustificationRepository businessJustificationRepository;

	@Autowired
	private BudgetAndCompensationRepository budgetAndCompensationRepository;

	@Autowired
	private RolesAndRequirementsRepository rolesAndRequirementsRepository;

	@Autowired
	private SourceStrategyRepository sourceStrategyRepository;
	
	@Autowired
	private DepartmentsRepository departmentsRepository;
	
	@Autowired
	private ApprovalRepository approvalRepository;

	
	@Override
	public ApiResponse<?> newStaffingRequisition(StaffingRequisitionRequest request, MultipartFile file) {

	    String srId = null;
	if (request.getPositonBascicsRequest() != null) {

	    PositonBascicsRequest positonBasicsRequest = request.getPositonBascicsRequest();
	    ApiResponse<?> error = validatePositonBasicsRequest(positonBasicsRequest);
	    if (error != null) return error;

	    SRPositionBasicsEntity entity = null;
	    if (positonBasicsRequest.getId() != null) {
	        entity = staffingRequisitionRepository
	                .findById(positonBasicsRequest.getId())
	                .orElse(null);
	    }

	    if (entity == null) {
	        entity = new SRPositionBasicsEntity();
	        entity.setDraft(true);
	        entity.setSubmitted(false);
	        entity.setApproved(false);

	        entity.setCreatedOn(LocalDate.now());
	        entity.setCreatedBy("SYSTEM"); 
	    }
	    entity.setJobTitle(positonBasicsRequest.getJobTitle());
	    entity.setBusinessUnitId(positonBasicsRequest.getBusinessUnitId());
	    entity.setDepartmentId(positonBasicsRequest.getDepartmentId());
	    entity.setReportingManagerInfo(positonBasicsRequest.getReportingManagerInfo());
	    entity.setLocation(positonBasicsRequest.getLocation());
	    entity.setSeniorityLevel(positonBasicsRequest.getSeniorityLevel());
	    entity.setOpenings(positonBasicsRequest.getOpenings());
	    entity.setTargetStartDate(positonBasicsRequest.getTargetStartDate());
	    entity.setEmploymentType(positonBasicsRequest.getEmploymentType());
	    entity.setWorkMode(positonBasicsRequest.getWorkMode());
	    entity.setPriority(positonBasicsRequest.getPriority());
	    staffingRequisitionRepository.save(entity);

	    if (entity.getSrId() == null) {
	        srId = generateSrId(entity);
	        entity.setSrId(srId);
	        staffingRequisitionRepository.save(entity);
	    }
	}

	    if (request.getSourcingStrategyRequest() != null) {

	        SourcingStrategyRequest req = request.getSourcingStrategyRequest();
	        ApiResponse<?> error = validateSourcingStrategyRequest(req);
		    if (error != null) return error;
		    
	        if (srId == null || srId.isBlank()) {
	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "srId is required",
	                    List.of("srId is required")
	            );
	        }
	        SourcingStrategyEntity entity =
	                sourceStrategyRepository.findBySrId(srId).orElse(null);

	        if (entity == null) {
	            entity = new SourcingStrategyEntity();

	            entity.setSrId(srId);
	            entity.setDraft(true);
	            entity.setSubmitted(false);
	            entity.setApproved(false);
	        }

	        entity.setInternalBoard(req.getInternalBoard());
	        entity.setNaukri(req.getNaukri());
	        entity.setLinkedIn(req.getLinkedIn());
	        entity.setIndeed(req.getIndeed());
	        entity.setCompanySite(req.getCompanySite());
	        entity.setAgencyRpo(req.getAgencyRpo());
	        entity.setInternalFirstPolicy(req.getInternalFirstPolicy());
	        entity.setSourcingBudget(req.getSourcingBudget());
	        entity.setReferralEnabled(req.getReferralEnabled());
	        entity.setReferralAmount(req.getReferralAmount());
	        entity.setDiversityEnabled(req.getDiversityEnabled());
	        entity.setDiversityTags(req.getDiversityTags());

	        sourceStrategyRepository.save(entity);
	    }
	    return ApiResponse.success(
	            ResponseCode.SUCCESS,
	            "Staffing Requisition created successfully",
	            srId
	    );
	}
	
	@Transactional
	public ApiResponse<?> submitForApproval(String srId) {

	    if (srId == null || srId.isBlank()) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "srId is required",
	                List.of("srId cannot be null or empty")
	        );
	    }
	    try {
	        positionBasicsRepository.findBySrId(srId).ifPresent(entity -> {
	            entity.setDraft(false);
	            entity.setSubmitted(true);
	            positionBasicsRepository.save(entity);
	        });

	        businessJustificationRepository.findBySrId(srId).ifPresent(entity -> {
	            entity.setDraft(false);
	            entity.setSubmitted(true);
	            businessJustificationRepository.save(entity);
	        });

	        budgetAndCompensationRepository.findBySrId(srId).ifPresent(entity -> {
	            entity.setDraft(false);
	            entity.setSubmitted(true);
	            budgetAndCompensationRepository.save(entity);
	        });
	        rolesAndRequirementsRepository.findBySrId(srId).ifPresent(entity -> {
	            entity.setDraft(false);
	            entity.setSubmitted(true);
	            rolesAndRequirementsRepository.save(entity);
	        });
	        sourceStrategyRepository.findBySrId(srId).ifPresent(entity -> {
	            entity.setDraft(false);
	            entity.setSubmitted(true);
	            sourceStrategyRepository.save(entity);
	        });
	        ApprovalEntity approvalEntity =
	                approvalRepository.findBySrId(srId).orElse(null);
	        return ApiResponse.success(
	                ResponseCode.SUCCESS,
	                "SR submitted for approval successfully",
	                srId
	        );

	    } catch (Exception e) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "Failed to submit SR",
	                List.of(e.getMessage())
	        );
	    }
	}
	
		
	private ApiResponse<?> validateObject(String value, String fieldName) {
		if (value == null || value.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " is required",
					List.of(fieldName + " is required"));
		}
		
		  if (value instanceof String str && str.trim().isEmpty()) {
		        return ApiResponse.failure(
		                ResponseCode.FAILURE,
		                fieldName + " is required",
		                List.of(fieldName + " is required")
		        );
		  }
		    
		return null;
	}
	
	private ApiResponse<?> validateObject(Object value, String fieldName) {
	    if (value instanceof Number) {
	        Number num = (Number) value;
	        if (num.doubleValue() <= 0) {
	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    fieldName + " must be greater than 0",
	                    List.of(fieldName + " must be positive")
	            );
	        }
	    }

	    return null;
	}

	
	
	private void uploadToMinio(MultipartFile offerLetter, String fileKey) throws Exception {

		log.info("staffingRequisitonServiceImpl::Inside uploadToMinio method");

		minioClient.putObject(PutObjectArgs.builder().bucket(Constants.BUCKET).object(fileKey)
				.stream(offerLetter.getInputStream(), offerLetter.getSize(), -1)
				.contentType(offerLetter.getContentType()).build());

		log.info("staffingRequisitionServiceImpl::Exit from uploadToMinio method");
	}

	public ApiResponse<?> validatePositonBasicsRequest(PositonBascicsRequest req) {
	    ApiResponse<?> error;
	    error = validateObject(req.getJobTitle(), "jobTitle");
	    if (error != null) return error;
	    error = validateObject(String.valueOf(req.getDepartmentId()), "departmentId");
	    if (error != null) return error;
	    if (req.getReportingManagerInfo() != null) {
	        for (int i = 0; i < req.getReportingManagerInfo().size(); i++) {
	            error = validateObject(String.valueOf(req.getReportingManagerInfo().get(i)), "reportingManagerInfo");
	            if (error != null) return error;
	        }
	    }
	    error = validateObject(req.getLocation(), "location");
	    if (error != null) return error;
	    error = validateObject(req.getSeniorityLevel(), "seniorityLevel");
	    if (error != null) return error;
	    error = validateObject(String.valueOf(req.getOpenings()), "openings");
	    if (error != null) return error;
	    error = validateObject(String.valueOf(req.getTargetStartDate()), "targetStartDate");
	    if (error != null) return error;
	    error = validateObject(req.getEmploymentType(), "employmentType");
	    if (error != null) return error;
	    error = validateObject(req.getWorkMode(), "workMode");
	    if (error != null) return error;
	    error = validateObject(req.getPriority(), "priority");
	    if (error != null) return error;
	    return null; 
	}

	public ApiResponse<?> validateSourcingStrategyRequest(SourcingStrategyRequest req) {

	    ApiResponse<?> error;

	    error = validateObject(req.getInternalFirstPolicy(), "internalFirstPolicy");
	    if (error != null) return error;
	    boolean hasJobBoard =
	            Boolean.TRUE.equals(req.getInternalBoard()) ||
	            Boolean.TRUE.equals(req.getLinkedIn()) ||
	            Boolean.TRUE.equals(req.getNaukri()) ||
	            Boolean.TRUE.equals(req.getIndeed()) ||
	            Boolean.TRUE.equals(req.getCompanySite());

	    if (!hasJobBoard) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "targetJobBoard is required",
	                List.of("At least one job board must be selected")
	        );
	    }
	    if (Boolean.TRUE.equals(req.getReferralEnabled())) {
	        error = validateObject(req.getReferralAmount(), "referralAmount");
	        if (error != null) return error;

	        if (req.getReferralAmount() != null && req.getReferralAmount() <= 0) {
	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "referralAmount must be greater than 0",
	                    List.of("Referral amount must be positive")
	            );
	        }
	    }
	    if (Boolean.TRUE.equals(req.getDiversityEnabled())) {
	        error = validateObject(req.getDiversityTags(), "diversityTags");
	        if (error != null) return error;
	    }
	    return null;
	}
	
		
	@Override
	@Transactional
	public ApiResponse<?> getBySrId(String srId) {

	    log.info("StaffRequisitionsServiceImpl : Inside getBySrId method");

	    try {

	        if (srId == null || srId.isBlank()) {
	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    Constants.SR_ID_IS_REQUIRED,
	                    List.of(Constants.SR_ID_CANNOT_BE_NULL_OR_EMPTY));
	        }

	        SRPositionBasicsEntity posEntity = positionBasicsRepository.findBySrId(srId).orElse(null);
	        BusinessJustificationEntity bjEntity = businessJustificationRepository.findBySrId(srId).orElse(null);
	        BudgetAndCompensationEntity budgetEntity = budgetAndCompensationRepository.findBySrId(srId).orElse(null);
	        RolesAndRequirementsEntity roleEntity = rolesAndRequirementsRepository.findBySrId(srId).orElse(null);
	        SourcingStrategyEntity sourcingEntity = sourceStrategyRepository.findBySrId(srId).orElse(null);

	        if (posEntity == null && bjEntity == null && budgetEntity == null &&
	                roleEntity == null && sourcingEntity == null) {

	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    Constants.NO_DATA_FOUND,
	                    List.of(Constants.INVALID_SR_ID_IS + srId)
	            );
	        }

	        StaffingRequisitionResponseDto response = new StaffingRequisitionResponseDto();
	        if (posEntity != null) {

	            PositonBasicsResponse posRes = new PositonBasicsResponse();

	            posRes.setId(posEntity.getId());
	            posRes.setSrId(posEntity.getSrId());
	            posRes.setJobTitle(posEntity.getJobTitle());
	            posRes.setBusinessUnitId(posEntity.getBusinessUnitId());
	            posRes.setDepartmentId(posEntity.getDepartmentId());
	            posRes.setReportingManagerInfo(posEntity.getReportingManagerInfo());
	            posRes.setLocation(posEntity.getLocation());
	            posRes.setSeniorityLevel(posEntity.getSeniorityLevel());
	            posRes.setOpenings(posEntity.getOpenings());
	            posRes.setTargetStartDate(posEntity.getTargetStartDate());
	            posRes.setWorkMode(posEntity.getWorkMode());
	            posRes.setEmploymentType(posEntity.getEmploymentType());
	            posRes.setPriority(posEntity.getPriority());
	            posRes.setDraft(posEntity.getDraft());
	            posRes.setSubmitted(posEntity.getSubmitted());
	            posRes.setApproved(posEntity.getApproved());
	            posRes.setCreatedOn(posEntity.getCreatedOn());
	            posRes.setCreatedBy(posEntity.getCreatedBy());

	            response.setPositonBasicsResponse(posRes);
	        }
	        if (bjEntity != null) {

	            BusinessJustificationResponse bjRes = new BusinessJustificationResponse();

	            bjRes.setId(bjEntity.getId());
	            bjRes.setSrId(bjEntity.getSrId());
	            bjRes.setRequisitionType(bjEntity.getRequisitionType());
	            bjRes.setBusinessCase(bjEntity.getBusinessCase());
	            bjRes.setImpactIfNotFilled(bjEntity.getImpactIfNotFilled());
	            bjRes.setReplacesEmployee(bjEntity.getReplacesEmployee());
	            bjRes.setDocument(bjEntity.getDocument());
	            bjRes.setDraft(bjEntity.getDraft());
	            bjRes.setSubmitted(bjEntity.getSubmitted());
	            bjRes.setApproved(bjEntity.getApproved());

	            response.setBusinessJustificationResponse(bjRes);
	        }
	        if (budgetEntity != null) {

	            BudgetAndCompensationResponse budRes = new BudgetAndCompensationResponse();

	            budRes.setId(budgetEntity.getId());
	            budRes.setSrId(budgetEntity.getSrId());
	            budRes.setProposedTotalCompensation(budgetEntity.getProposedTotalCompensation());
	            budRes.setSigningBonus(budgetEntity.getSigningBonus());
	            budRes.setEquity(budgetEntity.getEquity());
	            budRes.setRelocationBudget(budgetEntity.getRelocationBudget());
	            budRes.setSigningBonusAmount(budgetEntity.getSigningBonusAmount());
	            budRes.setEquityAmount(budgetEntity.getEquityAmount());
	            budRes.setRelocationBudgetAmount(budgetEntity.getRelocationBudgetAmount());
	            budRes.setAnnualHiringCost(budgetEntity.getAnnualHiringCost());
	            budRes.setDraft(budgetEntity.getDraft());
	            budRes.setSubmitted(budgetEntity.getSubmitted());
	            budRes.setApproved(budgetEntity.getApproved());

	            response.setBudgetAndCompensationResponse(budRes);
	        }
	        if (roleEntity != null) {

	            RolesAndRequirementsResponse roleRes = new RolesAndRequirementsResponse();

	            roleRes.setId(roleEntity.getId());
	            roleRes.setSrId(roleEntity.getSrId());
	            roleRes.setSkillsMustHave(roleEntity.getSkillsMustHave());
	            roleRes.setNiceToHaveSkills(roleEntity.getNiceToHaveSkills());
	            roleRes.setEducationRequirement(roleEntity.getEducationRequirement());
	            roleRes.setTravelRequirement(roleEntity.getTravelRequirement());
	            roleRes.setMinExperience(roleEntity.getMinExperience());
	            roleRes.setMaxExperience(roleEntity.getMaxExperience());
	            roleRes.setMinInterviewRounds(roleEntity.getMinInterviewRounds());
	            roleRes.setMaxInterviewRounds(roleEntity.getMaxInterviewRounds());
	            roleRes.setCertificationsRequired(roleEntity.getCertificationsRequired());
	            roleRes.setLanguages(roleEntity.getLanguages());
	            roleRes.setAssessmentRequired(roleEntity.getAssessmentRequired());
	            roleRes.setDraft(roleEntity.getDraft());
	            roleRes.setSubmitted(roleEntity.getSubmitted());
	            roleRes.setApproved(roleEntity.getApproved());

	            response.setRolesAndRequirementsResponse(roleRes);
	        }

	        if (sourcingEntity != null) {

	            SourcingStrategyResponse srcRes = new SourcingStrategyResponse();

	            srcRes.setId(sourcingEntity.getId());
	            srcRes.setSrId(sourcingEntity.getSrId());
	            srcRes.setInternalBoard(sourcingEntity.getInternalBoard());
	            srcRes.setNaukri(sourcingEntity.getNaukri());
	            srcRes.setLinkedIn(sourcingEntity.getLinkedIn());
	            srcRes.setIndeed(sourcingEntity.getIndeed());
	            srcRes.setCompanySite(sourcingEntity.getCompanySite());
	            srcRes.setAgencyRpo(sourcingEntity.getAgencyRpo());
	            srcRes.setInternalFirstPolicy(sourcingEntity.getInternalFirstPolicy());
	            srcRes.setSourcingBudget(sourcingEntity.getSourcingBudget());
	            srcRes.setReferralEnabled(sourcingEntity.getReferralEnabled());
	            srcRes.setReferralAmount(sourcingEntity.getReferralAmount());
	            srcRes.setDiversityEnabled(sourcingEntity.getDiversityEnabled());
	            srcRes.setDiversityTags(sourcingEntity.getDiversityTags());
	            srcRes.setDraft(sourcingEntity.getDraft());
	            srcRes.setSubmitted(sourcingEntity.getSubmitted());
	            srcRes.setApproved(sourcingEntity.getApproved());

	            response.setSourcingStrategyResponse(srcRes);
	        }

	        return ApiResponse.success(
	                ResponseCode.SUCCESS,
	                Constants.SR_DATA_FETCHED_SUCCESSFULLY,
	                response
	        );

	    } catch (Exception e) {

	        log.error("Error fetching SR data for srId: {}", srId, e);

	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.FAILED_TO_FETCH_SR_DATA,
	                List.of(e.getMessage())
	        );
	    }
	}

	@Override
	public ApiResponse<?> getAll(SRFilterRequest request) {
	    log.info("StaffRequisitionsServiceImpl : Inside from getAll method");
	    try {
	    	int page = request.getPage();
	        int size = request.getSize();
	        Pageable pageable = PageRequest.of(
	                page,
	                size,
	                Sort.by(Sort.Direction.DESC, Constants.CREATED_ON)
	        );
	        Page<SRPositionBasicsEntity> pageData = positionBasicsRepository.findAll(pageable);
	        if (pageData.isEmpty()) {
	            log.warn("No SR records found");
	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    Constants.NO_DATA_FOUND,
	                    List.of(Constants.NO_RECORDS_FOUND_IN_THE_DATABASE)
	            );
	        }

	        List<Map<String, Object>> list = pageData.getContent().stream().map(sr -> {
	            Map<String, Object> map = new HashMap<>();
	            map.put(Constants.SR_ID, sr.getSrId());
	            map.put(Constants.JOB_TITLE, sr.getJobTitle());
	            map.put(Constants.CREATED_DATE, sr.getCreatedOn());

	            String status;
	            if (Boolean.TRUE.equals(sr.getApproved())) {
	                status = Constants.APPROVED;
	            } else if (Boolean.TRUE.equals(sr.getSubmitted())) {
	                status = Constants.SUBMITTED;
	            } else {
	                status = Constants.DRAFT;
	            }
	            map.put(Constants.STATUS, status);
	            return map;
	        }).toList();
	        log.info("SUCCESS - Total records: {}", pageData.getTotalElements());
	        return ApiResponse.success(
	                ResponseCode.SUCCESS,
	               Constants.SR_DATA_FETCHED_SUCCESSFULLY,
	                Map.of(
	                        Constants.CONTENT, list,
	                        Constants.CURRENT_PAGE, pageData.getNumber(),
	                        Constants.TOTAL_PAGES, pageData.getTotalPages(),
	                        Constants.TOTAL_ELEMENTS, pageData.getTotalElements()
	                )
	        );
	    } catch (Exception e) {
	        log.error("ERROR fetching SR list", e);
	        log.info("StaffRequisitionsServiceImpl : Exit from getAll method");
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                Constants.FAILED_TO_FETCH_SR_DATA,
	                List.of(e.getMessage())
	        );
	    }
	}

	
	private String generateSrId(SRPositionBasicsEntity entity) {
	    int year = java.time.LocalDateTime.now().getYear();
	    String prefix = "NA";
	    if (entity.getBusinessUnitId() != null) {
	        Optional<DepartmentsEntity> deptOpt =
	                departmentsRepository.findByBusinessUnitId(entity.getBusinessUnitId());        
	        if (deptOpt.isPresent()) {
	            String deptCode = deptOpt.get().getDepartmentCode();

	            if (deptCode != null && !deptCode.trim().isEmpty()) {
	                prefix = deptCode.trim().toUpperCase();
	            }
	        }
	    }
	    Long sequenceNumber = staffingRequisitionRepository.getNextSrSequence();
	    String formattedSeq = String.format("%04d", sequenceNumber);
	    return "SR-" + year + "-" + prefix + "-" + formattedSeq;
	}
	

}


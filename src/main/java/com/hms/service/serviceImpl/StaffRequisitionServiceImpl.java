package com.hms.service.serviceImpl;

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
import com.hms.service.entity.BudgetAndCompensationEntity;
import com.hms.service.entity.BusinessJustificationEntity;
import com.hms.service.entity.RolesAndRequirementsEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.entity.SourcingStrategyEntity;
import com.hms.service.entity.StaffingRequisitionEntitys;
import com.hms.service.repository.BudgetAndCompensationRepository;
import com.hms.service.repository.BusinessJustificationRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RolesAndRequirementsRepository;
import com.hms.service.repository.SourceStrategyRepository;
import com.hms.service.repository.Staffing;
import com.hms.service.repository.StaffingRequisitionRepository;
import com.hms.service.request.PositonBascicsRequest;
import com.hms.service.request.SRFilterRequest;
import com.hms.service.request.StaffingRequisitionRequest;
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

	@Override
	public ApiResponse<?> newStaffingRequisition(StaffingRequisitionRequest request, MultipartFile file) {
		if (request.getPositonBascicsRequest() != null) {
			PositonBascicsRequest positonBasicsRequest = request.getPositonBascicsRequest();
			if (request.getPositonBascicsRequest().getId() == null) {

				validatePositonBasicsRequest(positonBasicsRequest);
				
				ApiResponse<?> error;
				error = validateObject(positonBasicsRequest.getJobTitle(), "jobTitle");
				if (error != null) {
					return error;
				}
				error = validateObject(String.valueOf(positonBasicsRequest.getDepartmentId()), "departmentId");
				if (error != null) {
					return error;
				}
				for (int i = 0; i < positonBasicsRequest.getReportingManagerInfo().size(); i++) {
					error = validateObject(String.valueOf(positonBasicsRequest.getReportingManagerInfo().get(i)),
							"reportingManagerInfo");
					if (error != null) {
						return error;
					}
				}
				error = validateObject(positonBasicsRequest.getLocation(), "location");
				if (error != null) {
					return error;
				}
				error = validateObject(positonBasicsRequest.getSeniorityLevel(), "seniorityLevel");
				if (error != null) {
					return error;
				}
				error = validateObject(String.valueOf(positonBasicsRequest.getOpenings()), "openings");
				if (error != null) {
					return error;
				}
				error = validateObject(String.valueOf(positonBasicsRequest.getTargetStartDate()), "targetStartDate");
				if (error != null) {
					return error;
				}
				error = validateObject(positonBasicsRequest.getEmploymentType(), "employementType");
				if (error != null) {
					return error;
				}
				error = validateObject(positonBasicsRequest.getWorkMode(), "workMode");
				if (error != null) {
					return error;
				}
				error = validateObject(positonBasicsRequest.getPriority(), "priority");
				if (error != null) {
					return error;
				}
				SRPositionBasicsEntity srPositionBasicsEntity = new SRPositionBasicsEntity();
				srPositionBasicsEntity.setJobTitle(positonBasicsRequest.getJobTitle());
				srPositionBasicsEntity.setBusinessUnitId(positonBasicsRequest.getBusinessUnitId());
				srPositionBasicsEntity.setDepartmentId(positonBasicsRequest.getDepartmentId());
				srPositionBasicsEntity.setReportingManagerInfo(positonBasicsRequest.getReportingManagerInfo());
				srPositionBasicsEntity.setLocation(positonBasicsRequest.getLocation());
				srPositionBasicsEntity.setSeniorityLevel(positonBasicsRequest.getSeniorityLevel());
				srPositionBasicsEntity.setOpenings(positonBasicsRequest.getOpenings());
				srPositionBasicsEntity.setTargetStartDate(positonBasicsRequest.getTargetStartDate());
				srPositionBasicsEntity.setEmploymentType(positonBasicsRequest.getEmploymentType());
				srPositionBasicsEntity.setWorkMode(positonBasicsRequest.getWorkMode());
				srPositionBasicsEntity.setPriority(positonBasicsRequest.getPriority());
				
				staffingRequisitionRepository.save(srPositionBasicsEntity);
				
		        
			}
			else
			{
				
				Optional<SRPositionBasicsEntity> entity = staffingRequisitionRepository.findById(positonBasicsRequest.getId());
				
				
				
			}

		}
		
		return null;

	}
		
	private ApiResponse<?> validateObject(String value, String fieldName) {
		if (value == null || value.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " is required",
					List.of(fieldName + " is required"));
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

	@Override
	public void test() {
		// TODO Auto-generated method stub

	}


	public ResponseEntity <?> validatePositonBasicsRequest(PositonBascicsRequest positonBasicsRequest){
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

	        SRPositionBasicsEntity srPositionBasicsEntity = positionBasicsRepository.findBySrId(srId).orElse(null);
	        BusinessJustificationEntity businessJustificationEntity = businessJustificationRepository.findBySrId(srId).orElse(null);
	        BudgetAndCompensationEntity budgetAndCompensationEntity = budgetAndCompensationRepository.findBySrId(srId).orElse(null);
	        RolesAndRequirementsEntity rolesAndRequirementsEntity = rolesAndRequirementsRepository.findBySrId(srId).orElse(null);
	        SourcingStrategyEntity sourcingStrategyEntity = sourceStrategyRepository.findBySrId(srId).orElse(null);

	        if (srPositionBasicsEntity == null && businessJustificationEntity == null && budgetAndCompensationEntity == null &&
	        		rolesAndRequirementsEntity == null && sourcingStrategyEntity == null) {

	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    Constants.NO_DATA_FOUND,
	                    List.of(Constants.INVALID_SR_ID_IS + srId)
	            );
	        }

	        StaffingRequisitionResponse staffingRequisitionResponse = new StaffingRequisitionResponse();
	        staffingRequisitionResponse.setSrId(srId);

	        if (srPositionBasicsEntity != null) {
	        	staffingRequisitionResponse.setJobTitle(srPositionBasicsEntity.getJobTitle());
	        	staffingRequisitionResponse.setBusinessUnitId(srPositionBasicsEntity.getBusinessUnitId());
	        	staffingRequisitionResponse.setDepartmentId(srPositionBasicsEntity.getDepartmentId());
	        	staffingRequisitionResponse.setReportingManagerInfo(srPositionBasicsEntity.getReportingManagerInfo());
	        	staffingRequisitionResponse.setLocation(srPositionBasicsEntity.getLocation());
	        	staffingRequisitionResponse.setSeniorityLevel(srPositionBasicsEntity.getSeniorityLevel());
	        	staffingRequisitionResponse.setOpenings(srPositionBasicsEntity.getOpenings());
	        	staffingRequisitionResponse.setTargetStartDate(srPositionBasicsEntity.getTargetStartDate());
	        	staffingRequisitionResponse.setWorkMode(srPositionBasicsEntity.getWorkMode());
	        	staffingRequisitionResponse.setEmploymentType(srPositionBasicsEntity.getEmploymentType());
	        	staffingRequisitionResponse.setPriority(srPositionBasicsEntity.getPriority());
	        }
	        if (businessJustificationEntity != null) {
	        	staffingRequisitionResponse.setBusinessCase(businessJustificationEntity.getBusinessCase());
	        	staffingRequisitionResponse.setImpactIfNotFilled(businessJustificationEntity.getImpactIfNotFilled());
	        	staffingRequisitionResponse.setReplacesEmployee(businessJustificationEntity.getReplacesEmployee());
	        	staffingRequisitionResponse.setDocument(businessJustificationEntity.getDocument());
	        	staffingRequisitionResponse.setDraft(businessJustificationEntity.getDraft());
	        	staffingRequisitionResponse.setSubmitted(businessJustificationEntity.getSubmitted());
	        	staffingRequisitionResponse.setApproved(businessJustificationEntity.getApproved());
	        	staffingRequisitionResponse.setRequisitionType(businessJustificationEntity.getRequisitionType());
	        }
	        if (budgetAndCompensationEntity != null) {
	        	staffingRequisitionResponse.setProposedTotalCompensation(budgetAndCompensationEntity.getProposedTotalCompensation());
	        	staffingRequisitionResponse.setSigningBonus(budgetAndCompensationEntity.getSigningBonus());
	        	staffingRequisitionResponse.setEquity(budgetAndCompensationEntity.getEquity());
	        	staffingRequisitionResponse.setRelocationBudget(budgetAndCompensationEntity.getRelocationBudget());
	        	staffingRequisitionResponse.setSigningBonusAmount(budgetAndCompensationEntity.getSigningBonusAmount());
	        	staffingRequisitionResponse.setEquityAmount(budgetAndCompensationEntity.getEquityAmount());
	        	staffingRequisitionResponse.setRelocationBudgetAmount(budgetAndCompensationEntity.getRelocationBudgetAmount());
	        	staffingRequisitionResponse.setAnnualHiringCost(budgetAndCompensationEntity.getAnnualHiringCost());
	        }
	        if (rolesAndRequirementsEntity != null) {
	        	staffingRequisitionResponse.setSkillsMustHave(rolesAndRequirementsEntity.getSkillsMustHave());
	        	staffingRequisitionResponse.setNiceToHaveSkills(rolesAndRequirementsEntity.getNiceToHaveSkills());
	        	staffingRequisitionResponse.setEducationRequirement(rolesAndRequirementsEntity.getEducationRequirement());
	        	staffingRequisitionResponse.setTravelRequirement(rolesAndRequirementsEntity.getTravelRequirement());
	        	staffingRequisitionResponse.setMinExperience(rolesAndRequirementsEntity.getMinExperience());
	        	staffingRequisitionResponse.setMaxExperience(rolesAndRequirementsEntity.getMaxExperience());
	        	staffingRequisitionResponse.setMinInterviewRounds(rolesAndRequirementsEntity.getMinInterviewRounds());
	        	staffingRequisitionResponse.setMaxInterviewRounds(rolesAndRequirementsEntity.getMaxInterviewRounds());
	        	staffingRequisitionResponse.setCertificationsRequired(rolesAndRequirementsEntity.getCertificationsRequired());
	        	staffingRequisitionResponse.setLanguages(rolesAndRequirementsEntity.getLanguages());
	        	staffingRequisitionResponse.setAssessmentRequired(rolesAndRequirementsEntity.getAssessmentRequired());
	        }
	        if (sourcingStrategyEntity != null) {
	        	staffingRequisitionResponse.setInternalBoard(sourcingStrategyEntity.getInternalBoard());
	        	staffingRequisitionResponse.setNaukri(sourcingStrategyEntity.getNaukri());
	        	staffingRequisitionResponse.setLinkedIn(sourcingStrategyEntity.getLinkedIn());
	        	staffingRequisitionResponse.setIndeed(sourcingStrategyEntity.getIndeed());
	        	staffingRequisitionResponse.setCompanySite(sourcingStrategyEntity.getCompanySite());
	        	staffingRequisitionResponse.setAgencyRpo(sourcingStrategyEntity.getAgencyRpo());
	        	staffingRequisitionResponse.setInternalFirstPolicy(sourcingStrategyEntity.getInternalFirstPolicy());
	        	staffingRequisitionResponse.setSourcingBudget(sourcingStrategyEntity.getSourcingBudget());
	        	staffingRequisitionResponse.setReferralEnabled(sourcingStrategyEntity.getReferralEnabled());
	        	staffingRequisitionResponse.setReferralAmount(sourcingStrategyEntity.getReferralAmount());
	        	staffingRequisitionResponse.setDiversityEnabled(sourcingStrategyEntity.getDiversityEnabled());
	        	staffingRequisitionResponse.setDiversityTags(sourcingStrategyEntity.getDiversityTags());
	        }

	        return ApiResponse.success(
	                ResponseCode.SUCCESS,
	                Constants.SR_DATA_FETCHED_SUCCESSFULLY,
	                staffingRequisitionResponse
	        );

	    } catch (Exception e) {
	        log.error("Error fetching SR data for srId: {}", srId, e);
	        log.info("StaffRequisitionsServiceImpl : Exit from getBySrId method");
	        return ApiResponse.failure(ResponseCode.FAILURE,Constants.FAILED_TO_FETCH_SR_DATA,List.of(e.getMessage())
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
		String jobTitle = entity.getJobTitle();
		StringBuilder prefixBuilder = new StringBuilder();
		if (jobTitle != null && !jobTitle.trim().isEmpty()) {
			String[] words = jobTitle.trim().split("\\s+");
			for (String word : words) {
				prefixBuilder.append(Character.toUpperCase(word.charAt(0)));
			}
		} else {
			prefixBuilder.append("REQ");
		}
		String prefix = prefixBuilder.toString();
		String formattedId = String.format("%04d", entity.getId());
		return "SR-" + year + "-" + prefix + "-" + formattedId;
	}

}

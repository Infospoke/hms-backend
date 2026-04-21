package com.hms.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public ApiResponse<?> getBySrId(String srId) {
		  log.info("StaffRequisitionsServiceImpl : Inside getBySrId method");
		  log.info("Entering getBySrId method with srId: {}", srId);
	    try {

	        if (srId == null || srId.isBlank()) {
	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "SR ID is required",
	                    List.of("srId cannot be null or empty")
	            );
	        }

	        // Fetch all steps
	        SRPositionBasicsEntity basic = positionBasicsRepository.findBySrId(srId).orElse(null);
	        BusinessJustificationEntity justification = businessJustificationRepository.findBySrId(srId).orElse(null);
	        BudgetAndCompensationEntity budget = budgetAndCompensationRepository.findBySrId(srId).orElse(null);
	        RolesAndRequirementsEntity role = rolesAndRequirementsRepository.findBySrId(srId).orElse(null);
	        SourcingStrategyEntity sourcing = sourceStrategyRepository.findBySrId(srId).orElse(null);

	        if (basic == null && justification == null && budget == null &&
	            role == null && sourcing == null) {

	            return ApiResponse.failure(
	                    ResponseCode.FAILURE,
	                    "No data found",
	                    List.of("Invalid SR ID: " + srId)
	            );
	        }

	        StaffingRequisitionResponse staffingRequisitionResponse = new StaffingRequisitionResponse();
	        staffingRequisitionResponse.setSrId(srId);

	        if (basic != null) {
	        	staffingRequisitionResponse.setJobTitle(basic.getJobTitle());
	        	staffingRequisitionResponse.setBusinessUnitId(basic.getBusinessUnitId());
	        	staffingRequisitionResponse.setDepartmentId(basic.getDepartmentId());
	        	staffingRequisitionResponse.setReportingManagerInfo(basic.getReportingManagerInfo());
	        	staffingRequisitionResponse.setLocation(basic.getLocation());
	        	staffingRequisitionResponse.setSeniorityLevel(basic.getSeniorityLevel());
	        	staffingRequisitionResponse.setOpenings(basic.getOpenings());
	        	staffingRequisitionResponse.setTargetStartDate(basic.getTargetStartDate());
	        	staffingRequisitionResponse.setWorkMode(basic.getWorkMode());
	        	staffingRequisitionResponse.setEmploymentType(basic.getEmploymentType());
	        	staffingRequisitionResponse.setPriority(basic.getPriority());
	            staffingRequisitionResponse.setRequisitionType(basic.getRequisitionType());
	        }
	        if (justification != null) {
	        	staffingRequisitionResponse.setBusinessCase(justification.getBusinessCase());
	        	staffingRequisitionResponse.setImpactIfNotFilled(justification.getImpactIfNotFilled());
	        	staffingRequisitionResponse.setReplacesEmployee(justification.getReplacesEmployee());
	        	staffingRequisitionResponse.setDocument(justification.getDocument());
	        	staffingRequisitionResponse.setDraft(justification.getDraft());
	        	staffingRequisitionResponse.setSubmitted(justification.getSubmitted());
	        	staffingRequisitionResponse.setApproved(justification.getApproved());
	        }
	        if (budget != null) {
	        	staffingRequisitionResponse.setProposedTotalCompensation(budget.getProposedTotalCompensation());
	        	staffingRequisitionResponse.setSigningBonus(budget.getSigningBonus());
	        	staffingRequisitionResponse.setEquity(budget.getEquity());
	        	staffingRequisitionResponse.setRelocationBudget(budget.getRelocationBudget());
	        	staffingRequisitionResponse.setSigningBonusAmount(budget.getSigningBonusAmount());
	        	staffingRequisitionResponse.setEquityAmount(budget.getEquityAmount());
	        	staffingRequisitionResponse.setRelocationBudgetAmount(budget.getRelocationBudgetAmount());
	        	staffingRequisitionResponse.setAnnualHiringCost(budget.getAnnualHiringCost());
	        }
	        if (role != null) {
	        	staffingRequisitionResponse.setSkillsMustHave(role.getSkillsMustHave());
	        	staffingRequisitionResponse.setNiceToHaveSkills(role.getNiceToHaveSkills());
	        	staffingRequisitionResponse.setEducationRequirement(role.getEducationRequirement());
	        	staffingRequisitionResponse.setTravelRequirement(role.getTravelRequirement());
	        	staffingRequisitionResponse.setMinExperience(role.getMinExperience());
	        	staffingRequisitionResponse.setMaxExperience(role.getMaxExperience());
	        	staffingRequisitionResponse.setMinInterviewRounds(role.getMinInterviewRounds());
	        	staffingRequisitionResponse.setMaxInterviewRounds(role.getMaxInterviewRounds());
	        	staffingRequisitionResponse.setCertificationsRequired(role.getCertificationsRequired());
	        	staffingRequisitionResponse.setLanguages(role.getLanguages());
	        	staffingRequisitionResponse.setAssessmentRequired(role.getAssessmentRequired());
	        }
	        if (sourcing != null) {
	        	staffingRequisitionResponse.setInternalBoard(sourcing.getInternalBoard());
	        	staffingRequisitionResponse.setNaukri(sourcing.getNaukri());
	        	staffingRequisitionResponse.setLinkedIn(sourcing.getLinkedIn());
	        	staffingRequisitionResponse.setIndeed(sourcing.getIndeed());
	        	staffingRequisitionResponse.setCompanySite(sourcing.getCompanySite());
	        	staffingRequisitionResponse.setAgencyRpo(sourcing.getAgencyRpo());
	        	staffingRequisitionResponse.setInternalFirstPolicy(sourcing.getInternalFirstPolicy());
	        	staffingRequisitionResponse.setSourcingBudget(sourcing.getSourcingBudget());
	        	staffingRequisitionResponse.setReferralEnabled(sourcing.getReferralEnabled());
	        	staffingRequisitionResponse.setReferralAmount(sourcing.getReferralAmount());
	        	staffingRequisitionResponse.setDiversityEnabled(sourcing.getDiversityEnabled());
	        	staffingRequisitionResponse.setDiversityTags(sourcing.getDiversityTags());
	        }

	        return ApiResponse.success(
	                ResponseCode.SUCCESS,
	                "SR data fetched successfully",
	                staffingRequisitionResponse
	        );

	    } catch (Exception e) {
	        log.error("Error fetching SR data for srId: {}", srId, e);
	        log.info("StaffRequisitionsServiceImpl : Exit from getBySrId method");
	        return ApiResponse.failure(ResponseCode.FAILURE,"Failed to fetch SR data",List.of(e.getMessage())
	        );
	    }
	}

	@Override
	public ApiResponse<?> getAll(int page, int size) {
		// TODO Auto-generated method stub
		return null;
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

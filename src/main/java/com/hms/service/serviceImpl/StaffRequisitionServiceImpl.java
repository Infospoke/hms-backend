package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.dto.StaffingRequisitionResponseDto;
import com.hms.service.entity.BudgetAndCompensationEntity;
import com.hms.service.entity.BusinessJustificationEntity;
import com.hms.service.entity.RolesAndRequirementsEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.entity.SourcingStrategyEntity;
import com.hms.service.repository.BudgetAndCompensationRepository;
import com.hms.service.repository.BusinessJustificationRepository;
import com.hms.service.repository.BusinessUnitRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RolesAndRequirementsRepository;
import com.hms.service.repository.SourceStrategyRepository;
import com.hms.service.repository.StaffingRequisitionRepository;
import com.hms.service.request.BudgetAndCompensationRequest;
import com.hms.service.request.BusinessJustificationRequest;
import com.hms.service.request.PositonBascicsRequest;
import com.hms.service.request.ReviewRequest;
import com.hms.service.request.RolesAndRequirementsRequest;
import com.hms.service.request.SRFilterRequest;
import com.hms.service.request.SourcingStrategyRequest;
import com.hms.service.request.StaffingRequisitionRequest;
import com.hms.service.response.BudgetAndCompensationResponse;
import com.hms.service.response.BusinessJustificationResponse;
import com.hms.service.response.BusinessValidationResponse;
import com.hms.service.response.PositonBasicsResponse;
import com.hms.service.response.RolesAndRequirementsResponse;
import com.hms.service.response.SourcingStrategyResponse;
import com.hms.service.service.IStaffingRequisitionService;
import com.hms.service.utils.SequenceGenerator;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StaffRequisitionServiceImpl implements IStaffingRequisitionService {

	@Autowired
	private StaffingRequisitionRepository staffingRequisitionRepository;

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
	 private BusinessUnitRepository businessUnitRepository;
	 
	@Autowired
	private SequenceGenerator sequenceGenerator;
	
	@Autowired
	private HttpServletRequest httpServletRequest; 

	@Autowired
	private UserServiceImpl userService;

	@Override
	public ApiResponse<?> newStaffingRequisition(StaffingRequisitionRequest request, MultipartFile file) {

		String srId = null;
		ApiResponse<?> finalResponse = null;
		if (request.getPositonBascicsRequest() != null) {

			PositonBascicsRequest positonBasicsRequest = request.getPositonBascicsRequest();
			ApiResponse<?> error = validatePositonBasicsRequest(positonBasicsRequest);
			if (error != null)
				return error;

			SRPositionBasicsEntity srPositionBasicsEntity = null;
			if (positonBasicsRequest.getId() != null) {
				srPositionBasicsEntity = staffingRequisitionRepository.findById(positonBasicsRequest.getId())
						.orElse(null);
			}

			if (srPositionBasicsEntity == null) {
				srPositionBasicsEntity = new SRPositionBasicsEntity();
				srPositionBasicsEntity.setSubmitted(false);
				srPositionBasicsEntity.setApproved(false);
				srPositionBasicsEntity.setCreatedOn(LocalDate.now());
				
	            String authHeader = httpServletRequest.getHeader("Authorization");
	            String username = "System";
	            if (authHeader != null && authHeader.startsWith("Bearer ")) {
	                String token = authHeader.substring(7);
	                username = userService.extractUsernameFromClaims(token);
	            }
	            srPositionBasicsEntity.setCreatedBy(username);
	        
			}
			srPositionBasicsEntity.setJobTitle(positonBasicsRequest.getJobTitle());
			
			if (businessUnitRepository.existsById(positonBasicsRequest.getBusinessUnitId())) {
				srPositionBasicsEntity.setBusinessUnitId(positonBasicsRequest.getBusinessUnitId());
			} else {
				log.info("BusinessUnit Id is required");
				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(Constants.BUSINESS_UNIT_REQUIRED));
			}

			if (departmentsRepository.existsById(positonBasicsRequest.getDepartmentId())) {
				srPositionBasicsEntity.setDepartmentId(positonBasicsRequest.getDepartmentId());
			} else {
				log.info("Department Id is required");
				return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of(Constants.DEPARTMENT_REQUIRED));
			}
			
			srPositionBasicsEntity.setReportingManagerInfo(positonBasicsRequest.getReportingManagerInfo());
			srPositionBasicsEntity.setLocation(positonBasicsRequest.getLocation());
			srPositionBasicsEntity.setSeniorityLevel(positonBasicsRequest.getSeniorityLevel());
			srPositionBasicsEntity.setOpenings(positonBasicsRequest.getOpenings());
			srPositionBasicsEntity.setTargetStartDate(positonBasicsRequest.getTargetStartDate());
			srPositionBasicsEntity.setEmploymentType(positonBasicsRequest.getEmploymentType());
			srPositionBasicsEntity.setWorkMode(positonBasicsRequest.getWorkMode());
			srPositionBasicsEntity.setPriority(positonBasicsRequest.getPriority());

			srId = generateSrId(srPositionBasicsEntity.getBusinessUnitId());
			srPositionBasicsEntity.setSrId(srId);
			srPositionBasicsEntity = staffingRequisitionRepository.save(srPositionBasicsEntity);
		}
		// business screen logic
		if (request.getBusinessJustificationRequest() != null) {

			BusinessJustificationRequest businessJustificationRequest = request.getBusinessJustificationRequest();

			ApiResponse<?> error = validateBusinessJustification(businessJustificationRequest, file);
			if (error != null)
				return error;

			BusinessJustificationEntity businessJustificationEntity = null;

			srId = businessJustificationRequest.getSrId();

			if (srId == null || srId.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "srId is required", List.of("srId is required"));
			}

			businessJustificationEntity = businessJustificationRepository.findBySrId(srId).orElse(null);
			if (businessJustificationEntity == null) {
				businessJustificationEntity = new BusinessJustificationEntity();

				businessJustificationEntity.setSrId(srId);
				businessJustificationEntity.setSubmitted(false);
				businessJustificationEntity.setApproved(false);
			}

			businessJustificationEntity.setRequisitionType(businessJustificationRequest.getRequisitionType());
			businessJustificationEntity.setBusinessCase(businessJustificationRequest.getBusinessCase());
			businessJustificationEntity.setImpactIfNotFilled(businessJustificationRequest.getImpactIfNotFilled());
			businessJustificationEntity.setReplacesEmployee(businessJustificationRequest.getReplacesEmployee());

			if (file != null && !file.isEmpty()) {

				String fileKey = srId + "-" + file.getOriginalFilename();

				try {
					uploadToMinio(file, fileKey);
				} catch (Exception e) {
					return ApiResponse.failure(ResponseCode.FAILURE, "File upload failed", List.of(e.getMessage()));
				}

				businessJustificationEntity.setDocument(fileKey);
			}

			businessJustificationRepository.save(businessJustificationEntity);

		}
		// budget and compensation

		if (request.getBudgetAndCompensationRequest() != null) {

			BudgetAndCompensationRequest budgetRequest = request.getBudgetAndCompensationRequest();

			ApiResponse<?> validationResponse = validateBudgetAndCompensation(budgetRequest);
			if (validationResponse != null) {
				finalResponse = validationResponse;
			}
			System.out.println("Validation Response: " + validationResponse);

			if (validationResponse != null
					&& ResponseCode.FAILURE.getCode().equals(validationResponse.getResponsecode())) {
				return validationResponse;
			}

			BusinessValidationResponse result = (BusinessValidationResponse) validationResponse.getData();

			BudgetAndCompensationEntity budgetEntity = null;

			if (budgetRequest.getSrId() != null) {
				budgetEntity = budgetAndCompensationRepository.findBySrId(budgetRequest.getSrId()).orElse(null);
			}

			if (budgetEntity == null) {
				budgetEntity = new BudgetAndCompensationEntity();

				budgetEntity.setSrId(budgetRequest.getSrId());

				budgetEntity.setSubmitted(false);
				budgetEntity.setApproved(false);
			}

			budgetEntity.setProposedTotalCompensation(budgetRequest.getProposedTotalCompensation());

			budgetEntity.setSigningBonus(budgetRequest.getSigningBonus());
			budgetEntity.setEquity(budgetRequest.getEquity());
			budgetEntity.setRelocationBudget(budgetRequest.getRelocationBudget());

			budgetEntity.setSigningBonusAmount(budgetRequest.getSigningBonusAmount());
			budgetEntity.setEquityAmount(budgetRequest.getEquityAmount());
			budgetEntity.setRelocationBudgetAmount(budgetRequest.getRelocationBudgetAmount());

			budgetEntity.setAnnualHiringCost(budgetRequest.getAnnualHiringCost());

			if (result != null) {
				budgetEntity.setBudgetCompensationStatus(result.getStatus());
				budgetEntity.setStatus(result.getMessage());
			}

			budgetAndCompensationRepository.save(budgetEntity);
		}

		if (request.getRolesAndRequirementsRequest() != null) {

			RolesAndRequirementsRequest rolesAndRequirementsRequest = request.getRolesAndRequirementsRequest();

			ApiResponse<?> error = validateRoleRequirements(rolesAndRequirementsRequest);
			if (error != null)
				return error;

			RolesAndRequirementsEntity entity = null;

			if (rolesAndRequirementsRequest.getSrId() != null) {
				entity = rolesAndRequirementsRepository.findBySrId(rolesAndRequirementsRequest.getSrId()).orElse(null);
			}

			if (entity == null) {

				entity = new RolesAndRequirementsEntity();

				srId = rolesAndRequirementsRequest.getSrId();

				if (srId == null || srId.isEmpty()) {
					return ApiResponse.failure(ResponseCode.FAILURE, "srId is required", List.of("srId is required"));
				}

				entity.setSrId(srId);
				entity.setSubmitted(false);
				entity.setApproved(false);
			}

			entity.setSkillsMustHave(rolesAndRequirementsRequest.getSkillsMustHave() != null
					? String.join(",", rolesAndRequirementsRequest.getSkillsMustHave())
					: null);

			entity.setNiceToHaveSkills((rolesAndRequirementsRequest.getNiceToHaveSkills() != null
					&& !rolesAndRequirementsRequest.getNiceToHaveSkills().isEmpty())
							? String.join(",", rolesAndRequirementsRequest.getNiceToHaveSkills())
							: null);

			entity.setEducationRequirement(rolesAndRequirementsRequest.getEducationRequirement());
			entity.setMinExperience(rolesAndRequirementsRequest.getMinExperience());
			entity.setMaxExperience(rolesAndRequirementsRequest.getMaxExperience());

			entity.setCertificationsRequired((rolesAndRequirementsRequest.getCertificationsRequired() != null
					&& !rolesAndRequirementsRequest.getCertificationsRequired().isEmpty())
							? String.join(",", rolesAndRequirementsRequest.getCertificationsRequired())
							: null);

			entity.setMinInterviewRounds(rolesAndRequirementsRequest.getMinInterviewRounds());
			entity.setMaxInterviewRounds(rolesAndRequirementsRequest.getMaxInterviewRounds());

			entity.setAssessmentRequired(rolesAndRequirementsRequest.getAssessmentRequired());
			entity.setTravelRequirement(rolesAndRequirementsRequest.getTravelRequirement());

			entity.setLanguages((rolesAndRequirementsRequest.getLanguages() != null
					&& !rolesAndRequirementsRequest.getLanguages().isEmpty())
							? String.join(",", rolesAndRequirementsRequest.getLanguages())
							: null);

			rolesAndRequirementsRepository.save(entity);

		}

		if (request.getSourcingStrategyRequest() != null) {

			SourcingStrategyRequest sourcingStrategyRequest = request.getSourcingStrategyRequest();

			ApiResponse<?> error = validateSourcingStrategyRequest(sourcingStrategyRequest);
			if (error != null)
				return error;

			srId = sourcingStrategyRequest.getSrId();

			if (srId == null || srId.isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "srId is required", List.of("srId is required"));
			}

			SourcingStrategyEntity entity = sourceStrategyRepository.findBySrId(srId).orElse(null);

			if (entity == null) {
				entity = new SourcingStrategyEntity();

				entity.setSrId(srId);
				entity.setSubmitted(false);
				entity.setApproved(false);
			}

			entity.setInternalBoard(sourcingStrategyRequest.getInternalBoard());
			entity.setNaukri(sourcingStrategyRequest.getNaukri());
			entity.setLinkedIn(sourcingStrategyRequest.getLinkedIn());
			entity.setIndeed(sourcingStrategyRequest.getIndeed());
			entity.setCompanySite(sourcingStrategyRequest.getCompanySite());
			entity.setAgencyRpo(sourcingStrategyRequest.getAgencyRpo());
			entity.setInternalFirstPolicy(sourcingStrategyRequest.getInternalFirstPolicy());
			entity.setSourcingBudget(sourcingStrategyRequest.getSourcingBudget());
			entity.setReferralEnabled(sourcingStrategyRequest.getReferralEnabled());
			entity.setReferralAmount(sourcingStrategyRequest.getReferralAmount());
			entity.setDiversityEnabled(sourcingStrategyRequest.getDiversityEnabled());
			entity.setDiversityTags(sourcingStrategyRequest.getDiversityTags());

			sourceStrategyRepository.save(entity);

		}

		if (request.getReviewRequest() != null) {
			ReviewRequest reviewRequest = request.getReviewRequest();

			if (reviewRequest.getSrId() == null || reviewRequest.getSrId().isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "srId is required",
						List.of("srId cannot be null or empty"));
			}
			try {
				positionBasicsRepository.findBySrId(srId).ifPresent(entity -> {

					entity.setSubmitted(true);
					entity.setApproved(false);
					positionBasicsRepository.save(entity);
				});

				businessJustificationRepository.findBySrId(srId).ifPresent(entity -> {

					entity.setSubmitted(true);
					entity.setApproved(false);
					businessJustificationRepository.save(entity);
				});

				budgetAndCompensationRepository.findBySrId(srId).ifPresent(entity -> {

					entity.setSubmitted(true);
					entity.setApproved(false);
					budgetAndCompensationRepository.save(entity);
				});
				rolesAndRequirementsRepository.findBySrId(srId).ifPresent(entity -> {

					entity.setSubmitted(true);
					entity.setApproved(false);
					rolesAndRequirementsRepository.save(entity);
				});
				sourceStrategyRepository.findBySrId(srId).ifPresent(entity -> {

					entity.setSubmitted(true);
					entity.setApproved(false);
					sourceStrategyRepository.save(entity);
				});

			} catch (Exception e) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Failed to submit SR", List.of(e.getMessage()));
			}
		}
		return finalResponse != null ? finalResponse
				: ApiResponse.success(ResponseCode.SUCCESS, "Staffing Requisition processed successfully", srId);

	}

	private ApiResponse<?> validateObject(String value, String fieldName) {
		if (value == null || value.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " is required",
					List.of(fieldName + " is required"));
		}

		if (fieldName.equals("businessCase") || fieldName.equals("impactIfNotFilled")) {
			if (value.length() < 100 || value.length() > 2000) {
				return ApiResponse.failure(ResponseCode.FAILURE,
						"No of Character Should be more than 100 and less than 2000",
						List.of("No of Character Should be more than 100 and less than 2000"));
			}
		}

		if (value instanceof String str && str.trim().isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " is required",
					List.of(fieldName + " is required"));
		}

		return null;
	}

	private ApiResponse<?> validateObject(Object value, String fieldName) {
		if (value instanceof Number) {
			Number num = (Number) value;
			if (num.doubleValue() <= 0) {
				return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " must be greater than 0",
						List.of(fieldName + " must be positive"));
			}
		}

		return null;
	}

	private ApiResponse<?> validateFile(MultipartFile file, String fieldName) {

		if (file != null && !file.isEmpty()) {

			if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
				return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " must be a PDF file",
						List.of(fieldName + " only accepts PDF format"));
			}

			long maxSize = 20 * 1024 * 1024;
			if (file.getSize() > maxSize) {
				return ApiResponse.failure(ResponseCode.FAILURE, fieldName + " must be less than 20MB",
						List.of(fieldName + " size exceeded"));
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

		if (req.getJobTitle() != null) {
			error = validateObject(req.getJobTitle(), "jobTitle");
			if (error != null)
				return error;
		}

		if (req.getDepartmentId() != null) {
			error = validateObject(req.getDepartmentId(), "departmentId");
			if (error != null)
				return error;
		}

		if (req.getBusinessUnitId() != null) {
			error = validateObject(req.getBusinessUnitId(), "businessUnitId");
			if (error != null)
				return error;
		}

		if (req.getReportingManagerInfo() != null) {

			if (req.getReportingManagerInfo().isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "reportingManagerInfo cannot be empty",
						List.of("reportingManagerInfo cannot be empty"));
			}

			for (Integer managerId : req.getReportingManagerInfo()) {
				if (managerId == null || managerId <= 0) {
					return ApiResponse.failure(ResponseCode.FAILURE, "Invalid reportingManagerInfo",
							List.of("Each managerId must be valid"));
				}
			}
		}

		if (req.getLocation() != null) {
			error = validateObject(req.getLocation(), "location");
			if (error != null)
				return error;
		}

		if (req.getSeniorityLevel() != null) {
			error = validateObject(req.getSeniorityLevel(), "seniorityLevel");
			if (error != null)
				return error;
		}

		if (req.getOpenings() != null) {
			error = validateObject(req.getOpenings(), "openings");
			if (error != null)
				return error;
		}

		if (req.getTargetStartDate() != null) {
			error = validateObject(req.getTargetStartDate().toString(), "targetStartDate");
			if (error != null)
				return error;
		}

		if (req.getEmploymentType() != null) {
			error = validateObject(req.getEmploymentType(), "employmentType");
			if (error != null)
				return error;
		}

		if (req.getWorkMode() != null) {
			error = validateObject(req.getWorkMode(), "workMode");
			if (error != null)
				return error;
		}

		if (req.getPriority() != null) {
			error = validateObject(req.getPriority(), "priority");
			if (error != null)
				return error;
		}

		return null;
	}

	// method for businessvalidations
	private ApiResponse<?> validateBusinessJustification(BusinessJustificationRequest bj, MultipartFile file) {

		ApiResponse<?> error;

		if (bj.getRequisitionType() != null) {
			error = validateObject(bj.getRequisitionType(), "requisitionType");
			if (error != null)
				return error;
			if (!List.of("New Headcount", "Backfill", "Replacement", "Contract to Perm")
					.contains(bj.getRequisitionType())) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid requisitionType",
						List.of("Allowed values: New Headcount, Backfill, Replacement, Contract to Perm"));
			}
		}

		if (bj.getBusinessCase() != null) {
			error = validateObject(bj.getBusinessCase(), "businessCase");
			if (error != null)
				return error;
		}

		if (bj.getImpactIfNotFilled() != null) {
			error = validateObject(bj.getImpactIfNotFilled(), "impactIfNotFilled");
			if (error != null)
				return error;
		}
		if (bj.getRequisitionType() != null && ("Backfill".equalsIgnoreCase(bj.getRequisitionType())
				|| "Replacement".equalsIgnoreCase(bj.getRequisitionType()))) {

			if (bj.getReplacesEmployee() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "replacesEmployee is required",
						List.of("replacesEmployee is required"));
			}
		}

		if (file != null && !file.isEmpty()) {
			error = validateFile(file, "document");
			if (error != null)
				return error;
		}
		return null;
	}

	// method for budget and compensation validations

	private ApiResponse<?> validateBudgetAndCompensation(BudgetAndCompensationRequest req) {

		ApiResponse<?> error;

		if (req.getMinSalary() != null) {
			error = validateObject(req.getMinSalary(), "minSalary");
			if (error != null)
				return error;
		}

		if (req.getMaxSalary() != null) {
			error = validateObject(req.getMaxSalary(), "maxSalary");
			if (error != null)
				return error;
		}

		if (req.getProposedTotalCompensation() != null) {
			error = validateObject(req.getProposedTotalCompensation(), "proposedTotalCompensation");
			if (error != null)
				return error;

		}

		if (Boolean.TRUE.equals(req.getSigningBonus())) {
			if (req.getSigningBonusAmount() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "signingBonusAmount is required",
						List.of("Enter signing bonus amount"));

			}
		}

		if (Boolean.TRUE.equals(req.getEquity())) {
			if (req.getEquityAmount() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "equityAmount is required",
						List.of("Enter equity amount"));

			}
		}

		if (Boolean.TRUE.equals(req.getRelocationBudget())) {
			if (req.getRelocationBudgetAmount() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "relocationBudgetAmount is required",
						List.of("Enter relocation budget amount"));
			}
		}

		int total =

				(req.getProposedTotalCompensation() != null ? req.getProposedTotalCompensation() : 0)
						+ (req.getSigningBonusAmount() != null ? req.getSigningBonusAmount() : 0)
						+ (req.getEquityAmount() != null ? req.getEquityAmount() : 0)
						+ (req.getRelocationBudgetAmount() != null ? req.getRelocationBudgetAmount() : 0);

		if (req.getAnnualHiringCost() != null) {
			if (total != req.getAnnualHiringCost()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "AnnualHiringCost mismatch",
						List.of("Sum of all components must equal Annual Hiring Cost"));

			}
		}
		if (req.getMinSalary() != null && req.getMaxSalary() != null && req.getProposedTotalCompensation() != null) {
			int min = req.getMinSalary();
			int max = req.getMaxSalary();
			int proposed = req.getProposedTotalCompensation();
			if (min > max) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid salary range",

						List.of("minSalary cannot be greater than maxSalary"));
			}
			double maxWith5Percent = max + (max * 0.05);
			if (proposed >= min && proposed <= max) {
				return ApiResponse.success(ResponseCode.SUCCESS, "Success",
						new BusinessValidationResponse("GREEN", "Within Range"));
			} else if (proposed < min) {
				return ApiResponse.success(ResponseCode.SUCCESS, "Success",
						new BusinessValidationResponse("YELLOW", "Offer Risk"));
			} else if (proposed > max && proposed <= maxWith5Percent) {
				return ApiResponse.success(ResponseCode.SUCCESS, "Success",
						new BusinessValidationResponse("YELLOW", "Range Review Required"));
			} else {
				return ApiResponse.success(ResponseCode.SUCCESS, "Success",
						new BusinessValidationResponse("RED", "Out of Range"));

			}
		}
		return null;

	}

	private ApiResponse<?> validateRoleRequirements(RolesAndRequirementsRequest req) {

		ApiResponse<?> error;

		if (req.getSrId() == null || req.getSrId().isBlank()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "srId is required",
					List.of("srId cannot be null or empty"));
		}

		if (req.getSkillsMustHave() != null) {
			if (req.getSkillsMustHave().isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "skillsMustHave cannot be empty",
						List.of("Provide at least one must-have skill"));
			}
		}

		if (req.getNiceToHaveSkills() != null) {
			if (req.getNiceToHaveSkills().isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "niceToHaveSkills cannot be empty",
						List.of("Remove empty list or provide values"));
			}
		}

		if (req.getEducationRequirement() != null) {
			error = validateObject(req.getEducationRequirement(), "educationRequirement");
			if (error != null)
				return error;
		}

		if (req.getTravelRequirement() != null) {
			error = validateObject(req.getTravelRequirement(), "travelRequirement");
			if (error != null)
				return error;
		}

		if (req.getMinExperience() != null && req.getMaxExperience() != null) {

			if (req.getMinExperience() < 0 || req.getMaxExperience() < 0) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid experience",
						List.of("Experience cannot be negative"));
			}

			if (req.getMinExperience() > req.getMaxExperience()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid experience range",
						List.of("minExperience cannot be greater than maxExperience"));
			}
		}

		if (req.getMinInterviewRounds() != null && req.getMaxInterviewRounds() != null) {

			if (req.getMinInterviewRounds() <= 0 || req.getMaxInterviewRounds() <= 0) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid interview rounds",
						List.of("Interview rounds must be greater than 0"));
			}

			if (req.getMinInterviewRounds() > req.getMaxInterviewRounds()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Invalid interview range",
						List.of("minInterviewRounds cannot be greater than maxInterviewRounds"));
			}
		}

		if (req.getCertificationsRequired() != null && req.getCertificationsRequired().isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "certificationsRequired cannot be empty",
					List.of("Provide valid certifications or remove field"));
		}

		if (req.getLanguages() != null && req.getLanguages().isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "languages cannot be empty",
					List.of("Provide valid languages or remove field"));
		}

		return null;
	}

	private ApiResponse<?> validateSourcingStrategyRequest(SourcingStrategyRequest req) {

		ApiResponse<?> error;

		if (req == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Request is required",
					List.of("SourcingStrategyRequest cannot be null"));
		}

		if (req.getSrId() == null || req.getSrId().isBlank()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "srId is required",
					List.of("srId cannot be null or empty"));
		}

		if (req.getInternalFirstPolicy() != null) {
			error = validateObject(req.getInternalFirstPolicy(), "internalFirstPolicy");
			if (error != null)
				return error;
		}

		boolean hasJobBoard = Boolean.TRUE.equals(req.getInternalBoard()) || Boolean.TRUE.equals(req.getLinkedIn())
				|| Boolean.TRUE.equals(req.getNaukri()) || Boolean.TRUE.equals(req.getIndeed())
				|| Boolean.TRUE.equals(req.getCompanySite());

		if (!hasJobBoard) {
			return ApiResponse.failure(ResponseCode.FAILURE, "targetJobBoard is required",
					List.of("At least one job board must be selected"));
		}

		if (Boolean.TRUE.equals(req.getReferralEnabled())) {

			if (req.getReferralAmount() == null) {
				return ApiResponse.failure(ResponseCode.FAILURE, "referralAmount is required",
						List.of("Provide referral amount"));
			}

			if (req.getReferralAmount() <= 0) {
				return ApiResponse.failure(ResponseCode.FAILURE, "referralAmount must be greater than 0",
						List.of("Referral amount must be positive"));
			}
		}

		if (Boolean.TRUE.equals(req.getDiversityEnabled())) {

			if (req.getDiversityTags() == null || req.getDiversityTags().isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "diversityTags is required",
						List.of("Provide diversity tags"));
			}
		}

		if (req.getSourcingBudget() != null && req.getSourcingBudget() < 0) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Invalid sourcingBudget",
					List.of("Sourcing budget cannot be negative"));
		}

		return null;
	}

	@Override
	public ApiResponse<?> getBySrId(String srId) {

		log.info("StaffRequisitionsServiceImpl : Inside getBySrId method");

		try {

			if (srId == null || srId.isBlank()) {
				return ApiResponse.failure(ResponseCode.FAILURE, Constants.SR_ID_IS_REQUIRED,
						List.of(Constants.SR_ID_CANNOT_BE_NULL_OR_EMPTY));
			}

			SRPositionBasicsEntity srPositionBasicsEntity = positionBasicsRepository.findBySrId(srId).orElse(null);
			BusinessJustificationEntity businessJustificationEntity = businessJustificationRepository.findBySrId(srId).orElse(null);
			BudgetAndCompensationEntity budgetAndCompensationEntity = budgetAndCompensationRepository.findBySrId(srId).orElse(null);
			RolesAndRequirementsEntity rolesAndRequirementsEntity = rolesAndRequirementsRepository.findBySrId(srId).orElse(null);
			SourcingStrategyEntity sourcingStrategyEntity = sourceStrategyRepository.findBySrId(srId).orElse(null);

			if (srPositionBasicsEntity == null && businessJustificationEntity == null && budgetAndCompensationEntity == null && rolesAndRequirementsEntity == null
					&& sourcingStrategyEntity == null) {

				return ApiResponse.failure(ResponseCode.FAILURE, Constants.NO_DATA_FOUND,
						List.of(Constants.INVALID_SR_ID_IS + srId));
			}

			StaffingRequisitionResponseDto response = new StaffingRequisitionResponseDto();
			if (srPositionBasicsEntity != null) {

				PositonBasicsResponse positonBasicsResponse = new PositonBasicsResponse();

				positonBasicsResponse.setId(srPositionBasicsEntity.getId());
				positonBasicsResponse.setSrId(srPositionBasicsEntity.getSrId());
				positonBasicsResponse.setJobTitle(srPositionBasicsEntity.getJobTitle());
				positonBasicsResponse.setBusinessUnitId(srPositionBasicsEntity.getBusinessUnitId());
				positonBasicsResponse.setDepartmentId(srPositionBasicsEntity.getDepartmentId());
				positonBasicsResponse.setReportingManagerInfo(srPositionBasicsEntity.getReportingManagerInfo());
				positonBasicsResponse.setLocation(srPositionBasicsEntity.getLocation());
				positonBasicsResponse.setSeniorityLevel(srPositionBasicsEntity.getSeniorityLevel());
				positonBasicsResponse.setOpenings(srPositionBasicsEntity.getOpenings());
				positonBasicsResponse.setTargetStartDate(srPositionBasicsEntity.getTargetStartDate());
				positonBasicsResponse.setWorkMode(srPositionBasicsEntity.getWorkMode());
				positonBasicsResponse.setEmploymentType(srPositionBasicsEntity.getEmploymentType());
				positonBasicsResponse.setPriority(srPositionBasicsEntity.getPriority());
				positonBasicsResponse.setSubmitted(srPositionBasicsEntity.getSubmitted());
				positonBasicsResponse.setApproved(srPositionBasicsEntity.getApproved());
				positonBasicsResponse.setCreatedOn(srPositionBasicsEntity.getCreatedOn());
				positonBasicsResponse.setCreatedBy(srPositionBasicsEntity.getCreatedBy());

				response.setPositonBasicsResponse(positonBasicsResponse);
			}
			if (businessJustificationEntity != null) {

				BusinessJustificationResponse businessJustificationResponse = new BusinessJustificationResponse();

				businessJustificationResponse.setId(businessJustificationEntity.getId());
				businessJustificationResponse.setSrId(businessJustificationEntity.getSrId());
				businessJustificationResponse.setRequisitionType(businessJustificationEntity.getRequisitionType());
				businessJustificationResponse.setBusinessCase(businessJustificationEntity.getBusinessCase());
				businessJustificationResponse.setImpactIfNotFilled(businessJustificationEntity.getImpactIfNotFilled());
				businessJustificationResponse.setReplacesEmployee(businessJustificationEntity.getReplacesEmployee());
				businessJustificationResponse.setDocument(businessJustificationEntity.getDocument());
				businessJustificationResponse.setSubmitted(businessJustificationEntity.getSubmitted());
				businessJustificationResponse.setApproved(businessJustificationEntity.getApproved());

				response.setBusinessJustificationResponse(businessJustificationResponse);
			}
			if (budgetAndCompensationEntity != null) {

				BudgetAndCompensationResponse budgetAndCompensationResponse = new BudgetAndCompensationResponse();

				budgetAndCompensationResponse.setId(budgetAndCompensationEntity.getId());
				budgetAndCompensationResponse.setSrId(budgetAndCompensationEntity.getSrId());
				budgetAndCompensationResponse.setProposedTotalCompensation(budgetAndCompensationEntity.getProposedTotalCompensation());
				budgetAndCompensationResponse.setSigningBonus(budgetAndCompensationEntity.getSigningBonus());
				budgetAndCompensationResponse.setEquity(budgetAndCompensationEntity.getEquity());
				budgetAndCompensationResponse.setRelocationBudget(budgetAndCompensationEntity.getRelocationBudget());
				budgetAndCompensationResponse.setSigningBonusAmount(budgetAndCompensationEntity.getSigningBonusAmount());
				budgetAndCompensationResponse.setEquityAmount(budgetAndCompensationEntity.getEquityAmount());
				budgetAndCompensationResponse.setRelocationBudgetAmount(budgetAndCompensationEntity.getRelocationBudgetAmount());
				budgetAndCompensationResponse.setAnnualHiringCost(budgetAndCompensationEntity.getAnnualHiringCost());
				budgetAndCompensationResponse.setSubmitted(budgetAndCompensationEntity.getSubmitted());
				budgetAndCompensationResponse.setApproved(budgetAndCompensationEntity.getApproved());

				response.setBudgetAndCompensationResponse(budgetAndCompensationResponse);
			}
			if (rolesAndRequirementsEntity != null) {

				RolesAndRequirementsResponse rolesAndRequirementsResponse = new RolesAndRequirementsResponse();

				rolesAndRequirementsResponse.setId(rolesAndRequirementsEntity.getId());
				rolesAndRequirementsResponse.setSrId(rolesAndRequirementsEntity.getSrId());
				rolesAndRequirementsResponse.setSkillsMustHave(rolesAndRequirementsEntity.getSkillsMustHave());
				rolesAndRequirementsResponse.setNiceToHaveSkills(rolesAndRequirementsEntity.getNiceToHaveSkills());
				rolesAndRequirementsResponse.setEducationRequirement(rolesAndRequirementsEntity.getEducationRequirement());
				rolesAndRequirementsResponse.setTravelRequirement(rolesAndRequirementsEntity.getTravelRequirement());
				rolesAndRequirementsResponse.setMinExperience(rolesAndRequirementsEntity.getMinExperience());
				rolesAndRequirementsResponse.setMaxExperience(rolesAndRequirementsEntity.getMaxExperience());
				rolesAndRequirementsResponse.setMinInterviewRounds(rolesAndRequirementsEntity.getMinInterviewRounds());
				rolesAndRequirementsResponse.setMaxInterviewRounds(rolesAndRequirementsEntity.getMaxInterviewRounds());
				rolesAndRequirementsResponse.setCertificationsRequired(rolesAndRequirementsEntity.getCertificationsRequired());
				rolesAndRequirementsResponse.setLanguages(rolesAndRequirementsEntity.getLanguages());
				rolesAndRequirementsResponse.setAssessmentRequired(rolesAndRequirementsEntity.getAssessmentRequired());
				rolesAndRequirementsResponse.setSubmitted(rolesAndRequirementsEntity.getSubmitted());
				rolesAndRequirementsResponse.setApproved(rolesAndRequirementsEntity.getApproved());

				response.setRolesAndRequirementsResponse(rolesAndRequirementsResponse);
			}

			if (sourcingStrategyEntity != null) {

				SourcingStrategyResponse sourcingStrategyResponse = new SourcingStrategyResponse();

				sourcingStrategyResponse.setId(sourcingStrategyEntity.getId());
				sourcingStrategyResponse.setSrId(sourcingStrategyEntity.getSrId());
				sourcingStrategyResponse.setInternalBoard(sourcingStrategyEntity.getInternalBoard());
				sourcingStrategyResponse.setNaukri(sourcingStrategyEntity.getNaukri());
				sourcingStrategyResponse.setLinkedIn(sourcingStrategyEntity.getLinkedIn());
				sourcingStrategyResponse.setIndeed(sourcingStrategyEntity.getIndeed());
				sourcingStrategyResponse.setCompanySite(sourcingStrategyEntity.getCompanySite());
				sourcingStrategyResponse.setAgencyRpo(sourcingStrategyEntity.getAgencyRpo());
				sourcingStrategyResponse.setInternalFirstPolicy(sourcingStrategyEntity.getInternalFirstPolicy());
				sourcingStrategyResponse.setSourcingBudget(sourcingStrategyEntity.getSourcingBudget());
				sourcingStrategyResponse.setReferralEnabled(sourcingStrategyEntity.getReferralEnabled());
				sourcingStrategyResponse.setReferralAmount(sourcingStrategyEntity.getReferralAmount());
				sourcingStrategyResponse.setDiversityEnabled(sourcingStrategyEntity.getDiversityEnabled());
				sourcingStrategyResponse.setDiversityTags(sourcingStrategyEntity.getDiversityTags());
				sourcingStrategyResponse.setSubmitted(sourcingStrategyEntity.getSubmitted());
				sourcingStrategyResponse.setApproved(sourcingStrategyEntity.getApproved());

				response.setSourcingStrategyResponse(sourcingStrategyResponse);
			}

			return ApiResponse.success(ResponseCode.SUCCESS, Constants.SR_DATA_FETCHED_SUCCESSFULLY, response);

		} catch (Exception e) {

			log.error("Error fetching SR data for srId: {}", srId, e);

			return ApiResponse.failure(ResponseCode.FAILURE, Constants.FAILED_TO_FETCH_SR_DATA,
					List.of(e.getMessage()));
		}
	}

	@Override
	public ApiResponse<?> getAll(SRFilterRequest request) {
		log.info("StaffRequisitionsServiceImpl : Inside from getAll method");
		try {
			int page = request.getPage();
			int size = request.getSize();
			Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, Constants.CREATED_ON));
			Page<SRPositionBasicsEntity> pageData = positionBasicsRepository.findAll(pageable);
			if (pageData.isEmpty()) {
				log.warn("No SR records found");
				return ApiResponse.failure(ResponseCode.FAILURE, Constants.NO_DATA_FOUND,
						List.of(Constants.NO_RECORDS_FOUND_IN_THE_DATABASE));
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
			return ApiResponse.success(ResponseCode.SUCCESS, Constants.SR_DATA_FETCHED_SUCCESSFULLY,
					Map.of(Constants.CONTENT, list, Constants.CURRENT_PAGE, pageData.getNumber(), Constants.TOTAL_PAGES,
							pageData.getTotalPages(), Constants.TOTAL_ELEMENTS, pageData.getTotalElements()));
		} catch (Exception e) {
			log.error("ERROR fetching SR list", e);
			log.info("StaffRequisitionsServiceImpl : Exit from getAll method");
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.FAILED_TO_FETCH_SR_DATA,
					List.of(e.getMessage()));
		}
	}

	private String generateSrId(Integer businessUnitId) {

	    int year = java.time.LocalDateTime.now().getYear();
	    String prefix = "NA";

	    if (businessUnitId != null) {
	        String deptCode = departmentsRepository
	                .findDeptCodeByBusinessUnitId(businessUnitId);

	        if (deptCode != null && !deptCode.trim().isEmpty()) {
	            prefix = deptCode.trim().toUpperCase();
	        }
	    }
	    int srSeq = sequenceGenerator.generateSrSequence();
	    String formattedSeq = String.format("%04d", srSeq);

	    return "SR-" + year + "-" + prefix + "-" + formattedSeq;
	}

}

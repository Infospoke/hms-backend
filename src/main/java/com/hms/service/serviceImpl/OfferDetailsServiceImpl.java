package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.entity.BudgetAndCompensationEntity;
import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.OfferDetailsEntity;
import com.hms.service.repository.BudgetAndCompensationRepository;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.OfferDetailsRepository;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.response.OfferCommentsResponse;
import com.hms.service.response.OfferDetailsResponse;
import com.hms.service.service.IOfferDetailsService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OfferDetailsServiceImpl implements IOfferDetailsService {
	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;
	
	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private OfferDetailsRepository offerDetailsRepository;
	
	@Autowired
	private BudgetAndCompensationRepository budgetAndCompensationRepository;

	@Override
	public ApiResponse<?> getReadyToRelease(SpecificationFilterRequest request) {

		log.info("OfferServiceImpl :: Inside getReadyToRelease");

		if (request.getPage() == null || request.getSize() == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Page and Size are required"));

		}

		Sort sort = Sort.by(

				"DESC".equalsIgnoreCase(request.getDirection())

						? Sort.Direction.DESC

						: Sort.Direction.ASC,

				request.getSortBy() != null

						? request.getSortBy()

						: "finalApprovalTime"

		);

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Specification<OfferDetailsEntity> spec = request.buildReadyToReleaseSpecification();

		Page<OfferDetailsEntity> page = offerDetailsRepository.findAll(spec, pageable);

		List<Map<String, Object>> offers = page.getContent().stream().map(this::convertToMap).toList();

		String priorityFilter = request.getFilter("priority");

		if (priorityFilter != null) {

			offers = offers.stream()

					.filter(x -> x.get("priority").toString().equalsIgnoreCase(priorityFilter))

					.toList();
		}

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("offers", offers);

		response.put("currentPage", page.getNumber());

		response.put("totalPages", page.getTotalPages());

		response.put("totalElements", page.getTotalElements());

		log.info("OfferServiceImpl :: Exit getReadyToRelease");

		return ApiResponse.success(

				ResponseCode.SUCCESS,

				"Ready To Release fetched successfully",

				response

		);

	}

	private Map<String, Object> convertToMap(OfferDetailsEntity offer) {

		JobApplicationEntity application = offer.getJobApplication();

		CreateJobDetailsEntity job = createJobDetailsRepository.findByJobId(application.getJobId());

		DepartmentsEntity department = departmentsRepository.findById(job.getDepartmentId()).orElse(null);

		Map<String, Object> map = new LinkedHashMap<>();

		map.put("offerId", offer.getId());

		map.put("applicationId", application.getId());

		map.put("candidateName", application.getFirstName() + " " + application.getLastName());

		map.put("email", application.getEmail());

		map.put("jobTitle", job.getJobTitle());

		map.put("department", department != null ? department.getDepartmentName() : "");

//	map.put("recruiterName", application.getRecuriterName());

		map.put("approvedOn", offer.getFinalApprovalTime());

		map.put("priority", calculatePriority(offer.getFinalApprovalTime()));

		return map;

	}

	private String calculatePriority(LocalDateTime finalApprovalTime) {

		long days = ChronoUnit.DAYS.between(finalApprovalTime.toLocalDate(), LocalDate.now());

		if (days >= 5) {
			return "High";
		}

		if (days >= 3) {
			return "Medium";
		}

		return "Low";
	}
	
	@Override
	public ApiResponse<?> getOfferDetailsByApplicantId(Integer applicantId) {

		try {

			Optional<JobApplicationEntity> applicantOptional = jobApplicationRepository.findById(applicantId);

			if (applicantOptional.isEmpty()) {
				return ApiResponse.failure(ResponseCode.FAILURE, "Applicant not found");
			}

			JobApplicationEntity applicant = applicantOptional.get();

			OfferDetailsResponse response = new OfferDetailsResponse();

			// Applicant Details
			response.setApplicantId(applicant.getId());
			response.setCandidateName(applicant.getFirstName());
			response.setEmail(applicant.getEmail());

			// Job Details
			CreateJobDetailsEntity jobDetails = createJobDetailsRepository.findByJobId(applicant.getJobId());

			response.setJobTitle(jobDetails.getJobTitle());
			response.setEmploymentType(jobDetails.getEmploymentType());
			response.setWorkLocation(jobDetails.getLocation());
			// Department
			Optional<DepartmentsEntity> departmentOptional = departmentsRepository.findById(jobDetails.getDepartmentId());

			if (departmentOptional.isPresent()) {
				response.setDepartment(departmentOptional.get().getDepartmentName());
			}

			// Budget & Compensation
			Optional<BudgetAndCompensationEntity> budgetAndCompensationEntity = budgetAndCompensationRepository
					.findBySrId(jobDetails.getSrId());

			if (budgetAndCompensationEntity.isPresent()) {

				BudgetAndCompensationEntity budget = budgetAndCompensationEntity.get();

				response.setBasicSalary(budget.getProposedTotalCompensation());

				response.setSigningBonus(
						Boolean.TRUE.equals(budget.getSigningBonus()) ? budget.getSigningBonusAmount() : 0);

				response.setAnnualRsuEsopValue(Boolean.TRUE.equals(budget.getEquity()) ? budget.getEquityAmount() : 0);

				response.setOtherBenefits(
						Boolean.TRUE.equals(budget.getRelocationBudget()) ? budget.getRelocationBudgetAmount() : 0);

				response.setTotalCtc(budget.getProposedTotalCompensation());

				response.setOfferedCtc(budget.getAnnualHiringCost());
				response.setMinSalary(budget.getMinimumSalary());
				response.setMaxSalary(budget.getMaximumSalary());
			}

			// Offer Details
			Optional<OfferDetailsEntity> offerOptional = offerDetailsRepository.findByJobApplicationId(applicantId);

			if (offerOptional.isPresent()) {

				OfferDetailsEntity offer = offerOptional.get();

				response.setRecruiter(offer.getRecruitedBy());
				response.setRequestedOn(
						offer.getInterviewCompletionDate() != null ? offer.getInterviewCompletionDate().toLocalDate()
								: null);

				response.setProbationPeriod(offer.getProbationPeriod());
				response.setNoticePeriod(offer.getNoticePeriod());

			
			}

			return ApiResponse.success(ResponseCode.SUCCESS, "Offer details fetched successfully", response);

		} catch (Exception e) {
			log.error("OfferDetailsServiceImpl :: Error while fetching offer details", e);
			return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
		}
	}
	@Override
	public ApiResponse<?> getOfferComments(Integer applicantId) {

	    try {

	        Optional<OfferDetailsEntity> offerDetailsEntity = offerDetailsRepository.findByJobApplicationId(applicantId);

	        if (offerDetailsEntity == null) {
	            return ApiResponse.failure(ResponseCode.FAILURE, "Offer details not found");
	        }
	        OfferDetailsEntity offer =offerDetailsEntity.get();
	        List<OfferCommentsResponse> responseList = new ArrayList<>();

	        // Approver 1
	        if (offer.getApprover1By() != null) {

	            OfferCommentsResponse response = new OfferCommentsResponse();
	            response.setRole(offer.getApprover1Role());
	            response.setApproverName(offer.getApprover1By());
	            response.setApproved(offer.getApprover1());
	            response.setApprovedOn(offer.getFinalApprovalTime());
	            response.setComments(offer.getApprover1_comments());

	            responseList.add(response);
	        }

	        // Approver 2
	        if (offer.getApprover2By() != null) {

	            OfferCommentsResponse response = new OfferCommentsResponse();
	            response.setRole(offer.getApprover2Role());
	            response.setApproverName(offer.getApprover2By());
	            response.setApproved(offer.getApprover2());
	            response.setApprovedOn(offer.getFinalApprovalTime());
	            response.setComments(offer.getApprover2_comments());

	            responseList.add(response);
	        }

	        // Approver 3
	        if (offer.getApprover3By() != null) {

	            OfferCommentsResponse response = new OfferCommentsResponse();
	            response.setRole(offer.getApprover3Role());
	            response.setApproverName(offer.getApprover3By());
	            response.setApproved(offer.getApprover3());
	            response.setApprovedOn(offer.getFinalApprovalTime());
	            response.setComments(offer.getApprover3_comments());

	            responseList.add(response);
	        }

	        return ApiResponse.success(ResponseCode.SUCCESS,
	                "Offer comments fetched successfully", responseList);

	    } catch (Exception e) {
	        log.error("OfferDetailsServiceImpl :: getOfferComments", e);
	        return ApiResponse.failure(ResponseCode.FAILURE, e.getMessage());
	    }
	}
}
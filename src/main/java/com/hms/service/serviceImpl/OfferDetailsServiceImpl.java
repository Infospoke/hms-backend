package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.DepartmentsEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.OfferDetailsEntity;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.DepartmentsRepository;
import com.hms.service.repository.OfferDetailsRepository;
import com.hms.service.request.ReleaseOfferRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.response.RaiseOfferRequestResponse;
import com.hms.service.service.IOfferDetailsService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OfferDetailsServiceImpl implements IOfferDetailsService {
	@Autowired
	private DepartmentsRepository departmentsRepository;

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private OfferDetailsRepository offerDetailsRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private HttpServletRequest httpServletRequest;

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

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("offers", offers);

		response.put("currentPage", page.getNumber());

		response.put("totalPages", page.getTotalPages());

		response.put("totalElements", page.getTotalElements());

		log.info("OfferServiceImpl :: Exit getReadyToRelease");

		return ApiResponse.success(

				ResponseCode.SUCCESS,

				"Ready To Release Offer letters list fetched successfully",

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

		map.put("recruiterName", offer.getRecruitedBy());

		map.put("finalApprovalTime", offer.getFinalApprovalTime());

		map.put("priority", calculatePriority(offer.getFinalApprovalTime()));
		
		map.put("totalCtc", offer.getTotalCtc());

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
	public ApiResponse<?> getAllRaiseOfferRequests(SpecificationFilterRequest request) {

	    log.info("OfferDetailsServiceImpl :: Inside getAllRaiseOfferRequests");

		Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Specification<OfferDetailsEntity> specification = request.buildRaiseOfferRequestSpecification();

		Page<OfferDetailsEntity> offerPage = offerDetailsRepository.findAll(specification, pageable);

	    List<RaiseOfferRequestResponse> responseList = new ArrayList<>();

	    for (OfferDetailsEntity offer : offerPage.getContent()) {

	        JobApplicationEntity application = offer.getJobApplication();

	        CreateJobDetailsEntity job =
	                createJobDetailsRepository.findByJobId(application.getJobId());

	        DepartmentsEntity department = null;

	        if(job != null){

	            department = departmentsRepository
	                    .findById(job.getDepartmentId())
	                    .orElse(null);
	        }

			RaiseOfferRequestResponse response = new RaiseOfferRequestResponse();

	        response.setOfferId(offer.getId());

	        response.setApplicantId(application.getId());

			response.setCandidateName(application.getFirstName() + " " + application.getLastName());

	        response.setCandidateEmail(application.getEmail());

	        response.setPhoneNumber(application.getPhNo());

	        if(job != null){

	            response.setJobId(job.getJobId());

	            response.setJobTitle(job.getJobTitle());

	        }
			if (department != null) {

				response.setDepartmentName(department.getDepartmentName());

			}
			response.setMovedToHireOn(offer.getInterviewCompletionDate());

			response.setRecruiter(offer.getRecruitedBy());
			
			response.setPriority(calculatedOfferRaiseRequestPriority(offer.getInterviewCompletionDate()));

			responseList.add(response);

	    }

	    Map<String, Object> response = new HashMap<>();

	    response.put("content", responseList);
	    
	    response.put("page", offerPage.getNumber());
	    
	    response.put("size", offerPage.getSize());
	    
	    response.put("totalElements", offerPage.getTotalElements());
	    
	    response.put("totalPages", offerPage.getTotalPages());
	    
	    response.put("last", offerPage.isLast());

	    log.info("OfferDetailsServiceImpl :: Exit getAllRaiseOfferRequests");

		return ApiResponse.success(ResponseCode.SUCCESS, "Raise Offer Requests fetched successfully", response);
	}
	
	private String calculatedOfferRaiseRequestPriority(LocalDateTime interviewCompletionDate) {

		long days = ChronoUnit.DAYS.between(interviewCompletionDate.toLocalDate(), LocalDate.now());

		if (days >= 3) {
			return "High";
		}

		if (days >= 2) {
			return "Medium";
		}

		return "Low";
	}

		
	@Transactional
	public ApiResponse<?> releaseOfferLetters(ReleaseOfferRequest request) {

		log.info("OfferDetailsServiceImpl :: releaseOfferLetters");

		String authHeader = httpServletRequest.getHeader("Authorization");

		Long userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			userId = jwtService.extractUserId(token);
		}

		if (request.getApplicationIds() == null || request.getApplicationIds().isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure",
					Collections.singletonList("Application Ids are required"));
		}

		List<OfferDetailsEntity> offers = offerDetailsRepository.findByJobApplication_IdIn(request.getApplicationIds());

		if (offers.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", Collections.singletonList("No offers found"));
		}

		for (OfferDetailsEntity offer : offers) {
			offer.setOfferReleased(true);
			offer.setOfferReleasedBy(userId);
			offer.setOfferReleasedAt(LocalDateTime.now());
		}

		offerDetailsRepository.saveAll(offers);

		return ApiResponse.success(ResponseCode.SUCCESS, "success", "Offer letters released successfully");
	}
}
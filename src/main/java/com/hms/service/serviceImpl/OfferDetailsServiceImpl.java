package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import com.hms.service.request.SpecificationFilterRequest;
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
	private OfferDetailsRepository offerDetailsRepository;

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
}
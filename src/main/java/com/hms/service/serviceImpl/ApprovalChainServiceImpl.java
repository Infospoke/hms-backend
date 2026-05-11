package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.FunctionalityEntity;
import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.request.ApprovalChainRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.request.UpdateApprovalChainRequest;
import com.hms.service.response.ApprovalChainResponse;
import com.hms.service.service.IApprovalChainService;
import com.hms.service.utils.JwtService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class ApprovalChainServiceImpl implements IApprovalChainService {

	@Autowired
	private ApprovalChainRepository approvalChainRepository;

	@Autowired
	private FunctionalityRepository functionalityRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private HttpServletRequest httpServletRequest;
	
	
	@Override
	public ApiResponse<?> getApprovalChainsList(SpecificationFilterRequest request) {

		log.info("ApprovalChainServiceImpl:: Inside getApprovalChainsList");

		
		if (request.getPage() == null || request.getSize() == null) {

			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("page and size must be provided"));
		}

		if (request.getPage() < 0 || request.getSize() <= 0) {

			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Invalid page or size values"));
		}

		Sort sort = Sort.by(

				"DESC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,

				request.getSortBy() != null ? request.getSortBy() : "id");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		Specification<ApprovalChainEntity> baseSpec = request.buildBaseSpec();

		Page<ApprovalChainEntity> pageResult = approvalChainRepository.findAll(baseSpec, pageable);

		List<ApprovalChainResponse> responseList = pageResult.getContent().stream()
				.map(entity -> new ApprovalChainResponse(

						entity.getId(),

						entity.getChainName(),

						entity.getDescription(),

						entity.getStatus(),

						entity.getLevelConfig() != null ? entity.getLevelConfig().size() : 0,

						entity.getUpdatedBy(),

						entity.getUpdatedAt(),

						entity.getCreatedAt(),

						entity.getCreatedBy(),

						entity.getApproval(),

						entity.getLevelConfig(),

						entity.getFunctionality(),

						entity.getFunctionalityName()))
				.toList();

		Specification<ApprovalChainEntity> countSpec = request.buildCountSpec();

		long totalCount = approvalChainRepository.count(countSpec);

		long approvedCount = approvalChainRepository.count(countSpec.and(approvalEquals("APPROVED")));

		long rejectedCount = approvalChainRepository.count(countSpec.and(approvalEquals("REJECTED")));

		long inProgressCount = approvalChainRepository.count(countSpec.and(approvalEquals("IN_PROGRESS")));

		long activeCount = approvalChainRepository.count(countSpec.and(statusEquals("ACTIVE")));

		long deactiveCount = approvalChainRepository.count(countSpec.and(statusEquals("DEACTIVE")));

		Map<String, Object> counts = new LinkedHashMap<>();

		counts.put("total", totalCount);

		counts.put("approved", approvedCount);

		counts.put("rejected", rejectedCount);

		counts.put("inProgress", inProgressCount);

		counts.put("active", activeCount);

		counts.put("deactive", deactiveCount);

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("approvalChains", responseList);

		response.put("currentPage", pageResult.getNumber());

		response.put("totalPages", pageResult.getTotalPages());

		response.put("totalElements", pageResult.getTotalElements());

		response.put("counts", counts);

		log.info("ApprovalChainServiceImpl:: Exit getApprovalChainsList");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	private Specification<ApprovalChainEntity> approvalEquals(String value) {

		return (r, q, c) -> c.equal(c.lower(r.get("approval")), value.toLowerCase());
	}

	private Specification<ApprovalChainEntity> statusEquals(String value) {

		return (r, q, c) -> c.equal(c.lower(r.get("status")), value.toLowerCase());
	}

	@Override
	public ApiResponse<?> getApprovalChainCounts() {

		log.info("ApprovalChainServiceImpl:: Inside getApprovalChainCounts");

		Long total = approvalChainRepository.count();
		Long approved = approvalChainRepository.countByApprovalIgnoreCase("APPROVED");
		Long rejected = approvalChainRepository.countByApprovalIgnoreCase("REJECTED");
		Long pending = approvalChainRepository.countByApprovalIgnoreCase("IN_PROGRESS");

		Long active = approvalChainRepository.countByStatusIgnoreCase("ACTIVE");
		Long deactive = approvalChainRepository.countByStatusIgnoreCase("DEACTIVE");

		Map<String, Object> response = new HashMap<>();

		response.put("total", total);
		response.put("approved", approved);
		response.put("pending", pending);
		response.put("rejected", rejected);

		response.put("active", active);
		response.put("deactive", deactive);

		log.info("ApprovalChainServiceImpl:: Exit getApprovalChainCounts");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	public ApiResponse<?> getApprovalChainById(Integer id) {

		log.info("ApprovalChainServiceImpl:: Inside getApprovalChainById with id: {}", id);

		if (id == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Id must not be null"));
		}

		ApprovalChainEntity entity = approvalChainRepository.findById(id).orElse(null);

		if (entity == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure",
					List.of("Approval Chain not found with id: " + id));
		}

		ApprovalChainResponse response = new ApprovalChainResponse();
		BeanUtils.copyProperties(entity, response);

		response.setLevels(entity.getLevelConfig() != null ? entity.getLevelConfig().size() : 0);

		response.setLevelConfig(entity.getLevelConfig());
		if (entity.getFunctionality() != null) {

			Optional<FunctionalityEntity> functionalityOptional = functionalityRepository
					.findById(entity.getFunctionality());

			if (functionalityOptional.isPresent()) {

				response.setFunctionalityName(functionalityOptional.get().getFunctionalityName());
			}
		}

		log.info("ApprovalChainServiceImpl:: Exit getApprovalChainById");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}

	@Override
	public ApiResponse<?> createApprovalChain(ApprovalChainRequest request) {

		log.info("ApprovalChainServiceImpl::Inside the createApprovalChain method");

		ApprovalChainEntity chainName = approvalChainRepository.findByChainNameIgnoreCase(request.getChainName());
		if (chainName != null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Chain Name already exists");

		}
		ApprovalChainEntity approvalChainEntity = new ApprovalChainEntity();

		approvalChainEntity.setChainName(request.getChainName());
		approvalChainEntity.setDescription(request.getDescription());
		approvalChainEntity.setStatus(request.getStatus());
		if (functionalityRepository.existsById(request.getFunctionality())) {
			approvalChainEntity.setFunctionality(request.getFunctionality());
		} else {
			log.info("BusinessUnit Id is required");
			return ApiResponse.failure(ResponseCode.FAILURE, "Failure", List.of("Functionality is not matched"));
		}

		String authHeader = httpServletRequest.getHeader("Authorization");
		String userName = "";
		String roleName = "";
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			userName = jwtService.extractUsernameFromClaims(token);
			roleName = jwtService.extractRole(token);

		}
		log.info("The username is :" + userName);
		log.info("the role name is :" + roleName);
		approvalChainEntity.setCreatedBy(userName);
		approvalChainEntity.setLevelConfig(request.getLevelConfig());
		approvalChainEntity.setApproval("In_Progress");

		approvalChainEntity.setCreatedAt(LocalDate.now());

		approvalChainRepository.save(approvalChainEntity);

		Optional<FunctionalityEntity> functionalityEntity = functionalityRepository
				.findById(request.getFunctionality());
		FunctionalityEntity functionality = functionalityEntity.get();
		functionality.setIsChaincreated(true);
		functionalityRepository.save(functionality);

		log.info("ApprovalChainServiceImpl::Exit from the createApprovalChain method");
		return ApiResponse.success("Approval Chain Created Successfully");
	}

	@Override
	public ApiResponse<?> updateApprovalChain(UpdateApprovalChainRequest request) {

		log.info("ApprovalChainServiceImpl::Inside the updateApprovalChain method");
		Optional<ApprovalChainEntity> approvalEntity = approvalChainRepository.findById(request.getId());
		if (approvalEntity.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Approval Chain not found");
		}

		ApprovalChainEntity approvalChainEntity = approvalEntity.get();

		String authHeader = httpServletRequest.getHeader("Authorization");
		String userName = "";
		String roleName = "";

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			userName = jwtService.extractUsernameFromClaims(token);
			roleName = jwtService.extractRole(token);
		}
		log.info("the role name is :" + roleName);
		if (roleName == null || !roleName.equalsIgnoreCase("Administrator")) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Only Administrator can update Approval Chain");
		}

		if (request.getStatus() != null) {

			String status = request.getStatus().trim().toUpperCase();

			if ("ACTIVE".equals(status)) {

				approvalChainEntity.setStatus(status);
				approvalChainEntity.setActivateComments(request.getActivateComments());

				approvalChainEntity.setDeactivateComments(null);

			} else if ("DEACTIVE".equals(status)) {

				approvalChainEntity.setStatus(status);
				approvalChainEntity.setDeactivateComments(request.getDeactivateComments());

				approvalChainEntity.setActivateComments(null);
			}
		}

		if (request.getApproval() != null) {

			String approval = request.getApproval().trim().toUpperCase();

			if ("APPROVED".equals(approval)) {

				approvalChainEntity.setApproval(approval);
				approvalChainEntity.setApprovedComments(request.getApprovedComments());

				approvalChainEntity.setRejectedComments(null);

			} else if ("REJECTED".equals(approval)) {

				approvalChainEntity.setApproval(approval);
				approvalChainEntity.setRejectedComments(request.getRejectedComments());

				approvalChainEntity.setApprovedComments(null);
			}
		}

		approvalChainEntity.setUpdatedBy(userName);
		approvalChainEntity.setUpdatedAt(LocalDate.now());

		approvalChainRepository.save(approvalChainEntity);

		log.info("ApprovalChainServiceImpl::Exit from the updateApprovalChain method");

		return ApiResponse.success("Approval Chain Updated Successfully");
	}
}

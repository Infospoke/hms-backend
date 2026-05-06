package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.entity.FunctionalityEntity;
import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.request.ApprovalChainRequest;
import com.hms.service.request.FilterRequest;
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
	public ApiResponse<?> getApprovalChainsList(FilterRequest request) {

		log.info("ApprovalChainServiceImpl:: Inside getApprovalChainsList");

		if (request.getPage() == null || request.getSize() == null) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("page and size must be provided"));
		}

		if (request.getPage() < 0 || request.getSize() <= 0) {
			return ApiResponse.failure(ResponseCode.FAILURE, "failure", List.of("Invalid page or size values"));
		}

		Sort sort = Sort.by("DESC".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
				request.getSortBy() != null ? request.getSortBy() : "id");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

		String status = null;
		String chainName = null;
		String approval=null;
		LocalDate fromDate = null;
		LocalDate toDate = null;


		if (request.getFilters() != null) {

			if (request.getFilters().containsKey("status")) {
				status = request.getFilters().get("status").toString();
			}
			
			if (request.getFilters().containsKey("approval")) {
				approval = request.getFilters().get("approval").toString();
			}

			if (request.getFilters().containsKey("chainName")) {
				chainName = request.getFilters().get("chainName").toString();
			}

			if (request.getFilters().containsKey("dateFilter")) {

			    String dateFilter = request.getFilters().get("dateFilter").toString();
			    LocalDate today = LocalDate.now();

			    switch (dateFilter.toUpperCase()) {

			        case "TODAY":
			            fromDate = today;
			            toDate = today;
			            break;

			        case "LAST_WEEK":
			            fromDate = today.minusWeeks(1);
			            toDate = today;
			            break;

			        case "LAST_MONTH":
			            fromDate = today.minusMonths(1);
			            toDate = today;
			            break;

			        case "CUSTOM":
			            if (request.getFilters().containsKey("fromDate") &&
			                request.getFilters().containsKey("toDate")) {

			                fromDate = LocalDate.parse(request.getFilters().get("fromDate").toString());
			                toDate = LocalDate.parse(request.getFilters().get("toDate").toString());
			            }
			            break;
			    }
			}
		}

		log.info("Fetching approval chains with status: {}, chainName: {},approval: {}", status, chainName,approval);

		Page<ApprovalChainEntity> pageResult;

		if (fromDate != null && toDate != null) {

		    if (status != null && chainName != null && approval != null) {
		        pageResult = approvalChainRepository
		                .findByStatusIgnoreCaseAndChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCaseAndCreatedAtBetween(
		                        status, chainName, approval, fromDate, toDate, pageable);

		    } else if (status != null && approval != null) {
		        pageResult = approvalChainRepository
		                .findByStatusIgnoreCaseAndApprovalContainingIgnoreCaseAndCreatedAtBetween(
		                        status, approval, fromDate, toDate, pageable);

		    } else if (chainName != null && approval != null) {
		        pageResult = approvalChainRepository
		                .findByChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCaseAndCreatedAtBetween(
		                        chainName, approval, fromDate, toDate, pageable);

		    } else if (approval != null) {
		        pageResult = approvalChainRepository
		                .findByApprovalContainingIgnoreCaseAndCreatedAtBetween(
		                        approval, fromDate, toDate, pageable);

		    } else if (status != null && chainName != null) {
		        pageResult = approvalChainRepository
		                .findByStatusIgnoreCaseAndChainNameContainingIgnoreCaseAndCreatedAtBetween(
		                        status, chainName, fromDate, toDate, pageable);

		    } else if (status != null) {
		        pageResult = approvalChainRepository
		                .findByStatusContainingIgnoreCaseAndCreatedAtBetween(
		                        status, fromDate, toDate, pageable);

		    } else if (chainName != null) {
		        pageResult = approvalChainRepository
		                .findByChainNameContainingIgnoreCaseAndCreatedAtBetween(
		                        chainName, fromDate, toDate, pageable);

		    } else {
		        pageResult = approvalChainRepository
		                .findByCreatedAtBetween(fromDate, toDate, pageable);
		    }

		} else {


		    if (status != null && chainName != null && approval != null) {
		        pageResult = approvalChainRepository
		                .findByStatusIgnoreCaseAndChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCase(
		                        status, chainName, approval, pageable);

		    } else if (status != null && approval != null) {
		        pageResult = approvalChainRepository
		                .findByStatusIgnoreCaseAndApprovalContainingIgnoreCase(status, approval, pageable);

		    } else if (chainName != null && approval != null) {
		        pageResult = approvalChainRepository
		                .findByChainNameContainingIgnoreCaseAndApprovalContainingIgnoreCase(chainName, approval, pageable);

		    } else if (approval != null) {
		        pageResult = approvalChainRepository
		                .findByApprovalContainingIgnoreCase(approval, pageable);

		    } else if (status != null && chainName != null) {
		        pageResult = approvalChainRepository
		                .findByStatusIgnoreCaseAndChainNameContainingIgnoreCase(status, chainName, pageable);

		    } else if (status != null) {
		        pageResult = approvalChainRepository
		                .findByStatusContainingIgnoreCase(status, pageable);

		    } else if (chainName != null) {
		        pageResult = approvalChainRepository
		                .findByChainNameContainingIgnoreCase(chainName, pageable);

		    } else {
		        pageResult = approvalChainRepository.findAll(pageable);
		    }
		}

		List<ApprovalChainResponse> responseList = pageResult.getContent().stream()
				.map(entity -> new ApprovalChainResponse(entity.getId(), entity.getChainName(), entity.getDescription(),
						entity.getStatus(), entity.getLevelConfig() != null ? entity.getLevelConfig().size() : 0, entity.getUpdatedBy(),entity.getUpdatedAt(),
						entity.getCreatedAt(), entity.getCreatedBy(), entity.getApproval(),entity.getLevelConfig(), entity.getFunctionality()))
				.toList();
		Map<String, Object> response = new HashMap<>();
		response.put("approvalChains", responseList);
		response.put("currentPage", pageResult.getNumber());
		response.put("totalPages", pageResult.getTotalPages());
		response.put("totalElements", pageResult.getTotalElements());

		log.info("ApprovalChainServiceImpl:: Exit getApprovalChainsList");

		return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
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

	    response.put("total",total);
	    response.put("approved", approved);
	    response.put("pending", pending);
	    response.put("rejected",rejected);
	   

	    response.put("active", active);
	    response.put("deactive", deactive);

	    log.info("ApprovalChainServiceImpl:: Exit getApprovalChainCounts");

	    return ApiResponse.success(ResponseCode.SUCCESS, "success", response);
	}
	
	@Override
	public ApiResponse<?> getApprovalChainById(Integer id) {

	    log.info("ApprovalChainServiceImpl:: Inside getApprovalChainById with id: {}", id);

	    if (id == null) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "failure",
	                List.of("Id must not be null")
	        );
	    }

	    ApprovalChainEntity entity = approvalChainRepository.findById(id).orElse(null);

	    if (entity == null) {
	        return ApiResponse.failure(
	                ResponseCode.FAILURE,
	                "failure",
	                List.of("Approval Chain not found with id: " + id)
	        );
	    }

	    ApprovalChainResponse response = new ApprovalChainResponse();
	    BeanUtils.copyProperties(entity, response);

	    response.setLevels(
	            entity.getLevelConfig() != null ? entity.getLevelConfig().size() : 0
	    );

	    response.setLevelConfig(entity.getLevelConfig());

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

		Optional<FunctionalityEntity> functionalityEntity = functionalityRepository.findById(request.getFunctionality());
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
			return ApiResponse.failure(ResponseCode.FAILURE,"Approval Chain not found");
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
		if (roleName == null || !roleName.equalsIgnoreCase("Adminstrator")) {
			return ApiResponse.failure(ResponseCode.FAILURE, "Only Administrator can update Approval Chain");
		}

		approvalChainEntity.setApproval(request.getApproval());
		approvalChainEntity.setStatus(request.getStatus());
		approvalChainEntity.setApprovedComments(request.getApprovedComments());
		approvalChainEntity.setRejectedComments(request.getRejectedComments());

		approvalChainEntity.setUpdatedBy(userName);
		approvalChainEntity.setUpdatedAt(LocalDate.now());

		approvalChainRepository.save(approvalChainEntity);

		log.info("ApprovalChainServiceImpl::Exit from the updateApprovalChain method");

		return ApiResponse.success("Approval Chain Updated Successfully");
	}
}


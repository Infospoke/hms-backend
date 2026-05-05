package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.entity.ApprovalChainEntity;
import com.hms.service.repository.ApprovalChainRepository;
import com.hms.service.repository.FunctionalityRepository;
import com.hms.service.request.ApprovalChainRequest;
import com.hms.service.request.UpdateApprovalChainRequest;
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

		log.info("ApprovalChainServiceImpl::Exit from the createApprovalChain method");
		return ApiResponse.success("Approval Chain Created Successfully");
	}

	@Override
	public ApiResponse<?> updateApprovalChain(UpdateApprovalChainRequest request) {

		log.info("ApprovalChainServiceImpl::Inside the updateApprovalChain method");
		Optional<ApprovalChainEntity> approvalEntity = approvalChainRepository.findById(request.getId());
		if (approvalEntity.isEmpty()) {
			return ApiResponse.failure(ResponseCode.FAILURE,  "Approval Chain not found");
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

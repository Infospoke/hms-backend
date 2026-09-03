package com.hms.service.serviceImpl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.service.entity.ClientManagementDetailsEntity;
import com.hms.service.repository.ClientRepository;
import com.hms.service.request.ClientRequest;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.IClientService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

	private final ClientRepository clientRepository;

	@Override
	@Transactional
	public ApiResponse<?> createClient(ClientRequest request) {

		ClientManagementDetailsEntity client = new ClientManagementDetailsEntity();

		client.setClientName(request.getClientName());
		client.setIndustry(request.getIndustry());
		client.setTeamSize(request.getTeamSize());
		client.setClientStatus(request.getClientStatus());
		client.setAgreementStatus(request.getAgreementStatus());
		client.setAgreementStartDate(request.getAgreementStartDate());
		client.setAgreementEndDate(request.getAgreementEndDate());
		client.setBdm(request.getBdm());
		client.setBusinessProposed(request.getBusinessProposed());
		client.setClientManager(request.getClientManager());
		client.setDesignation(request.getDesignation());
		client.setContactNo(request.getContactNo());
		client.setEmail(request.getEmail());
		client.setLocation(request.getLocation());

		// POC is List<PocConfig>
		client.setPoc(request.getPoc());

		client.setRemarks(request.getRemarks());

		clientRepository.save(client);

		return ApiResponse.success(ResponseCode.SUCCESS, "Client Added successfully", "Success");
	}

	@Override
	public ApiResponse<?> getClientList(SpecificationFilterRequest request) {

		Sort.Direction direction;

		try {
			direction = Sort.Direction.valueOf(request.getDirection().toUpperCase());
		} catch (Exception e) {
			direction = Sort.Direction.DESC;
		}

		String sortBy = request.getSortBy();

		if (sortBy == null || sortBy.isBlank()) {
			sortBy = "id";
		}

		int page = request.getPage();
		int size = request.getSize();

		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

		Page<ClientManagementDetailsEntity> clientPage = clientRepository.findAll(request.buildClientSpecification(),
				pageable);

		Map<String, Object> response = new LinkedHashMap<>();

		response.put("content", clientPage.getContent());
		response.put("totalPages", clientPage.getTotalPages());
		response.put("totalElements", clientPage.getTotalElements());

		return ApiResponse.success(ResponseCode.SUCCESS, "Client list fetched successfully", response);
	}
}
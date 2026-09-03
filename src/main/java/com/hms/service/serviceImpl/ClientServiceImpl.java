package com.hms.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.service.entity.ClientManagementDetailsEntity;
import com.hms.service.repository.ClientRepository;
import com.hms.service.request.ClientRequest;
import com.hms.service.service.IClientService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

	@Autowired
    private  ClientRepository clientRepository;


    @Override
    @Transactional
    public ApiResponse<?>createClient(ClientRequest request) {

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
        client.setRemarks(request.getRemarks());

        client.setPoc(request.getPoc());
          clientRepository.save(client);
          
          
          return ApiResponse.success(ResponseCode.SUCCESS, "Client Added successfully","Success");
    }
    
    
    
}

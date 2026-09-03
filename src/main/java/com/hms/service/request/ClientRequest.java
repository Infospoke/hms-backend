package com.hms.service.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientRequest {

    private String clientName;

    private String industry;

    private Integer teamSize;

    private String clientStatus;

    private String agreementStatus;

    private LocalDate agreementStartDate;

    private LocalDate agreementEndDate;

    private String bdm;

    private String businessProposed;

    private String clientManager;

    private String designation;

    private String contactNo;

    private String email;
    
    private Integer id;

    private String location;

    @Valid
    private List<PocConfig> poc;

    private String remarks;
}
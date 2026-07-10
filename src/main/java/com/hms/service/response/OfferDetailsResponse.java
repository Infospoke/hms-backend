package com.hms.service.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferDetailsResponse {

   
    private Integer applicantId;
    private String candidateName;
    private String email;
    private String jobTitle;
    private String department;
    private String recruiter;
    private LocalDate requestedOn;

  
    private Integer basicSalary;
    private Integer signingBonus;
    private Integer annualRsuEsopValue;
    private Integer otherBenefits;
    private Integer totalCtc;
    private Long minSalary;
    private Long maxSalary;
    
   
    private String employmentType;
    private Long offeredCtc;
    private String probationPeriod;
    private String noticePeriod;
    private String workLocation;
}

package com.hms.service.response;

import java.time.LocalDate;
import java.util.List;

import com.hms.service.request.FinanceRecommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReReleaseOfferDetailsResponse {

    private Integer offerId;

    private String candidateId;

    private String candidateName;

    private String email;

    private String jobTitle;

    private String departmentName;

    private String employmentType;

    private String location;

    private String probationPeriod;

    private Long totalCtc;

    private LocalDate joiningDate;

    private LocalDate offerValidity;

    private List<FinanceRecommendation> financeRecommendations;

}

package com.hms.service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourcingStrategyResponse {

    private Integer id;
    private String srId;

    private Boolean internalBoard;
    private Boolean naukri;
    private Boolean linkedIn;
    private Boolean indeed;
    private Boolean companySite;
    private Boolean agencyRpo;

    private Boolean internalFirstPolicy;
    private Integer sourcingBudget;

    private Boolean referralEnabled;
    private Double referralAmount;

    private Boolean diversityEnabled;
    private List<String> diversityTags;

    private Boolean draft;
    private Boolean submitted;
    private Boolean approved;
}

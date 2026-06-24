package com.hms.service.dto;

import com.hms.service.response.ApplicantsCountResponse;
import com.hms.service.response.JobDescriptionResponse;
import com.hms.service.response.JobOverviewResponse;
import com.hms.service.response.RecruitersResponse;
import com.hms.service.response.SourcingChannelResponse;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCreationDetailsResponseDto {

    private JobOverviewResponse jobOverview;

    private JobDescriptionResponse jobDescription;

    private SourcingChannelResponse sourcingStrategy;

    private RecruitersResponse recruiters;
    
    private ApplicantsCountResponse applicantsCount;
}
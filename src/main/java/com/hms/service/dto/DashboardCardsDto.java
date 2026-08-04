package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardCardsDto {

    private Long myApprovedSRs;

    private Long activeCandidates;

    private Integer totalOpenings;

    private Integer yetToFill;

    private Integer inProgress;
    
    private Integer filled;
    
    private Integer my;
    
    private Integer team;

}
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

    private Integer inProgress=0;
    
    private Integer filled=0;
    
    private Integer my=0;
    
    private Integer team=0;

}
package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterDashboardCountResponse {

    private Long totalAssignments;

    private Long acceptedAssignments;

    private Long rejectedAssignments;

    private Long applicationsAdded;

    private Long offersReleased;

    private Long hired;

    private Long onTrack;

    private Long atRisk;

    private Long overdue;
    
    private Double slaCompliance;

}
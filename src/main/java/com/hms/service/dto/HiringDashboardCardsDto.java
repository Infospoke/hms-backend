package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HiringDashboardCardsDto {

    private Long openSrs;

    private Long totalCandidates;

    private Long interviews;

    private Long offers;

    private Long averageHiringAge;

}
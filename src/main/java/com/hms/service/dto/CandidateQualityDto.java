package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateQualityDto {

    private Long excellent;

    private Long good;

    private Long average;

    private Long needsReview;

    private Long totalCandidates;

}

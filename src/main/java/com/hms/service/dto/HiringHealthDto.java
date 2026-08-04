package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HiringHealthDto {

	private Double pipelineCoverage;

	private Double offerProgress;

	private Double candidateQuality;

	private Double requisitionsOnTrack;

	private Double agingRequisitions;

}

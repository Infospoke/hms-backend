package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateSourcePerformanceDto {

	private String source;

	private Long applicantsAdded;

	private Long interviewed;

	private Long offered;

	private Long hired;

}
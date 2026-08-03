package com.hms.service.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HrRecommendationRequest {

	@NotNull
	private Integer applicantId;

	@NotNull
	private Long hrRecommendedCtc;

	@NotEmpty
	private List<HrRecommendation> hrRecommendations;
	
	private String hrReason;
	
	private LocalDate revisedJoiningDate;
}

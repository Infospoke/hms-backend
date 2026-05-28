package com.hms.service.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewPlanRequest {
	
	private Integer planId;
	
    @NotBlank(message = "Plan name is required")
    private String planName;

    private String description;

    @Valid
    @NotEmpty(message = "At least one round is required")
    private List<InterviewRoundRequest> rounds;

}

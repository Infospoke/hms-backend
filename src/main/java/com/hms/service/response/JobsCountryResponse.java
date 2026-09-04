package com.hms.service.response;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobsCountryResponse {
	private Integer jobId;
	private String location;
	private Integer minExperience;
	private Integer maxExperience;
	private String jobTitle;
	private String employmentType;
	private String jobSummary;
	private Integer openings;
	private LocalDateTime createdAt;
}
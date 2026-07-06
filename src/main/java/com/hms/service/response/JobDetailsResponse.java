package com.hms.service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDetailsResponse {
	
	private Integer jobId;
	
	private String jobCode;
	
	private String jobTitle;
	
	private Integer minExperience;
	
	private Integer maxExperience;
	
	private String Location;
	
	private List<String> skillsMustHave;
	
	private String modeType;

}

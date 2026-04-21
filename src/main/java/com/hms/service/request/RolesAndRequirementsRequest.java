package com.hms.service.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesAndRequirementsRequest {

	private List<String> skillsMustHave;

	
	private List<String> niceToHaveSkills;
 
	
	private String educationRequirement;

	private String travelRequirement;;


	private Integer minExperience;

	private Integer maxExperience;

	private Integer minInterviewRounds;

	private Integer maxInterviewRounds;

	private String certificationsRequired;

	private String languages;

	private Boolean assessmentRequired = false;

	

}

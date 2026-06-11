package com.hms.service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDescriptionDetailResponse {

	private String jobTitle;
	
	private String jobSummary;
	
	private List<String>  keyResponsibilities;
	
	private List<String> basicQaulifications;
	
	private List<String>  preferredQualifications;
	
	private List<String>  skillsMustHave;
	
	private List<String>  niceToHaveSkills;
	
	private String educationRequirements;
	
	private String experienceRequirements;
	
	private List<String> certificationsRequired;
	
	private List<String> languagesRequired;	
	
	private String workMode;
	
	private String employmentType;
	
	private String location;
	
	private String aboutCompany;

}

package com.hms.service.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobDetailsRequest {
	
    
	private String jobTitle;
	
	private Integer businessUnitId;
	
	private Integer departmentId;
	
	private String location;
	
	private String jobCode;
	
	private Integer openings;
	
	private LocalDate targetStartDate;
	
	private String workMode;
	
	private String employmentType;
	
	private String skillsMustHave;

	private String niceToHaveSkills;

	private Integer minExperience;

	private Integer maxExperience;

	private String additionalNotes;
	
    private String educationRequirement;
    
    private Boolean isOpen;
    
    private String country;
    
	private String certificationsRequired;
	
	private String languages;
	


}
package com.hms.service.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {
	 
	 private Integer jobId;

	 private String jobCode;
	 private String jobTitle;
	 private String jobType;
	 private String jobMode;
	 private String experience;
	 private String jobDescription;
	 private String jobRequirements;
	 private String qualification;
	 private String jobCountry;
	 private String jobLocation;
	 private String jobLevel;
	 private String jobInfo;
	 private String createdBy;
	 private String updatedBy;
	 private Boolean isOpen;
	 private List<JobSkillRequest> skills;
} 
	 
	   
	 
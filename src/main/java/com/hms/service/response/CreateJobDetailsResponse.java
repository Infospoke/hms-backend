package com.hms.service.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateJobDetailsResponse {

    private String srId;
    private String jobTitle;
    private Integer jobId;
    private Integer businessUnitId;
    private Integer departmentId;
    private String location;
    private String workMode;
    private String employmentType;
    private Integer openings;
    private LocalDate targetStartDate;
    private String skillsMustHave;
    private String niceToHaveSkills;
    private Integer minExperience;
    private Integer maxExperience;
	private String jobCode;
    private String educationRequirement;
    private String country;
   
}
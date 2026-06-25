package com.hms.service.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewDetailsResponse {

    private String candidateName;

    private String designation;

    private String totalExperience;

    private String currentCompany;
    
    private String jobTitle;
    
    private String department;
    
    private String InterviewType;
    
    private String InterviewMode;
    
    private Integer InterviewRound;
    
    private LocalDate scheduleTime;
    
    private String duration;
    
    private String InterviewFlatform;

    private List<InterviewExperienceResponse> experienceDetails;

    private List<InterviewProjectResponse> projectDetails;

}
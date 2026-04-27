package com.hms.service.response;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hms.service.request.JobSkillRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonInclude(Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobsResponse  {
 
 
 private Integer jobId;
 private String jobCode;
 private String jobLocation;
 private String experience;
 private String jobType;
 private String jobInfo;
 private String jobTitle;
 private String jobMode;
 private String jobLevel;
 private String jobDescription;
 private String jobRequirements;
 private String createdBy;
 private Boolean isOpen;
 private long applicantCount;
 private long interview;
 private List<JobSkillRequest> skills;
 private String qualification;
 private String jobCountry;
 private String responseCode;
 private String responseMessage;
 private long resumeCount;
 private long interviewCount;
 private long shortlisted;
 private long offerReleased;
 private long hiredCount;
 
}

package com.hms.service.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositonBasicsResponse {

    private Integer id;
    
    private String srId;

    private String jobTitle;
    
    private String businessUnitName;
    
    private Integer departmentId;
    
    private String departmentName;
    
    private List<Integer> reportingManagerInfo;

    private String location;
    
    private String seniorityLevelName;
    
    private Integer openings;
    
    private LocalDate targetStartDate;

    private String workMode;
    
    private String employmentType;
    
    private String priority;
    
    private Boolean approved;

    private LocalDateTime createdOn;
    
    private String createdBy;
    
    private Long userId;

    private Boolean approver1;

    private Boolean approver2;

    private Boolean approver3;

    private String approver1By;

    private String approver2By;

    private String approver3By;

    private LocalDateTime dateOfApproval1;

    private LocalDateTime dateOfApproval2;

    private LocalDateTime dateOfApproval3;
    
    private String commentsByApprover1;

    private String commentsByApprover2;

    private String commentsByApprover3;

    private String approver1Role;

    private String approver2Role;

    private String approver3Role;

	private LocalDateTime submittedOn;
    
    //private List<String>reportingManagerName;
    
    // private Boolean rejected;

    // private String rejectedBy;

    // private String currentStage;
    
    //  private Boolean inProgress;
    
    // private Boolean submitted;
    
    //  private Integer businessUnitId;
}

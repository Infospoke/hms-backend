package com.hms.service.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KanbanFilterResponse {


    private Integer id;
    
    private Integer jobId;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String phNo;
    
    private String source;
    
    private Boolean referral;
    
    private String currentStage;
    
    private LocalDateTime createdDate;
    
    private LocalDateTime stageEntryDate;
    
    private Long daysInStage;
    
    private Integer slaDays;
    
    private Double slaPercentage;
    
    private String slaColor;
    
    private String slaDisplay;

    private Boolean rejected;
}
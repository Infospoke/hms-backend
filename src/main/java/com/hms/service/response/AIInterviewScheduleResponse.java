package com.hms.service.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIInterviewScheduleResponse {

    private Integer applicationId;

    private String candidateName;

    private String email;

    private String jobTitle;

    private String interviewPlan;

    private String priority;

    private LocalDate dueDate;
}
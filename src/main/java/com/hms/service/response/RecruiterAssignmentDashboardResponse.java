package com.hms.service.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterAssignmentDashboardResponse {

    private Integer jobId;

    private String srId;

    private String jobTitle;

    private String assignmentStatus;

    private LocalDate acceptedOn;

    private String priority;

    private Integer requestedOpenings;

    private Integer filled;

    private Integer remaining;

    private LocalDate targetDate;

    private Long daysLeft;

    private String sla;

}
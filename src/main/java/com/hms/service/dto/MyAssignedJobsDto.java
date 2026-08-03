package com.hms.service.dto;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyAssignedJobsDto {

    private Integer jobId;

    private String position;

    private Integer totalOpenings;

    private LocalDate targetStartDate;

    private Integer my;

    private Integer team;

    private Integer yetToFill;

    private Integer inProgress;

    private Long daysRemaining;

    private String slaStatus;

}
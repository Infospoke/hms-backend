package com.hms.service.response;

import java.time.LocalDate;
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
    private Integer businessUnitId;
    private Integer departmentId;
    private List<Integer> reportingManagerInfo;

    private String location;
    private String seniorityLevel;
    private Integer openings;
    private LocalDate targetStartDate;

    private String workMode;
    private String employmentType;
    private String priority;

    private Boolean submitted;
    private Boolean approved;

    private LocalDate createdOn;
    private String createdBy;
}

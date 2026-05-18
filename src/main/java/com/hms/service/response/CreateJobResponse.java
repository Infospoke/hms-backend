package com.hms.service.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobResponse {

    private Integer id;

    private String jobTitle;

    private Integer businessUnitId;

    private Integer departmentId;

    private String location;

    private String jobCode;

    private Integer openings;

    private LocalDate targetStartDate;

    private String workMode;

    private String employmentType;

    private String skillsMustHave;

    private String niceToHaveSkills;

    private Integer minExperience;

    private Integer maxExperience;

    private String additionalNotes;

    private Boolean submit;
}
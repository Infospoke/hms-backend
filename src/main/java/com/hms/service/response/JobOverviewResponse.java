package com.hms.service.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobOverviewResponse {


    private String jobTitle;

    private String jobCode;

    private String businessUnit;

    private String department;

    private String location;

    private Integer openings;

    private LocalDate targetStartDate;

    private String workMode;

    private String employmentType;

    private Integer minExperience;

    private Integer maxExperience;

    private List<String> skillsMustHave;

    private List<String> niceToHaveSkills;

    private String additionalNotes;
}
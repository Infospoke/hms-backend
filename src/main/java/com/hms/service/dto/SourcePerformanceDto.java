package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourcePerformanceDto {

    private Long linkedIn;

    private Long naukri;

    private Long employeeReferral;

    private Long companyCareerPortal;

    private Long indeed;

    private Long others;

}
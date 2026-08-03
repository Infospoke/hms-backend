package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversionFunnelDto {

    private Long applications;

    private Long screening;

    private Long shortlisted;

    private Long interview;

    private Long offers;

    private Long hired;

}
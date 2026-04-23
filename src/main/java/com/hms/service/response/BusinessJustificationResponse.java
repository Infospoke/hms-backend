package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessJustificationResponse {

    private Integer id;
    private String srId;

    private String requisitionType;
    private String businessCase;
    private String impactIfNotFilled;
    private Integer replacesEmployee;
    private String document;

    private Boolean draft;
    private Boolean submitted;
    private Boolean approved;
}

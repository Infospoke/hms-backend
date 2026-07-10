package com.hms.service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferReadyToReleaseDto {

    private Integer offerId;
    private Integer applicationId;

    private String candidateName;
    private String email;

    private Integer jobId;
    private String jobTitle;

    private Integer departmentId;
    private String departmentName;

    private String recruiterName;

    private LocalDateTime approvedOn;

    private String priority;
}
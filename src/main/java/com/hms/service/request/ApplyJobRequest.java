package com.hms.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplyJobRequest {

    @NotBlank(message = "Candidate Id is required")
    private String candidateId;

    @NotNull(message = "Job Id is required")
    private Integer jobId;

}
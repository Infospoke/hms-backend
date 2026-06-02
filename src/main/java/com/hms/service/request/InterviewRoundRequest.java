package com.hms.service.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewRoundRequest {

    @NotNull(message = "Round order is required")
    private Integer roundOrder;

    @NotBlank(message = "Stage name is required")
    private String stageName;

    @NotBlank(message = "Stage type is required")
    private String stageType;

    @NotBlank(message = "Interview mode is required")
    private String interviewMode;

    private Boolean mandatory;
}

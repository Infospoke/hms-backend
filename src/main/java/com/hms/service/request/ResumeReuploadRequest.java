package com.hms.service.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeReuploadRequest {

    @NotNull(message = "Application Id is required.")
    private Integer applicationId;

    @NotNull(message = "Update profile resume flag is required.")
    private Boolean updateProfileResume;

}
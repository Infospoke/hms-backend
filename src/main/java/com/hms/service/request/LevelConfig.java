package com.hms.service.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LevelConfig {

    @NotNull(message = "Level is required")
    private Integer level;

    @NotNull(message = "Department is required")
    private Integer departmentId;

    @NotNull(message = "Role is required")
    private Integer roleId;
}


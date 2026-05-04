package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LevelConfig {

    private Integer level;
    private Integer departmentId;
    private Integer roleId;
}

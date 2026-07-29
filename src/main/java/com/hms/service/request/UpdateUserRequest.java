package com.hms.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    private Integer roleId;
    private Integer departmentId;
    private Boolean deactivate;
    private Boolean activate;
}

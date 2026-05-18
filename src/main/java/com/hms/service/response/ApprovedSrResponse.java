package com.hms.service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovedSrResponse {

    private String srId;

    private String srTitle;

    private String department;

    private String requestedBy;

    private LocalDateTime dateRange;
}
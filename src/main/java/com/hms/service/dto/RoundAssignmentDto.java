package com.hms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundAssignmentDto {

    private Integer roundId;

    private Integer interviewerUserId;

    private String interviewerName;

    private String roleName;
}
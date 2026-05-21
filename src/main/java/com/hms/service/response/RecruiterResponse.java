package com.hms.service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class RecruiterResponse {

	private Integer departmentId;

	private Integer roleId;

	private String roleName;

	private List<RecruiterDetailsResponse> users;

}

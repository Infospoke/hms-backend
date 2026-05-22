package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterDetailsResponse {

	private Integer userId;

	private String recruiterName;

	private String email;

	private String roleName;

	private Long totalAssignments;


}
package com.hms.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewRoundResponse {

	private Integer roundOrder;

	private Integer stageTypeId;

	private String roundName;

	private String status;

}

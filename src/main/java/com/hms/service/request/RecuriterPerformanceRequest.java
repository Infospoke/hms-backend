package com.hms.service.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecuriterPerformanceRequest {

	private Integer recruiterId;

	private Integer jobId;

	private LocalDate fromDate;

	private LocalDate toDate;

}
package com.hms.service.request;

import java.time.LocalDate;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;





import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;



@Data

@AllArgsConstructor

@NoArgsConstructor

public class ListingRequest {

	private Integer page = 0;

	private Integer size = 10;

	private String sortBy = "";

	private String direction = "ASC";

	private Integer jobId;

	private String jobCode;

	private String jobTitle;

	private Map<String, Object> filters;

	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate fromDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate toDate;

}

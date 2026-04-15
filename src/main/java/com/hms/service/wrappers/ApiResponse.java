package com.hms.service.wrappers;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

	private final String responsecode;
	private String message;
	private T data;
	private List<String> errors;
	private int totalRecords;

	public ApiResponse(ResponseCode responsecode, String message) {
		this.responsecode = responsecode.getCode();
		this.message = message;
	}

	public ApiResponse(ResponseCode responseCode, String message, T data, int totalRecords) {
		this.responsecode = responseCode.getCode();
		this.message = message;
		this.data = data;
		this.totalRecords = totalRecords;
	}

	public ApiResponse(ResponseCode responseCode, T data) {
		this.responsecode = responseCode.getCode();
		this.message = responseCode.getMessage();
		this.data = data;
		this.errors = Collections.emptyList();
	}

	public ApiResponse(ResponseCode responseCode, List<String> errors) {
		this.responsecode = responseCode.getCode();
		this.message = responseCode.getMessage();
		this.errors = errors;
	}

	public ApiResponse(ResponseCode responseCode, List<String> errors, T data) {
		this.responsecode = responseCode.getCode();
		this.message = responseCode.getMessage();
		this.errors = errors;
		this.data = data;
	}
	
	public ApiResponse(ResponseCode responseCode, String message,T data) {
		this.responsecode = responseCode.getCode();
		this.message = responseCode.getMessage();
		this.data = data;
	}

	public ApiResponse(ResponseCode responseCode, String message,List<String> errors) {
		this.responsecode = responseCode.getCode();
		this.message = responseCode.getMessage();
		this.errors = errors;
	}
	// Static helpers for convenience
	public static <T> ApiResponse<T> success(ResponseCode code, T data) {
		return new ApiResponse<>(code, data);
	}

	public static <T> ApiResponse<T> failure(ResponseCode code, List<String> errors) {
		return new ApiResponse<>(code, errors);
	}


	public static <T> ApiResponse<T> success(String message, T data, int totalRecords) {
		return new ApiResponse<>(ResponseCode.SUCCESS, message, data, totalRecords);
	}

	public static <T> ApiResponse<T> success(String message) {
		return new ApiResponse<>(ResponseCode.SUCCESS, message);
	}

	public static <T> ApiResponse<T> failure(String message) {
		return new ApiResponse<>(ResponseCode.FAILURE, message);
	}

	public static ApiResponse<?> failure(ResponseCode code, String message) {
		return new ApiResponse<>(code, message);
	}	
	// for success use this
	public static <T> ApiResponse<T> success(ResponseCode code,String message, T data) {
		return new ApiResponse<>(ResponseCode.SUCCESS, message, data);
	}
	
	//failure use this
	public static <T> ApiResponse<T> failure(ResponseCode code,String message, List<String> errors) {
		return new ApiResponse<>(code, message,errors);
	}
}

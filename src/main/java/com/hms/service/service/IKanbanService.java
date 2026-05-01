package com.hms.service.service;


import com.hms.service.request.FilterRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IKanbanService {

	ApiResponse<?> getFilteredData(FilterRequest request);

}

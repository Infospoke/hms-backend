package com.hms.service.service;

import org.jspecify.annotations.Nullable;

import com.hms.service.request.FilterRequest;
import com.hms.service.request.KanbanFilterRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IKanbanService {

	ApiResponse<?> getFilteredData(FilterRequest request);

}

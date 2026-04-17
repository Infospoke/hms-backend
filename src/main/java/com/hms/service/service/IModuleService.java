package com.hms.service.service;

import com.hms.service.request.ModuleRequest;
import com.hms.service.wrappers.ApiResponse;

public interface IModuleService {

	ApiResponse<?> addModule(ModuleRequest request);

	

}

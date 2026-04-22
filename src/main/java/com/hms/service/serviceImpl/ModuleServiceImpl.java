package com.hms.service.serviceImpl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.constants.Constants;
import com.hms.service.entity.ModuleEntity;
import com.hms.service.repository.ModuleRepository;
import com.hms.service.request.ModuleRequest;
import com.hms.service.service.IModuleService;
import com.hms.service.utils.SequenceGenerator;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ModuleServiceImpl implements IModuleService {

	@Autowired
	private ModuleRepository moduleRepository;
	
	@Autowired
	private SequenceGenerator sequenceGenerator;

	@Override
	public ApiResponse<?> addModule(ModuleRequest request) {

		log.info("ModuleServiceImpl::Inside addModule method");

		ModuleEntity existingModule = moduleRepository.findByModuleNameIgnoreCase(request.getModuleName());

		if (existingModule != null) {
			return ApiResponse.failure(ResponseCode.FAILURE, Constants.MODULE_NAME_ALREADY_EXISTS);
		}

		ModuleEntity moduleEntity = new ModuleEntity();
		moduleEntity.setModuleName(request.getModuleName());
        moduleEntity.setCreatedDate(LocalDate.now());
        moduleEntity.setCreatedBy(request.getCreatedBy());
        moduleEntity.setModuleId(sequenceGenerator.generateModuleId());
      

		moduleRepository.save(moduleEntity);

		log.info("ModuleServiceImpl::Exit from addModule method");

		return ApiResponse.success(Constants.MODULE_ADDED_SUCCESSFULLY);
	}

}

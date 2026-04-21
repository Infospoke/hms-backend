package com.hms.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.entity.StaffingRequisitionEntitys;
import com.hms.service.repository.Staffing;
import com.hms.service.repository.StaffingRequisitionRepository;
import com.hms.service.request.StaffingRequisitionRequest;
import com.hms.service.service.IStaffingRequisitionService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class StaffRequisitionServiceImpl implements IStaffingRequisitionService{
	
	@Autowired
	private StaffingRequisitionRepository staffingRequisitionRepository;
	
	@Autowired
	private Staffing staffing;
	
	@Autowired
	private MinioClient minioClient;

	@Override
	public ApiResponse<?> newStaffingRequisition(StaffingRequisitionRequest request, MultipartFile file) {
		// TODO Auto-generated method stub
		return null;
	}

}


package com.hms.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.entity.CreateJob;
import com.hms.service.entity.RolesAndRequirementsEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.repository.CreateJobRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RolesAndRequirementsRepository;
import com.hms.service.response.CreateJobResponse;
import com.hms.service.service.ICreateJobService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreateJobServiceImpl implements ICreateJobService {

	@Autowired
	private PositionBasicsRepository positionBasicsRepository;

	@Autowired
	private CreateJobRepository createJobRepository;

	@Autowired
	private RolesAndRequirementsRepository rolesAndRequirementsRepository;

	@Override
	public ApiResponse<?> createJobFromSr(String srId, String additionalNotes) {

		log.info("Inside createJobFromSr service");

		SRPositionBasicsEntity srData = positionBasicsRepository.findBySrId(srId)
				.orElseThrow(() -> new RuntimeException("SR data not found"));

		RolesAndRequirementsEntity rolesData = rolesAndRequirementsRepository.findBySrId(srId)
				.orElseThrow(() -> new RuntimeException("Roles & Requirements data not found"));

		CreateJob createJob = new CreateJob();

		createJob.setJobTitle(srData.getJobTitle());
		createJob.setBusinessUnitId(srData.getBusinessUnitId());
		createJob.setDepartmentId(srData.getDepartmentId());
		createJob.setLocation(srData.getLocation());
		createJob.setOpenings(srData.getOpenings());
		createJob.setTargetStartDate(srData.getTargetStartDate());
		createJob.setWorkMode(srData.getWorkMode());
		createJob.setEmploymentType(srData.getEmploymentType());

		String jobCode = srData.getSrId();
		
		
		if (jobCode != null && jobCode.startsWith("SR-")) {
		    jobCode = jobCode.substring(3);
		}

		createJob.setJobCode(jobCode);

		Optional<CreateJob> existingJob =
		        createJobRepository.findByJobCode(jobCode);

		if (existingJob.isPresent()) {

		    return ApiResponse.failure(
		            ResponseCode.FAILURE,
		            "Failure",
		            List.of("Job already created for this SR ID")
		    );
		}

		createJob.setSkillsMustHave(rolesData.getSkillsMustHave());
		createJob.setNiceToHaveSkills(rolesData.getNiceToHaveSkills());
		createJob.setMinExperience(rolesData.getMinExperience());
		createJob.setMaxExperience(rolesData.getMaxExperience());

		createJob.setAdditionalNotes(
				additionalNotes != null && !additionalNotes.trim().isEmpty() ? additionalNotes.trim() : null);

		createJob.setSubmit(false);

		CreateJob savedJob = createJobRepository.save(createJob);

		CreateJobResponse response = mapToResponse(savedJob);

		return ApiResponse.success(ResponseCode.SUCCESS, "job details saved as draft successfully", response);
	}

	private CreateJobResponse mapToResponse(CreateJob entity) {

		CreateJobResponse response = new CreateJobResponse();

		response.setId(entity.getId());
		response.setJobTitle(entity.getJobTitle());
		response.setBusinessUnitId(entity.getBusinessUnitId());
		response.setDepartmentId(entity.getDepartmentId());
		response.setLocation(entity.getLocation());
		response.setJobCode(entity.getJobCode());
		response.setOpenings(entity.getOpenings());
		response.setTargetStartDate(entity.getTargetStartDate());
		response.setWorkMode(entity.getWorkMode());
		response.setEmploymentType(entity.getEmploymentType());
		response.setSkillsMustHave(entity.getSkillsMustHave());
		response.setNiceToHaveSkills(entity.getNiceToHaveSkills());
		response.setMinExperience(entity.getMinExperience());
		response.setMaxExperience(entity.getMaxExperience());
		response.setAdditionalNotes(entity.getAdditionalNotes());
		response.setSubmit(entity.getSubmit());

		return response;
	}
}

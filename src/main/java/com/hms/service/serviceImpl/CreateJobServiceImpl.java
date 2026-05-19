package com.hms.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hms.service.entity.CreateJobEntity;
import com.hms.service.entity.JobPostingEntity;
import com.hms.service.entity.RolesAndRequirementsEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.entity.SourcingChannelEntity;
import com.hms.service.repository.CreateJobRepository;
import com.hms.service.repository.JobPostingRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RolesAndRequirementsRepository;
import com.hms.service.repository.SourcingChannelRepository;
import com.hms.service.request.CreateJobRequest;
import com.hms.service.response.CreateJobResponse;
import com.hms.service.response.SourcingChannelResponse;
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

	@Autowired
	private JobPostingRepository jobPostingRepository;

	@Autowired
	private SourcingChannelRepository sourcingChannelRepository;

	@Override
	public ApiResponse<?> createJobFromSr(String srId, CreateJobRequest request) {

		log.info("Inside createJobFromSr service");

		SRPositionBasicsEntity srData = positionBasicsRepository.findBySrId(srId)
				.orElseThrow(() -> new RuntimeException("SR data not found"));

		RolesAndRequirementsEntity rolesData = rolesAndRequirementsRepository.findBySrId(srId)
				.orElseThrow(() -> new RuntimeException("Roles & Requirements data not found"));

		String jobCode = generateJobCode(srId);

		CreateJobEntity jobEntity = createJobRepository.findByJobCode(jobCode).orElse(null);

		boolean isUpdate = jobEntity != null;

		if (!isUpdate) {

			jobEntity = buildCreateJobEntity(srData, rolesData, request, jobCode);
		}

		else {

			jobEntity.setAdditionalNotes(trimValue(request.getAdditionalNotes()));
		}

		CreateJobEntity savedJob = createJobRepository.save(jobEntity);

		saveSelectedChannels(jobCode, savedJob.getId(), request);

		return ApiResponse.success(ResponseCode.SUCCESS,
				isUpdate ? "Job updated successfully" : "Job created successfully");
	}

	private CreateJobEntity buildCreateJobEntity(SRPositionBasicsEntity srData, RolesAndRequirementsEntity rolesData,
			CreateJobRequest request, String jobCode) {

		CreateJobEntity entity = new CreateJobEntity();

		entity.setJobTitle(srData.getJobTitle());

		entity.setBusinessUnitId(srData.getBusinessUnitId());

		entity.setDepartmentId(srData.getDepartmentId());

		entity.setLocation(srData.getLocation());

		entity.setOpenings(srData.getOpenings());

		entity.setTargetStartDate(srData.getTargetStartDate());

		entity.setWorkMode(srData.getWorkMode());

		entity.setEmploymentType(srData.getEmploymentType());

		entity.setJobCode(jobCode);

		entity.setSkillsMustHave(rolesData.getSkillsMustHave());

		entity.setNiceToHaveSkills(rolesData.getNiceToHaveSkills());

		entity.setMinExperience(rolesData.getMinExperience());

		entity.setMaxExperience(rolesData.getMaxExperience());

		entity.setAdditionalNotes(trimValue(request.getAdditionalNotes()));

		entity.setSubmit(false);

		return entity;
	}

	private void saveSelectedChannels(String jobCode,Integer jobId, CreateJobRequest request) {

		if (request.getChannelIds() == null || request.getChannelIds().isEmpty()) {

			return;
		}

		List<SourcingChannelEntity> channels = sourcingChannelRepository.findByIdIn(request.getChannelIds());

		if (channels.size() != request.getChannelIds().size()) {

			throw new RuntimeException("Invalid channel ids provided");
		}

		if (request.getChannelIds().contains(5) && trimValue(request.getReferralAmount()) == null) {

			throw new RuntimeException("Referral amount is required for Employee Referral");
		}

		List<JobPostingEntity> mappings = request.getChannelIds().stream()
				.map(channelId -> buildChannelEntity(jobCode,jobId,channelId, request)).toList();

		jobPostingRepository.saveAll(mappings);
	}

	private JobPostingEntity buildChannelEntity(String jobCode,Integer jobId, Integer channelId, CreateJobRequest request) {

		JobPostingEntity entity = new JobPostingEntity();

		entity.setJobCode(jobCode);
		
		entity.setJobId(jobId);

		entity.setSourcingChannelId(channelId);

		entity.setPostJob(true);

		if (channelId.equals(5)) {

			entity.setReferralAmount(trimValue(request.getReferralAmount()));
		}

		return entity;
	}

//	private void validateDuplicateJob(String jobCode) {
//
//		boolean alreadyExists = createJobRepository.findByJobCode(jobCode).isPresent();
//
//		if (alreadyExists) {
//
//			throw new RuntimeException("Job already created for this SR ID");
//		}
//	}

	private String generateJobCode(String srId) {

		if (srId != null && srId.startsWith("SR-")) {
			return srId.substring(3);
		}

		return srId;
	}

	private String trimValue(String value) {

		return value != null && !value.trim().isEmpty()

				? value.trim()

				: null;
	}

	@Override
	public ApiResponse<?> getCreateJobDetails(String srId) {

		log.info("Inside getCreateJobDetails service");

		SRPositionBasicsEntity srData = positionBasicsRepository.findBySrId(srId)
				.orElseThrow(() -> new RuntimeException("SR data not found"));

		RolesAndRequirementsEntity rolesData = rolesAndRequirementsRepository.findBySrId(srId)
				.orElseThrow(() -> new RuntimeException("Roles data not found"));

		List<SourcingChannelEntity> channels = sourcingChannelRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

		CreateJobResponse response = new CreateJobResponse();

		response.setJobTitle(srData.getJobTitle());

		response.setBusinessUnitId(srData.getBusinessUnitId());

		response.setDepartmentId(srData.getDepartmentId());

		response.setLocation(srData.getLocation());

		response.setOpenings(srData.getOpenings());

		response.setTargetStartDate(srData.getTargetStartDate());

		response.setWorkMode(srData.getWorkMode());

		response.setEmploymentType(srData.getEmploymentType());

		response.setJobCode(generateJobCode(srData.getSrId()));

		response.setMinExperience(rolesData.getMinExperience());

		response.setMaxExperience(rolesData.getMaxExperience());

		response.setSkillsMustHave(rolesData.getSkillsMustHave());

		response.setNiceToHaveSkills(rolesData.getNiceToHaveSkills());

		response.setChannels(channels.stream().map(this::mapChannelResponse).toList());

		return ApiResponse.success(ResponseCode.SUCCESS, "Create Job details fetched successfully", response);
	}

	private SourcingChannelResponse mapChannelResponse(SourcingChannelEntity entity) {

		SourcingChannelResponse response = new SourcingChannelResponse();

		response.setId(entity.getId());

		response.setChannelName(entity.getChannelName());

		response.setBestFor(entity.getBestFor());

		response.setCost(entity.getCost());

		return response;
	}
}
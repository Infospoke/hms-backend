package com.hms.service.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.service.entity.CreateJobEntity;
import com.hms.service.entity.RolesAndRequirementsEntity;
import com.hms.service.entity.SRPositionBasicsEntity;
import com.hms.service.entity.SourcingChannelEntity;
import com.hms.service.repository.CreateJobRepository;
import com.hms.service.repository.PositionBasicsRepository;
import com.hms.service.repository.RolesAndRequirementsRepository;
import com.hms.service.repository.SourcingChannelRepository;
import com.hms.service.request.CreateJobRequest;
import com.hms.service.request.SourcingChannelRequest;
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
	private SourcingChannelRepository sourcingChannelRepository;

	private static final Map<String, Map<String, String>> CHANNEL_MASTER_DATA = Map.of(

			"LinkedIn Jobs", Map.of("bestFor", "Professional & experienced candidates",

					"cost", "Paid"),

			"Indeed", Map.of("bestFor", "Large volume of active job seekers",

					"cost", "Paid"),

			"Naukri.com", Map.of("bestFor", "Active job seekers across India",

					"cost", "Paid"),

			"Internal Career Site", Map.of("bestFor", "Internal & past applicants",

					"cost", "Free"),

			"Employee Referral", Map.of("bestFor", "Quality hires through employee network",

					"cost", "Free"),

			"Monster", Map.of("bestFor", "Diverse talent pool",

					"cost", "Paid"),

			"Shine.com", Map.of("bestFor", "Mid-level professionals",

					"cost", "Paid"),

			"TimesJobs", Map.of("bestFor", "Experienced professionals",

					"cost", "Paid"),

			"Apna", Map.of("bestFor", "Blue collar & local candidates",

					"cost", "Free"));

	@Override
	public ApiResponse<?> createJobFromSr(String srId, CreateJobRequest request) {

		log.info("Inside createJobFromSr service");

		SRPositionBasicsEntity srData = positionBasicsRepository.findBySrId(srId)
				.orElseThrow(() -> new RuntimeException("SR data not found"));

		RolesAndRequirementsEntity rolesData = rolesAndRequirementsRepository.findBySrId(srId)
				.orElseThrow(() -> new RuntimeException("Roles & Requirements data not found"));

		String jobCode = generateJobCode(srData.getSrId());

		validateDuplicateJob(jobCode);

		CreateJobEntity createJob = buildCreateJobEntity(srData, rolesData, request, jobCode);

		CreateJobEntity savedJob = createJobRepository.save(createJob);

		saveSourcingChannels(savedJob.getId(), request.getChannels());

		return ApiResponse.success(ResponseCode.SUCCESS, "Job details saved successfully");
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

	private void saveSourcingChannels(Integer jobId, List<SourcingChannelRequest> channels) {

		if (channels == null || channels.isEmpty()) {
			return;
		}

		List<SourcingChannelEntity> entities = channels.stream().map(channel -> buildChannelEntity(jobId, channel))
				.toList();

		sourcingChannelRepository.saveAll(entities);
	}

	private SourcingChannelEntity buildChannelEntity(Integer jobId, SourcingChannelRequest request) {

		validateChannel(request);

		Map<String, String> channelData = CHANNEL_MASTER_DATA.get(request.getChannelName());

		SourcingChannelEntity entity = new SourcingChannelEntity();

		entity.setJobId(jobId);

		entity.setChannelName(request.getChannelName());

		entity.setBestFor(channelData.get("bestFor"));

		entity.setCost(channelData.get("cost"));

		entity.setPostJob(request.getPostJob());

		entity.setReferralAmount(trimValue(request.getReferralAmount()));

		return entity;
	}

	private void validateDuplicateJob(String jobCode) {

		boolean alreadyExists = createJobRepository.findByJobCode(jobCode).isPresent();

		if (alreadyExists) {

			throw new RuntimeException("Job already created for this SR ID");
		}
	}

	private void validateChannel(SourcingChannelRequest request) {

		String channelName = request.getChannelName();

		if (!CHANNEL_MASTER_DATA.containsKey(channelName)) {

			throw new RuntimeException("Invalid channel name: " + channelName);
		}

		if ("Employee Referral".equalsIgnoreCase(channelName)

				&&

				Boolean.TRUE.equals(request.getPostJob())

				&&

				trimValue(request.getReferralAmount()) == null) {

			throw new RuntimeException("Referral amount is required for Employee Referral");
		}
	}

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

		List<SourcingChannelEntity> channels = sourcingChannelRepository.findAll();

		CreateJobResponse response = new CreateJobResponse();

		response.setJobTitle(srData.getJobTitle());

		response.setBusinessUnitId(srData.getBusinessUnitId());

		response.setDepartmentId(srData.getDepartmentId());

		response.setLocation(srData.getLocation());

		response.setOpenings(srData.getOpenings());

		response.setTargetStartDate(srData.getTargetStartDate());

		response.setWorkMode(srData.getWorkMode());

		response.setEmploymentType(srData.getEmploymentType());

		response.setJobCode(srData.getSrId());

		response.setMinExperience(rolesData.getMinExperience());

		response.setMaxExperience(rolesData.getMaxExperience());

		response.setSkillsMustHave(rolesData.getSkillsMustHave());

		response.setNiceToHaveSkills(rolesData.getNiceToHaveSkills());

		List<SourcingChannelResponse> channelResponses = channels.stream().map(this::mapChannelResponse).toList();

		response.setChannels(channelResponses);

		return ApiResponse.success(ResponseCode.SUCCESS, "Create Job details fetched successfully", response);
	}

	private SourcingChannelResponse mapChannelResponse(SourcingChannelEntity entity) {

		SourcingChannelResponse response = new SourcingChannelResponse();

		response.setId(entity.getId());

		response.setChannelName(entity.getChannelName());

		response.setBestFor(entity.getBestFor());

		response.setCost(entity.getCost());

		response.setPostJob(false);

		response.setReferralAmount(null);

		return response;
	}
}
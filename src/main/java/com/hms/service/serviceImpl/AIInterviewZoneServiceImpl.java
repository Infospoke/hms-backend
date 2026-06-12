package com.hms.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hms.service.dto.AIInterviewZoneDto;
import com.hms.service.entity.AIInterviewQuestionsEntity;
import com.hms.service.entity.InterviewSessionEntity;
import com.hms.service.repository.AInterviewQuestionsRepository;
import com.hms.service.repository.InterviewSessionRepository;
import com.hms.service.request.SpecificationFilterRequest;
import com.hms.service.service.IAIInterviewZoneService;
import com.hms.service.wrappers.ApiResponse;
import com.hms.service.wrappers.ResponseCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AIInterviewZoneServiceImpl implements IAIInterviewZoneService {

	@Autowired
	private InterviewSessionRepository interviewSessionRepository;

	@Autowired
	private AInterviewQuestionsRepository aInterviewQuestionsRepository;

	@Override
	public ApiResponse<?> getAiInterviewZoneList(SpecificationFilterRequest request) {
		
		log.info("AIInterviewZoneServiceImpl::Inside the getAiInterviewZoneList method");

		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				"DESC".equalsIgnoreCase(request.getDirection()) ? Sort.by(request.getSortBy()).descending()
						: Sort.by(request.getSortBy()).ascending());

		Specification<InterviewSessionEntity> spec = request.buildAIInterviewQuestionSpecification();

		Page<InterviewSessionEntity> page = interviewSessionRepository.findAll(spec, pageable);

		List<AIInterviewZoneDto> content = page.getContent().stream().map(session -> {

			AIInterviewQuestionsEntity aiQuestion = aInterviewQuestionsRepository
					.findByApplicationId(session.getApplicant().getApplicationId()).orElse(null);

			Boolean questionStatus = session.getQuestionsStatus();

			Integer numberOfQuestions = 0;

			if (aiQuestion != null) {
				numberOfQuestions = aiQuestion.getNumberOfQuestions();
			}

			LocalDateTime updatedDate;

			if (Boolean.TRUE.equals(questionStatus) && aiQuestion != null) {
				updatedDate = aiQuestion.getCreatedAt();
			} else {
				updatedDate = session.getCreatedDate();
			}

			return new AIInterviewZoneDto(
					session.getApplicant() != null ? session.getApplicant().getApplicationId() : null,

					session.getApplicant() != null ? session.getApplicant().getCandidateName() : null,

					session.getJob() != null ? session.getJob().getJobTitle() : null,

					numberOfQuestions, questionStatus, updatedDate,
					session.getApplicant() != null ? session.getApplicant().getEmail() : null);

		}).toList();

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("candidateCount", page.getTotalElements());
		response.put("content", content);
		response.put("page", page.getNumber());
		response.put("size", page.getSize());
		response.put("totalElements", page.getTotalElements());
		response.put("totalPages", page.getTotalPages());
		
		log.info("AIInterviewZoneServiceImpl::Exit from the getAiInterviewZoneList method");
		
		return ApiResponse.success(ResponseCode.SUCCESS, "Candidates fetched successfully", response);
	}
}

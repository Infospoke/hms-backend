package com.hms.service.serviceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hms.service.entity.CreateJobDetailsEntity;
import com.hms.service.entity.JobApplicationEntity;
import com.hms.service.entity.UserEntity;
import com.hms.service.repository.CreateJobDetailsRepository;
import com.hms.service.repository.JobApplicationRepository;
import com.hms.service.repository.UserRepository;
import com.hms.service.service.IMailService;
import com.hms.service.service.ResumeReuploadMailService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ResumeReuploadMailServiceImpl implements ResumeReuploadMailService {

	@Autowired
	private JobApplicationRepository jobApplicationRepository;

	@Autowired
	private CreateJobDetailsRepository createJobDetailsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IMailService mailService;

	@Value("${spring.mail.username}")
	private String fromMail;

	@Value("${company.name}")
	private String companyName;

	@Value("${company.email}")
	private String companyEmail;

	@Value("${resume.reupload.days:3}")
	private Integer reuploadDays;

	@Override
	public void sendResumeReuploadMail(Integer applicationId) {

		JobApplicationEntity application = jobApplicationRepository.findById(applicationId)
				.orElseThrow(() -> new RuntimeException("Application not found"));

		CreateJobDetailsEntity job = createJobDetailsRepository.findById(application.getJobId()).orElse(null);
		log.info("Job returned : {}", job);
		UserEntity recruiter = userRepository.findByUserId(application.getRecruiterId())
				.orElseThrow(() -> new RuntimeException("Recruiter not found"));

		String applicantName = application.getFirstName() + " " + application.getLastName();

		String recruiterName = recruiter.getFirstName() + " " + recruiter.getLastName();

		String dueDate = LocalDate.now().plusDays(reuploadDays).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

		String subject = "Resume Re-upload Request - " + job.getJobTitle();

		String body = buildMail(applicantName, recruiterName, companyName, job.getJobTitle(), dueDate, companyEmail);

		mailService.sendMail(fromMail, application.getEmail(), null, subject, body, null);

		log.info("Resume re-upload mail sent successfully.");
	}

	private String buildMail(String applicantName, String recruiterName, String companyName, String jobTitle,
			String dueDate, String companyEmail) {

		return """
								<html>
								<body>

								<p>Dear %s,</p>

								<p>We hope you are doing well.</p>

								<p>
								Thank you for your interest in the <b>%s</b> position at
								<b>%s</b>.
								</p>

								<p>
								During our review of your application, we noticed that your uploaded
								resume is incomplete, outdated, corrupted, or could not be accessed.
								</p>

								<p>
								Please upload the latest version of your resume to continue with the
								recruitment process.
								</p>

								<p><b>Steps:</b></p>

								<ol>
								   <li>Login to the Candidate Portal.</li>
								   <li>Open your application.</li>
								   <li>Upload your latest resume.</li>
								   <li>Submit the application.</li>
								</ol>

								<p><b>Requested By:</b> %s</p>

								<p><b>Please upload your resume before:</b> %s</p>

								<p>
								For any queries, please contact us at
								<b>%s</b>.
								</p>

								<br>

				               <p>Thanks &amp; Regards,</p>

				               <p><b>Recruitment Team</b></p>

				               <p><b>%s</b></p>

								</body>
								</html>
								""".formatted(applicantName, jobTitle, companyName, recruiterName, dueDate,
				companyEmail, companyName);
	}
}
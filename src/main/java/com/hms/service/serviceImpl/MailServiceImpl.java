package com.hms.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hms.service.constants.Constants;
import com.hms.service.exceptions.CustomSystemErrorException;
import com.hms.service.service.IMailService;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailServiceImpl implements IMailService {

	@Autowired
	private JavaMailSender javaMailSender;

	public void sendMail(String from, String to, String cc, String subject, String body, MultipartFile files) {
		log.info("MailServiceImpl::Inside the sendMail method");
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

			mimeMessageHelper.setFrom(from);
			mimeMessageHelper.setTo(to);
			if (cc != null)
				mimeMessageHelper.setCc(cc);
			mimeMessageHelper.setSubject(subject);
			mimeMessageHelper.setText(body, true);
			try {
				if (files != null && !files.isEmpty()) {
					mimeMessageHelper.addAttachment(files.getOriginalFilename(),
							new ByteArrayResource(files.getBytes()));
				}
			} catch (Exception e) {
				log.info("MailServiceImpl::exception occured in sendMail method" + e.getMessage());
				throw new CustomSystemErrorException(Constants.COULD_NOT_ATTACH_FILE);

			}
			javaMailSender.send(mimeMessage);
			log.info("MailServiceImpl::Email sent successfully");
		} catch (Exception e) {
			log.info("MailServiceImpl::exception occured in sendMail method" + e.getMessage());
			throw new CustomSystemErrorException(Constants.MAIL_FAILURE);

		}
		log.info("MailServiceImpl::Exit from the sendMail method");
	}

	@Override
	public void sendMailToMultiple(String from, String toEmails, List<String> ccEmails, String subject, String body,
			MultipartFile file) {

		log.info("MailServiceImpl::Inside sendMailToMultiple");

		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(from);

			if (toEmails != null && !toEmails.isEmpty()) {
				helper.setTo(toEmails);
			}
			if (file != null) {
			}
			if (ccEmails != null && !ccEmails.isEmpty()) {
				helper.setCc(ccEmails.toArray(new String[0]));
			}
			helper.setSubject(subject);
			helper.setText(body, true);
			if (file != null && !file.isEmpty()) {
				helper.addAttachment(file.getOriginalFilename(), new ByteArrayResource(file.getBytes()));
			}
			javaMailSender.send(mimeMessage);
			log.info("MailServiceImpl::Email sent successfully");
		} catch (Exception e) {
			log.info("MailServiceImpl::exception occured in sendMailToMultiple method" + e.getMessage());
			throw new CustomSystemErrorException(Constants.MAIL_FAILURE);
		}
		log.info("MailServiceImpl::Exit sendMailToMultiple");
	}

	public void sendMailWithAttachment(String from, String to, String cc, String subject, String body, byte[] file,
			String fileName) {

		try {

			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setFrom(from);
			helper.setTo(to);

			if (cc != null) {
				helper.setCc(cc);
			}

			helper.setSubject(subject);
			helper.setText(body, true);

			helper.addAttachment(fileName, new ByteArrayResource(file));

			javaMailSender.send(mimeMessage);

		} catch (Exception e) {
			throw new CustomSystemErrorException(Constants.MAIL_FAILURE);
		}
	}

}

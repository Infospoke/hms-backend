package com.hms.service.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface IMailService {

	 void sendMail(String from , String to, String cc, String subject, String body, MultipartFile files );
	 
	 void sendMailToMultiple(String from,String toEmail, List<String> ccEmails, String subject, String body,
				MultipartFile file);
}

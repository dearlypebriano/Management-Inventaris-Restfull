package com.management.ManagementInventaris.email.impl;

import com.management.ManagementInventaris.email.EmailDetails;
import com.management.ManagementInventaris.email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendSimpleMail(EmailDetails details) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(details.getRecipient());
            message.setText(details.getMsgBody());
            message.setSubject(details.getSubject());
            javaMailSender.send(message);
            return "Mail sent successfully!!";
        } catch (Exception e) {
            return "Error while sending mail" + e;
        }
    }

    @Override
    public String sendMailWithAttachment(EmailDetails details) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(details.getSubject());

            FileSystemResource file = new FileSystemResource(details.getAttachment());
            mimeMessageHelper.addAttachment(file.getFilename(), file);
            javaMailSender.send(mimeMessage);
            return "Mail sent successfully!!";
        } catch (MessagingException e) {
            return "Error while sending mail" + e;
        }
    }

    @Override
    public String sendApiKeyToEmail(String apiKey) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient("dearlyfebrianoi@gmail.com");
        emailDetails.setSubject("New API Key Generated");
        emailDetails.setMsgBody("A new API key has been generated: " + apiKey);

        return sendSimpleMail(emailDetails);
    }
}
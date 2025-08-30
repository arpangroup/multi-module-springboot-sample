package com.trustai.notification_service.notification.service.impl;

import com.trustai.notification_service.config.MailProperties;
import com.trustai.notification_service.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    @Async
    public void sendSimpleMail(String to, String subject, String text) {
        log.info("Sending simple text email to: {}, subject: {}", to, subject);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            //message.setFrom("arpangroup1@gmail.com");
            message.setFrom(mailProperties.getFrom().getAddress());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Simple email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}. Error: {}", to, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void sendMail(String to, String subject, String htmlContent) {
        log.info("Sending HTML email to: {}, subject: {}", to, subject);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(mailProperties.getFrom().getAddress());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = send as HTML

            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to {}. Error: {}", to, e.getMessage(), e);
            //e.printStackTrace();
        }
    }

    @Override
    @Async
    public void sendMailWithAttachment(String to, String subject, String text, String pathToAttachment) {
        log.info("Sending email with attachment to: {}, subject: {}, attachment: {}", to, subject, pathToAttachment);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            // Setting multipart as true for attachments to be send
            mimeMessageHelper.setFrom("noreply@baeldung.com");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text);
            //mimeMessageHelper.setText(content, true); // true = HTML

            // Adding the attachment
            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            mimeMessageHelper.addAttachment(file.getFilename(), file);

            mailSender.send(mimeMessage);
            log.info("Email with attachment sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email with attachment to {}. Error: {}", to, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}

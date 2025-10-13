package com.artflow.artflow.email;

import com.artflow.artflow.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

//@Service
//public class MailService {
//    private static final Logger log = LoggerFactory.getLogger(MailService.class);
//    private final boolean mailEnabled;
//    private final JavaMailSender mailSender;
//
//    public MailService(JavaMailSender mailSender, @Value("#{ '${spring.profiles.active}' == 'prod' ? true : false }") boolean mailEnabled) {
//        this.mailSender = mailSender;
//        this.mailEnabled = mailEnabled;
//    }
//
//    public void sendSimpleMessage(String to, String subject, String text) {
//        if (!mailEnabled) {
//            log.info("Mail disabled in dev");
//            return;
//        }
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("ruthieismakinganapp@gmail.com");
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//        mailSender.send(message);
//    }
//
//}

//package com.example.Backend_java_newbie.application.service;
//
//import com.example.Backend_java_newbie.domain.interfaces.service.IEmailService;
//import com.example.Backend_java_newbie.exception.BaseException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.FileCopyUtils;
//import org.springframework.util.ResourceUtils;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//
//@Service
//public class EmailService {
//
//    private final IEmailService emailService;
//
//    @Value("${app.frontend.activate-url}")
//    private String activateUrl;
//
//    public EmailService(IEmailService emailService) {
//        this.emailService = emailService;
//    }
//
//    public void sendVerifyEmail(String email, String firstName, String rawToken) throws BaseException {
//        String html;
//        try {
//            html = readEmailTemplate("email-activate-user.html");
//        } catch (IOException e) {
//            throw new BaseException("email.template.not.found");
//        }
//
//        String link = activateUrl + "?token=" + rawToken;
//
//        html = html.replace("${P_NAME}", firstName);
//        html = html.replace("${LINK}", link);
//
//        emailService.send(email, "Please activate your account", html);
//    }
//
//    private String readEmailTemplate(String filename) throws IOException {
//        File file = ResourceUtils.getFile("classpath:email/" + filename);
//        return FileCopyUtils.copyToString(new FileReader(file));
//    }
//}
//

package org.rubilnik.auth_service.services;

import jakarta.mail.MessagingException;
import org.rubilnik.auth_service.record_classes.Records;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.regex.Pattern;

@Service @Profile(value = "server")
public class EmailService {
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    TemplateEngine thymeleaf;
    @Autowired
    UserRegisterEmailVerificationTokenService userRegisterEmailVerificationTokenService;
    @Value("${spring.mail.username}")
    String from;
    @Value("${rubilnik.central-server.url}")
    String centralServerUrl;

    public void sendVerifyEmail(Records.UserValidationInfo info, String username) throws IllegalArgumentException {
        validate(info.email());
//        sendTextEmail(info.email(), "Rubilnik email verification", "Please click on the link below to verify your email\n"+centralServerUrl+"/verify?email="+info.email()+"&token="+ userRegisterEmailVerificationTokenService.generateToken(username,info));
        var context = new Context();
        context.setVariable("username", username);
        context.setVariable("url", centralServerUrl);
        context.setVariable("link", centralServerUrl+"/verify?email="+info.email()+"&token="+ userRegisterEmailVerificationTokenService.generateToken(username,info));
        sendThymeleafHtmlTemplateEmail(info.email(), "Rubilnik email verification", "registration-confirm-email", context);
    }

    private void sendThymeleafHtmlTemplateEmail(String to, String subject, String filename, Context context){
        validate(to);
        var mime = mailSender.createMimeMessage();
        var mail = new MimeMessageHelper(mime);// SimpleMailMessage(); // TODO MimeMessage mb with ThymeleafConfig @Bean TemplateEngine
        var text = thymeleaf.process(filename, context);
        try {
            mail.setFrom(from);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(text, true);
        } catch (MessagingException e){ System.out.println(e.getMessage());} // TODO
        mailSender.send(mime);
    }
    private void sendTextEmail(String to, String subject, String text){
        validate(to);
        System.out.println("Sending email to "+to+" with subject "+subject);
        System.out.println("Sending email from ["+from+"] to ["+to+"] with subject: "+subject);
        var mail = new SimpleMailMessage(); // TODO MimeMessage mb with ThymeleafConfig @Bean TemplateEngine
        mail.setFrom(from);
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(text);
        mailSender.send(mail);
    }
    private void validate(String to) throws IllegalArgumentException{
        var pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        if (!pattern.matcher(to).matches()) throw new IllegalArgumentException("Invalid email address");
    }

}
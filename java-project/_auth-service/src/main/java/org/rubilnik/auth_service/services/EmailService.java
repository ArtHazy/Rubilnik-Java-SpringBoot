package org.rubilnik.auth_service.services;

import org.rubilnik.auth_service.record_classes.Records;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service @Profile(value = "server")
public class EmailService {
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    UserRegisterEmailVerificationTokenService userRegisterEmailVerificationTokenService;
    @Value("${spring.mail.username}")
    String from;
    @Value("${rubilnik.central-server.url}")
    String centralServerUrl;

    public void sendVerifyEmail(Records.UserValidationInfo info, String username) throws IllegalArgumentException {
        validate(info.email());
        sendEmail(info.email(), "Rubilnik email verification", "Please click on the link below to verify your email\n"+centralServerUrl+"/verify?email="+info.email()+"&token="+ userRegisterEmailVerificationTokenService.generateToken(username,info));
    }

    private void sendEmail(String to, String subject, String text){
        System.out.println("Sending email to "+to+" with subject "+subject);
        System.out.println("Sending email from ["+from+"] to ["+to+"] with subject: "+subject);
        var mail = new SimpleMailMessage();
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
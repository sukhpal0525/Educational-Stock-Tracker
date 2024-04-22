//package com.aston.stockapp.user.email;
//
//import com.aston.stockapp.user.User;
//import com.aston.stockapp.user.email.verification.VerificationToken;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@Service
//public class EmailService {
//
//    @Autowired private JavaMailSender mailSender;
//    @Autowired private VerificationTokenRepository tokenRepository;
//
//    @Value("${spring.mail.username}")
//    private String fromAddress;
//
//    public void sendVerificationEmail(User user) {
//        String token = UUID.randomUUID().toString();
//        VerificationToken verificationToken = new VerificationToken();
//        verificationToken.setToken(token);
//        verificationToken.setUser(user);
//        tokenRepository.save(verificationToken);
//
//        String subject = "Confirm your account";
//        String confirmationUrl = "/verify?token=" + token;
//        String message = "Please click the link to verify your account: ";
//
//        SimpleMailMessage email = new SimpleMailMessage();
//        email.setFrom(fromAddress);
//        email.setTo(user.getEmail());
//        email.setSubject(subject);
//        email.setText(message + "http://localhost:8080" + confirmationUrl);
//
//        mailSender.send(email);
//    }
//}

package com.example.api.services;

import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    private JavaMailSender mailsender;
    private SpringTemplateEngine templateEngine;

    public EmailService(
            JavaMailSender mailsender,
            SpringTemplateEngine templateEngine
        ) { 
            this.mailsender = mailsender; 
            this.templateEngine = templateEngine; 
    }

    public void sendEmail(
            String to, 
            String subject, 
            String templateName,
            Map<String, Object> variables
        ) throws MessagingException {

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariables(variables);

            // Render HTML template with variables
            String htmlContent = templateEngine.process("email/" + templateName, context);
            
            // Build email
            MimeMessage message = mailsender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailsender.send(message);
    }

}

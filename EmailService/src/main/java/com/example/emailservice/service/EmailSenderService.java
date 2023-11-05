package com.example.emailservice.service;

import com.example.emailservice.model.dto.RegistrationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender javaMailSender;
    @Value("${email}")
    private String email;
    @Value("${hello_message}")
    private String helloMessage;

    public void sendRegistrationEmail(RegistrationDto registrationDto) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(registrationDto.getEmail());
            mailMessage.setSubject(helloMessage);
            mailMessage.setText(registrationDto.getFirstName() + " " + registrationDto.getLastName() + helloMessage);
            mailMessage.setFrom(email);

            javaMailSender.send(mailMessage);
            log.info("Сообщение успешно отправлено, userEmail {}", registrationDto.getEmail());
        }
        catch (MailException mailException){
            log.error("Не удалось отправить сообщение, userEmail {}", registrationDto.getEmail(), mailException);
        }
    }
}

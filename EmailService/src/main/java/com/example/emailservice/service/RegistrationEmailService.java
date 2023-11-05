package com.example.emailservice.service;

import com.example.emailservice.model.dto.RegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationEmailService {

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "registrationTopic", groupId = "registrationEmailGroup")
    public void receiveRegistrationDetails(RegistrationDto registrationDto) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(registrationDto.getEmail());
        mailMessage.setSubject("Поздравляем с регистрацией!");
        mailMessage.setText(registrationDto.getFirstName() + " " + registrationDto.getLastName() + " поздравляем с регистрацией!");
        mailMessage.setFrom("signal.test.mail@mail.ru");

        javaMailSender.send(mailMessage);

    }

}

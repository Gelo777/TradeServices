package com.example.emailservice.service;

import com.example.emailservice.model.dto.RegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationEmailService {

    private final EmailSenderService emailSenderService;

    @KafkaListener(topics = "${spring.kafka.consumer.registation-topic}", groupId = "${spring.kafka.consumer.registation-group-id}")
    public void receiveRegistrationDetails(RegistrationDto registrationDto) {
        emailSenderService.sendRegistrationEmail(registrationDto);
    }

}

package com.example.emailservice;

import com.example.emailservice.model.dto.RegistrationDto;
import com.example.emailservice.service.EmailSenderService;
import com.example.emailservice.service.RegistrationEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

public class RegistrationEmailServiceTest {

    @InjectMocks
    private RegistrationEmailService registrationEmailService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReceiveRegistrationDetails() {
        RegistrationDto registrationDto = RegistrationDto.builder().build();
        registrationDto.setEmail("test@example.com");
        registrationDto.setFirstName("John");
        registrationDto.setLastName("Doe");

        registrationEmailService.receiveRegistrationDetails(registrationDto);

        Mockito.verify(emailSenderService).sendRegistrationEmail(registrationDto);
    }
}
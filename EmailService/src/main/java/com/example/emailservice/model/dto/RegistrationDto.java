package com.example.emailservice.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationDto {
    private String email;
    private String firstName;
    private String lastName;
}

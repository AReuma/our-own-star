package com.example.backend.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCertificationCodeRequestDTO {

    @Email
    @NotBlank
    private String username;

    @NotBlank
    private String code;
}

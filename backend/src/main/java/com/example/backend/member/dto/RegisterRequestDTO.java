package com.example.backend.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "USERNAME cannot be blank")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$", message = "이메일 형식이 아닙니다.")
    @Schema(description = "아이디", example = "example@example.com")
    private String username;

    @NotBlank(message = "PASSWORD cannot be blank")
    private String password;

    @NotBlank(message = "NICKNAME cannot be blank")
    private String nickname;

    @NotBlank(message = "PHONENUM cannot be blank")
    private String phoneNum;
}

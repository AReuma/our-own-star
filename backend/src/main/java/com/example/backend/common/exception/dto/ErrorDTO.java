package com.example.backend.common.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {

    @NotBlank(message = "errorCode cannot be blank")
    @Schema(description = "errorCode", example = "에러 코드")
    private int errorCode;

    @NotBlank(message = "errorMessage cannot be blank")
    @Schema(description = "errorMessage", example = "에러 메세지")
    private String errorMessage;

    @NotBlank(message = "errorCode cannot be blank")
    @Schema(description = "errorCode", example = "에러 코드")
    private String status;
}

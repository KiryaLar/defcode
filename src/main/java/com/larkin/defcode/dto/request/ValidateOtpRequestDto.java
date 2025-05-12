package com.larkin.defcode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ValidateOtpRequestDto {
    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "\\d+", message = "Code must contain only digits")
    private String code;
}

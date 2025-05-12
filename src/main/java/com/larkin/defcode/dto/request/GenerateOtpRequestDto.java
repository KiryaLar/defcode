package com.larkin.defcode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GenerateOtpRequestDto {
    @NotBlank(message = "Method is required")
    private String method;
    @NotBlank(message = "Contact is required")
    private String contact;
    @NotBlank(message = "Operation is required")
    @Pattern(regexp = "\\d+", message = "Code must contain only digits")
    private String operationType;
}

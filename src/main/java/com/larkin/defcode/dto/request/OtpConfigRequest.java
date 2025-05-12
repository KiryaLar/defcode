package com.larkin.defcode.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpConfigRequest {
    @NotNull(message = "Code length is required")
    @Min(value = 4, message = "code must be at least 4 characters long")
    private Integer length;
    @NotBlank(message = "Lifetime is required")
    private String lifetime;
}

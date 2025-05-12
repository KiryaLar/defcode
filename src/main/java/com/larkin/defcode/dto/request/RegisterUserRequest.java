package com.larkin.defcode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString(exclude = "password")
public class RegisterUserRequest {
    @NotBlank(message = "Login is required")
    private String username;
    @Size(min = 8, message = "Password must contain at least 8 characters.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]+$",
            message = "Password must contain at least one digit, one uppercase and one lowercase letter."
    )
    @NotBlank(message = "Password is required")
    private String password;
    @Pattern(
            regexp = "(?i)^(admin|user)$",
            message = "Role must be 'admin' or 'user'"
    )
    @NotBlank(message = "Role is required")
    private String role;
}

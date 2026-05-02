package org.bloggers.ts_users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.bloggers.ts_users.utils.RequestUtils.VALID_PASSWORD_PATTERN;
import static org.bloggers.ts_users.utils.RequestUtils.VALID_USERNAME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserCredentialRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(
            regexp = VALID_USERNAME_PATTERN,
            message = "Username can only contain letters, numbers, dots, and underscores"
    )
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = VALID_PASSWORD_PATTERN,
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;
}
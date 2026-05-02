package org.bloggers.ts_users.dto.request;

import jakarta.validation.constraints.Email;
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
public class UpdateUserCredentialRequest {

    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(
            regexp = VALID_USERNAME_PATTERN,
            message = "Username can only contain letters, numbers, dots, and underscores"
    )
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(
            regexp = VALID_PASSWORD_PATTERN,
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;
}
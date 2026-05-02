package org.bloggers.ts_users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidatePasswordRequest {

    @NotNull(message = "Identifier type is required")
    private IdentifierType type;

    @NotBlank(message = "Identifier value is required")
    private String identifierValue;

    @NotBlank(message = "Password is required")
    private String password;
}

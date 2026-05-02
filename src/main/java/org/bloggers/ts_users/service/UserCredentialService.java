package org.bloggers.ts_users.service;

import jakarta.validation.constraints.NotNull;
import org.bloggers.ts_users.dto.request.CreateUserCredentialRequest;
import org.bloggers.ts_users.dto.request.IdentifierType;
import org.bloggers.ts_users.dto.request.UpdateUserCredentialRequest;
import org.bloggers.ts_users.dto.response.SuccessResponse;
import org.bloggers.ts_users.dto.response.UserCreatedResponse;
import org.bloggers.ts_users.dto.response.UserCredentialResponse;

public interface UserCredentialService {

    SuccessResponse<UserCreatedResponse> createUserCredential(CreateUserCredentialRequest request);

    SuccessResponse<UserCredentialResponse> getByIdentifier(String value, IdentifierType type);

    boolean validatePasswordByIdentifier(IdentifierType type, String identifierValue, String rawPassword);

    SuccessResponse<UserCredentialResponse> updateCredential(@NotNull String userId, UpdateUserCredentialRequest request);

    void softDeleteUserById(@NotNull String id);
}
package org.bloggers.ts_users.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bloggers.ts_users.dto.events.UserCreatedEvent;
import org.bloggers.ts_users.dto.events.UserDeletedEvent;
import org.bloggers.ts_users.dto.events.UserEventPayload;
import org.bloggers.ts_users.dto.events.UserUpdatedEvent;
import org.bloggers.ts_users.dto.request.CreateUserCredentialRequest;
import org.bloggers.ts_users.dto.request.IdentifierType;
import org.bloggers.ts_users.dto.request.UpdateUserCredentialRequest;
import org.bloggers.ts_users.dto.response.SuccessResponse;
import org.bloggers.ts_users.dto.response.UserCreatedResponse;
import org.bloggers.ts_users.dto.response.UserCredentialResponse;
import org.bloggers.ts_users.entities.Role;
import org.bloggers.ts_users.entities.UserProfile;
import org.bloggers.ts_users.exceptions.BadRequestException;
import org.bloggers.ts_users.exceptions.OperationOnDisabledResourceException;
import org.bloggers.ts_users.exceptions.ResourceConflictException;
import org.bloggers.ts_users.exceptions.ResourceNotFoundException;
import org.bloggers.ts_users.factories.UserIdentifierStrategyFactory;
import org.bloggers.ts_users.repositories.UserProfileRepository;
import org.bloggers.ts_users.service.EventPublisher;
import org.bloggers.ts_users.service.UserCredentialService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
class UserCredentialServiceImpl implements UserCredentialService {

    private final UserProfileRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserIdentifierStrategyFactory factory;
    private final EventPublisher publisher;

    @Transactional
    @Override
    public SuccessResponse<UserCreatedResponse> createUserCredential(CreateUserCredentialRequest request) {
        repository.findByCredentialsEmail(request.getEmail()).or(() -> repository.findByCredentialsUsername(request.getUsername()))
                .ifPresent(_ -> {
                    throw new ResourceConflictException("User already exist");
                });

        var encodedPassword = passwordEncoder.encode(request.getPassword());
        var user = repository.save(UserProfile.builder()
                .credentials(
                        UserProfile.Credentials.builder()
                                .email(request.getEmail())
                                .passwordHash(encodedPassword)
                                .username(request.getUsername())
                                .build()
                )
                .role(Role.USER)
                .build());
        log.info("New User saved {} at {}", user.getId(), user.getCreatedAt());

        publisher.publishEvent(new UserCreatedEvent(user.getId(), UserEventPayload.from(user)));

        return SuccessResponse.<UserCreatedResponse>builder()
                .message("User created successfully")
                .data(UserCreatedResponse.builder()
                        .id(user.getId())
                        .username(user.getCredentials().getUsername())
                        .build())
                .build();
    }

    @Override
    public SuccessResponse<UserCredentialResponse> getByIdentifier(String value, IdentifierType type) {
        var user = getUser(value, type);
        return SuccessResponse.<UserCredentialResponse>builder()
                .message("User fetched successfully")
                .data(UserCredentialResponse.builder()
                        .id(user.getId())
                        .username(user.getCredentials().getUsername())
                        .email(user.getCredentials().getEmail())
                        .build())
                .build();

    }

    @Override
    public boolean validatePasswordByIdentifier(IdentifierType type, String identifierValue, String rawPassword) {
        var user = getUser(identifierValue, type);
        return passwordEncoder.matches(
                rawPassword,
                user.getCredentials().getPasswordHash()
        );
    }

    @Transactional
    @Override
    public SuccessResponse<UserCredentialResponse> updateCredential(
            @NotNull String userId,
            UpdateUserCredentialRequest request
    ) {

        UserProfile user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive() || user.isDeleted()) {
            throw new OperationOnDisabledResourceException("User is inactive or deleted");
        }

        var credentials = user.getCredentials();

        boolean updated = false;
        boolean nonPasswordUpdated = false;

        if (StringUtils.isNotBlank(request.getUsername())) {
            String username = request.getUsername().trim();
            credentials.setUsername(username);
            updated = true;
            nonPasswordUpdated = true;
        }

        if (StringUtils.isNotBlank(request.getEmail())) {
            String email = request.getEmail().trim().toLowerCase();
            credentials.setEmail(email);
            updated = true;
            nonPasswordUpdated = true;
        }

        if (StringUtils.isNotBlank(request.getPassword())) {
            String passwordHash = passwordEncoder.encode(request.getPassword());
            credentials.setPasswordHash(passwordHash);
            updated = true;
        }

        if (!updated) {
            throw new BadRequestException("No fields provided for update");
        }

        try {
            repository.save(user);
        } catch (DuplicateKeyException ex) {
            throw new ResourceConflictException("Email or username already exists");
        }

        if (nonPasswordUpdated) {
            publisher.publishEvent(new UserUpdatedEvent(user.getId(), UserEventPayload.from(user)));
        }

        return SuccessResponse.<UserCredentialResponse>builder()
                .message("User updated successfully")
                .data(UserCredentialResponse.builder()
                        .id(user.getId())
                        .username(user.getCredentials().getUsername())
                        .email(user.getCredentials().getEmail())
                        .build())
                .build();
    }

    @Transactional
    @Override
    public void softDeleteUserById(@NotNull String id) {
        var user = getUser(id, IdentifierType.ID);
        user.setDeleted(true);
        user.setActive(false);
        repository.save(user);
        publisher.publishEvent(new UserDeletedEvent(user.getId(), UserEventPayload.from(user)));
    }

    private UserProfile getUser(String value, IdentifierType type) {
        UserProfile user = factory.getStrategy(type)
                .find(value)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Credentials"));
        if (!user.isActive() || user.isDeleted()) {
            throw new ResourceNotFoundException("Invalid Credentials");
        }
        return user;

    }
}

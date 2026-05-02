package org.bloggers.ts_users.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.bloggers.ts_users.annotations.HideResponseLog;
import org.bloggers.ts_users.dto.request.CreateUserCredentialRequest;
import org.bloggers.ts_users.dto.request.IdentifierType;
import org.bloggers.ts_users.dto.request.UpdateUserCredentialRequest;
import org.bloggers.ts_users.dto.request.ValidatePasswordRequest;
import org.bloggers.ts_users.dto.response.SuccessResponse;
import org.bloggers.ts_users.dto.response.UserCreatedResponse;
import org.bloggers.ts_users.dto.response.UserCredentialResponse;
import org.bloggers.ts_users.service.UserCredentialService;
import org.bloggers.ts_users.validation.ValidEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/user-credentials")
@AllArgsConstructor
class UserCredentialController {

    private final UserCredentialService userCredentialService;

    @PostMapping("/create")
    ResponseEntity<SuccessResponse<UserCreatedResponse>> createUserCredential(
            @Valid @RequestBody CreateUserCredentialRequest request
    ) {
        return ResponseEntity.ok(userCredentialService.createUserCredential(request));
    }

    @GetMapping
    ResponseEntity<SuccessResponse<UserCredentialResponse>> getUserCredential(
            @RequestParam
            @ValidEnum(enumClass = IdentifierType.class, message = "Invalid identifier type")
            String type,
            @RequestParam @NotBlank(message = "Value cannot be empty")
            String value
    ) {

        return ResponseEntity.ok(userCredentialService.getByIdentifier(value, IdentifierType.valueOf(type.toUpperCase())));
    }

    @HideResponseLog
    @PostMapping("/validate-password")
    ResponseEntity<SuccessResponse<Boolean>> validatePassword(
            @Valid @RequestBody ValidatePasswordRequest request
    ) {
        boolean isValid = userCredentialService.validatePasswordByIdentifier(
                request.getType(),
                request.getIdentifierValue(),
                request.getPassword()
        );
        return ResponseEntity.ok(SuccessResponse.<Boolean>builder().data(isValid).build());
    }

    @PatchMapping("/{id}")
    ResponseEntity<SuccessResponse<UserCredentialResponse>> updateUserCredential(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserCredentialRequest request
    ) {
        return ResponseEntity.ok(userCredentialService.updateCredential(id, request));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<SuccessResponse<String>> deleteUserCredential(
            @PathVariable String id
    ) {
        userCredentialService.softDeleteUserById(id);
        return ResponseEntity.ok(SuccessResponse.<String>builder().message("User deleted successfully").build());
    }
}

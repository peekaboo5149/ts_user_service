package org.bloggers.ts_users.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.bloggers.ts_users.dto.request.UpdateProfileRequest;
import org.bloggers.ts_users.dto.response.SuccessResponse;
import org.bloggers.ts_users.dto.response.UserProfileResponse;
import org.bloggers.ts_users.service.UserProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/user-profiles")
@RequiredArgsConstructor
class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{id}")
    ResponseEntity<SuccessResponse<UserProfileResponse>> getProfile(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(userProfileService.getProfileByUserId(id));
    }

    @PatchMapping("/{id}")
    ResponseEntity<SuccessResponse<UserProfileResponse>> updateProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(userProfileService.updateProfile(id, request));
    }

    @GetMapping
    ResponseEntity<SuccessResponse<Page<UserProfileResponse>>> getProfiles(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(SuccessResponse.success(userProfileService.getProfiles(pageable)));
    }

    @GetMapping("/non-admin")
    ResponseEntity<SuccessResponse<Page<UserProfileResponse>>> getNonAdminProfiles(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        Pageable pageable = buildPageable(page, size, sort, direction);
        return ResponseEntity.ok(SuccessResponse.success(userProfileService.getNonAdminProfiles(pageable)));
    }

    private Pageable buildPageable(int page, int size, String sort, String direction) {
        Sort sortOrder = Sort.unsorted();

        if (sort != null && !sort.isBlank()) {
            Sort.Direction dir;
            try {
                dir = Sort.Direction.fromString(direction);
            } catch (Exception e) {
                dir = Sort.Direction.ASC;
            }
            sortOrder = Sort.by(dir, sort);
        }

        return PageRequest.of(page, size, sortOrder);
    }
}
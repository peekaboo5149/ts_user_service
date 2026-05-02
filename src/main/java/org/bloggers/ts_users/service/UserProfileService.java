package org.bloggers.ts_users.service;

import jakarta.validation.constraints.NotNull;
import org.bloggers.ts_users.dto.request.UpdateProfileRequest;
import org.bloggers.ts_users.dto.response.SuccessResponse;
import org.bloggers.ts_users.dto.response.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserProfileService {

    SuccessResponse<UserProfileResponse> getProfileByUserId(String userId);

    SuccessResponse<UserProfileResponse> updateProfile(
            @NotNull String userId,
            UpdateProfileRequest request
    );

    Page<UserProfileResponse> getProfiles(Pageable pageable);
    Page<UserProfileResponse> getNonAdminProfiles(Pageable pageable);
}

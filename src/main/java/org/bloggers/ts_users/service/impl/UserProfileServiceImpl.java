package org.bloggers.ts_users.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bloggers.ts_users.dto.events.UserEventPayload;
import org.bloggers.ts_users.dto.events.UserUpdatedEvent;
import org.bloggers.ts_users.dto.request.UpdateProfileRequest;
import org.bloggers.ts_users.dto.response.SuccessResponse;
import org.bloggers.ts_users.dto.response.UserProfileResponse;
import org.bloggers.ts_users.entities.Role;
import org.bloggers.ts_users.entities.UserProfile;
import org.bloggers.ts_users.exceptions.ResourceNotFoundException;
import org.bloggers.ts_users.repositories.UserProfileRepository;
import org.bloggers.ts_users.service.EventPublisher;
import org.bloggers.ts_users.service.UserProfileService;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final EventPublisher eventPublisher;

    @Override
    public SuccessResponse<UserProfileResponse> getProfileByUserId(String userId) {
        var user = getUserProfile(userId);
        return SuccessResponse.<UserProfileResponse>builder()
                .message("User profile fetched successfully")
                .data(mapToProfileResponse(user))
                .build();
    }

    @Transactional
    @Override
    public SuccessResponse<UserProfileResponse> updateProfile(String userId, UpdateProfileRequest request) {
        var user = getUserProfile(userId);
        var profile = user.getProfile();

        if (!StringUtils.isBlank(request.getFirstName()) || !StringUtils.isBlank(request.getLastName())) {
            profile.setFullName(resolveFullName(profile.getFullName(), request.getFirstName(), request.getLastName()));
        }

        if (!StringUtils.isBlank(request.getBio())) {
            profile.setBio(request.getBio());
        }

        if (!StringUtils.isBlank(request.getPhotoUrl())) {
            profile.setPhotoUrl(request.getPhotoUrl());
        }

        if (!StringUtils.isBlank(request.getCoverPhotoUrl())) {
            profile.setCoverPhotoUrl(request.getCoverPhotoUrl());
        }

        if (request.getLocation() != null) {
            if (profile.getLocation() == null) {
                profile.setLocation(new UserProfile.Location());
            }
            if (!StringUtils.isBlank(request.getLocation().getCity())) {
                profile.getLocation().setCity(request.getLocation().getCity());
            }
            if (!StringUtils.isBlank(request.getLocation().getCountry())) {
                profile.getLocation().setCountry(request.getLocation().getCountry());
            }
        }

        var updatedUser = userProfileRepository.save(user);

        eventPublisher.publishEvent(new UserUpdatedEvent(updatedUser.getId(), UserEventPayload.from(updatedUser)));

        return SuccessResponse.<UserProfileResponse>builder()
                .message("User profile updated successfully")
                .data(mapToProfileResponse(updatedUser))
                .build();
    }

    private @NonNull UserProfile getUserProfile(String userId) {
        return userProfileRepository.findByIdAndIsActiveTrueAndIsDeletedFalse(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
    }

    @Override
    public Page<UserProfileResponse> getProfiles(Pageable pageable) {
        return userProfileRepository.findByIsActiveTrueAndIsDeletedFalse(pageable)
                .map(this::mapToProfileResponse);
    }

    @Override
    public Page<UserProfileResponse> getNonAdminProfiles(Pageable pageable) {
        return userProfileRepository.findByIsActiveTrueAndIsDeletedFalseAndRole(Role.USER, pageable)
                .map(this::mapToProfileResponse);
    }

    private UserProfileResponse mapToProfileResponse(UserProfile user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getCredentials().getEmail())
                .username(user.getCredentials().getUsername())
                .role(user.getRole().name())
                .profile(UserProfileResponse.Profile.builder()
                        .fullName(user.getProfile().getFullName())
                        .bio(user.getProfile().getBio())
                        .photoUrl(user.getProfile().getPhotoUrl())
                        .coverPhotoUrl(user.getProfile().getCoverPhotoUrl())
                        .location(user.getProfile().getLocation() != null
                                ? UserProfileResponse.Location.builder()
                                  .city(user.getProfile().getLocation().getCity())
                                  .country(user.getProfile().getLocation().getCountry())
                                  .build()
                                : null)
                        .build())
                .social(UserProfileResponse.Social.builder()
                        .website(user.getSocial().getWebsite())
                        .githubUrl(user.getSocial().getGithubUrl())
                        .linkedinUrl(user.getSocial().getLinkedinUrl())
                        .facebookUrl(user.getSocial().getFacebookUrl())
                        .twitterUrl(user.getSocial().getTwitterUrl())
                        .build())
                .build();
    }

    private String resolveFullName(String currentFullName, String firstName, String lastName) {
        String currentFirst = "";
        String currentLast = "";
        if (!StringUtils.isBlank(currentFullName)) {
            int lastSpace = currentFullName.lastIndexOf(' ');
            if (lastSpace > 0) {
                currentFirst = currentFullName.substring(0, lastSpace);
                currentLast = currentFullName.substring(lastSpace + 1);
            } else {
                currentFirst = currentFullName;
            }
        }
        String resolvedFirst = firstName != null ? firstName : currentFirst;
        String resolvedLast = lastName != null ? lastName : currentLast;
        return (resolvedFirst + " " + resolvedLast).trim();
    }

}

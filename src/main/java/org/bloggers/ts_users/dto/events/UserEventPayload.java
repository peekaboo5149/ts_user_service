package org.bloggers.ts_users.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bloggers.ts_users.entities.UserProfile;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventPayload {

    private String id;
    private String email;
    private String username;
    private String role;
    private boolean active;
    private boolean deleted;
    private Profile profile;
    private Social social;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserEventPayload from(UserProfile user) {
        if (user == null) {
            return null;
        }

        return UserEventPayload.builder()
                .id(user.getId())
                .email(user.getCredentials() != null ? user.getCredentials().getEmail() : null)
                .username(user.getCredentials() != null ? user.getCredentials().getUsername() : null)
                .role(user.getRole() != null ? user.getRole().name() : null)
                .active(user.isActive())
                .deleted(user.isDeleted())
                .profile(user.getProfile() != null ? Profile.builder()
                        .fullName(user.getProfile().getFullName())
                        .bio(user.getProfile().getBio())
                        .photoUrl(user.getProfile().getPhotoUrl())
                        .coverPhotoUrl(user.getProfile().getCoverPhotoUrl())
                        .location(user.getProfile().getLocation() != null
                                ? Location.builder()
                                .city(user.getProfile().getLocation().getCity())
                                .country(user.getProfile().getLocation().getCountry())
                                .build()
                                : null)
                        .build() : null)
                .social(user.getSocial() != null ? Social.builder()
                        .website(user.getSocial().getWebsite())
                        .githubUrl(user.getSocial().getGithubUrl())
                        .linkedinUrl(user.getSocial().getLinkedinUrl())
                        .facebookUrl(user.getSocial().getFacebookUrl())
                        .twitterUrl(user.getSocial().getTwitterUrl())
                        .build() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private String fullName;
        private String bio;
        private String photoUrl;
        private String coverPhotoUrl;
        private Location location;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private String city;
        private String country;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Social {
        private String website;
        private String githubUrl;
        private String linkedinUrl;
        private String facebookUrl;
        private String twitterUrl;
    }
}

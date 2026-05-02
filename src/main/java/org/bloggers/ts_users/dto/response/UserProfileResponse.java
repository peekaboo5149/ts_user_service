package org.bloggers.ts_users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class UserProfileResponse {

    private String id;

    private String email;
    private String username;

    private String role;

    @Builder.Default
    private Profile profile = new Profile();

    @Builder.Default
    private Social social = new Social();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private String fullName;
        private String bio;
        private String photoUrl;
        private String coverPhotoUrl;
        private Location location = new Location();
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
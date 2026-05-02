package org.bloggers.ts_users.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_profiles")
@CompoundIndex(name = "unique_email_idx", def = "{ 'credentials.email': 1 }", unique = true)
@CompoundIndex(name = "unique_username_idx", def = "{ 'credentials.username': 1 }", unique = true)
@CompoundIndex(
        name = "active_not_deleted_idx",
        def = "{ 'is_active': 1, 'is_deleted': 1 }",
        partialFilter = "{ 'is_active': true, 'is_deleted': false }"
)
@CompoundIndex(
        name = "active_admin_only_idx",
        def = "{ 'role': 1, 'is_active': 1, 'is_deleted': 1 }",
        partialFilter = "{ 'role': 'ADMIN', 'is_active': true, 'is_deleted': false }"
)
public class UserProfile {

    @Id
    private String id;

    private Credentials credentials;

    @Builder.Default
    private Social social = new Social();

    @Field("profile_info")
    @Builder.Default
    private Profile profile = new Profile();

    @Field("is_active")
    @Builder.Default
    private boolean isActive = true;

    @Field("is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    private Role role;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Version
    private Long version;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Credentials {

        private String email;

        private String username;

        @Field("password_hash")
        private String passwordHash;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private String bio;

        @Field("full_name")
        private String fullName;

        @Field("photo_url")
        private String photoUrl;

        @Field("cover_photo_url")
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

        @Indexed(sparse = true)
        @Field("github_url")
        private String githubUrl;

        @Field("linkedin_url")
        private String linkedinUrl;

        @Field("facebook_url")
        private String facebookUrl;

        @Field("twitter_url")
        private String twitterUrl;
    }

}
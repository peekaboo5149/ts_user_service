package org.bloggers.ts_users.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Pattern(
            regexp = "^(https?://).*",
            message = "Photo URL must be a valid URL starting with http or https"
    )
    private String photoUrl;

    @Pattern(
            regexp = "^(https?://).*",
            message = "Cover photo URL must be a valid URL starting with http or https"
    )
    private String coverPhotoUrl;

    @Valid
    private LocationRequest location;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationRequest {

        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        private String city;

        @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
        private String country;
    }
}
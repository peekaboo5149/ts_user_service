package org.bloggers.ts_users.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreatedResponse {
    private String id;
    private String username;
}

package org.bloggers.ts_users.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SuccessResponse<T> {
    private String message;
    private T data;

    @Builder.Default
    private Instant timestamp = Instant.now();

    public static <T> SuccessResponse<T> success(T data) {
        return SuccessResponse.<T>builder()
                .message("Success")
                .data(data).build();
    }

    public static <T> SuccessResponse<T> success(String message, T data) {
        return SuccessResponse.<T>builder().message(message).data(data).build();
    }
}

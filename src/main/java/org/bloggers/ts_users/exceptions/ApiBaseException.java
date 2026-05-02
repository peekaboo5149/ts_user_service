package org.bloggers.ts_users.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
abstract class ApiBaseException extends RuntimeException {
    protected final HttpStatus status;

    public ApiBaseException(HttpStatus status, String message) {
        super(message);
        this.status = status;

    }
}

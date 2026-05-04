package org.bloggers.ts_users.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
abstract class ApiBaseException extends RuntimeException {
    protected final HttpStatus status;
    protected final String detailedErrorMessage;

    public ApiBaseException(HttpStatus status, String message) {
        this(status, message, null);

    }

    public ApiBaseException(HttpStatus status, String message, String detailedErrorMessage) {
        this.status = status;
        this.detailedErrorMessage = detailedErrorMessage;
        super(message);

    }
}

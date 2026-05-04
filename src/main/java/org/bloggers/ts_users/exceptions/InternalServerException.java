package org.bloggers.ts_users.exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ApiBaseException {
    public InternalServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public InternalServerException(String message, String detailedErrorMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, detailedErrorMessage);
    }
}

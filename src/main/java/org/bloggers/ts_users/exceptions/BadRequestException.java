package org.bloggers.ts_users.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiBaseException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}

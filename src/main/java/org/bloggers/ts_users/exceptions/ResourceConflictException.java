package org.bloggers.ts_users.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends ApiBaseException {

    public ResourceConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}

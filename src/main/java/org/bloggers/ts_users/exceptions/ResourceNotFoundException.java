package org.bloggers.ts_users.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiBaseException {
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

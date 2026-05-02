package org.bloggers.ts_users.exceptions;

import org.springframework.http.HttpStatus;

public class OperationOnDisabledResourceException extends ApiBaseException {

    public OperationOnDisabledResourceException(String message) {
        super(HttpStatus.NOT_ACCEPTABLE, message);
    }
}

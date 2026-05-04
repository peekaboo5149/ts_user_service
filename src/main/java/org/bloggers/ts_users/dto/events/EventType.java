package org.bloggers.ts_users.dto.events;

import java.io.Serial;
import java.io.Serializable;

public enum EventType implements Serializable {
    USER_CREATED,
    USER_DELETED,
    USER_UPDATED;

    @Serial
    private static final long serialVersionUID = 2L;
}

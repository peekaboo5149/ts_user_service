package org.bloggers.ts_users.dto.events;

import java.time.Instant;
import java.util.UUID;

public class UserDeletedEvent extends UserEvent<UserEventPayload> {

    public UserDeletedEvent(String userId, UserEventPayload payload) {
        this(userId, UUID.randomUUID().toString(), Instant.now(), payload);
    }

    public UserDeletedEvent(String userId, String eventId, Instant occurredAt, UserEventPayload payload) {
        super(EventType.USER_DELETED, userId, eventId, occurredAt, payload);
    }
}

package org.bloggers.ts_users.dto.events;

import java.time.Instant;
import java.util.UUID;

public class UserCreatedEvent extends UserEvent<UserEventPayload> {

    public UserCreatedEvent(String userId, UserEventPayload payload) {
        this(userId, UUID.randomUUID().toString(), Instant.now(), payload);
    }

    public UserCreatedEvent(String userId, String eventId, Instant occurredAt, UserEventPayload payload) {
        super(EventType.USER_CREATED, userId, eventId, occurredAt, payload);
    }
}

package org.bloggers.ts_users.dto.events;

import java.time.Instant;
import java.util.UUID;

public class UserUpdatedEvent extends UserEvent<UserEventPayload> {

    public UserUpdatedEvent(String userId, UserEventPayload payload) {
        this(userId, UUID.randomUUID().toString(), Instant.now(), payload);
    }

    public UserUpdatedEvent(String userId, String eventId, Instant occurredAt, UserEventPayload payload) {
        super(EventType.USER_UPDATED, userId, eventId, occurredAt, payload);
    }
}

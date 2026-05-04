package org.bloggers.ts_users.dto.events;

import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class UserEvent<E> {

    protected final EventType type;
    protected final String userId;
    protected final String eventId;
    protected final Instant occurredAt;
    protected final E payload;

    protected UserEvent(EventType type, String userId, String eventId, Instant occurredAt, E payload) {
        this.type = type;
        this.userId = userId;
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.payload = payload;
    }
}

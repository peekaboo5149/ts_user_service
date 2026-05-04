package org.bloggers.ts_users.dto.events;

import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Builder
public class EventEnvelope<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String eventId;
    private final EventType eventType;
    private final Instant occurredAt;
    private final T payload;
}
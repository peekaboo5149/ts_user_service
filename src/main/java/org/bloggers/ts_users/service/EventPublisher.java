package org.bloggers.ts_users.service;

import org.bloggers.ts_users.dto.events.UserEvent;

@FunctionalInterface
public interface EventPublisher {

    <E extends UserEvent<?>> void publishEvent(E event);

}

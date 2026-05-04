package org.bloggers.ts_users.listener;

import org.bloggers.ts_users.dto.events.UserEvent;
import org.bloggers.ts_users.service.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public <E extends UserEvent<?>> void publishEvent(E event) {
        applicationEventPublisher.publishEvent(event);
    }
}

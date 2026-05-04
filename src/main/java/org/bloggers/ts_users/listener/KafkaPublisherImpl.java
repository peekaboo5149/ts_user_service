package org.bloggers.ts_users.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bloggers.ts_users.config.UserEventsProperties;
import org.bloggers.ts_users.dto.events.EventEnvelope;
import org.bloggers.ts_users.dto.events.UserEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class KafkaPublisherImpl {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserEventsProperties userEventsProperties;
    private final ObjectMapper objectMapper;

    @Async
    @EventListener
    void publish(UserEvent<?> event) {
        var envelope = EventEnvelope.builder()
                .eventId(event.getEventId())
                .eventType(event.getType())
                .occurredAt(event.getOccurredAt())
                .payload(event.getPayload())
                .build();

        String topic = userEventsProperties.getTopicName();
        log.info("Publishing event to Kafka topic '{}': type={}, eventId={}, userId={}",
                topic, event.getType(), event.getEventId(), event.getUserId());

        try {
            String payload = objectMapper.writeValueAsString(envelope);
            kafkaTemplate.send(topic, event.getUserId(), payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event envelope for userId={}", event.getUserId(), e);
            throw new RuntimeException("Event serialization failed", e);
        }
    }
}

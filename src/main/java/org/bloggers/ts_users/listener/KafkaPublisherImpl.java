package org.bloggers.ts_users.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bloggers.ts_users.config.UserEventsProperties;
import org.bloggers.ts_users.dto.events.EventEnvelope;
import org.bloggers.ts_users.dto.events.UserEvent;
import org.bloggers.ts_users.exceptions.InternalServerException;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
class KafkaPublisherImpl {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserEventsProperties userEventsProperties;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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

            var message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, event.getUserId())
                    .setHeader("eventType", event.getType().name())
                    .build();
            kafkaTemplate.send(message).get();
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event envelope for userId={}", event.getUserId(), e);
            throw new InternalServerException("Event serialization failed", e.getMessage());
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to send event to Kafka topic={} for userId={}", topic, event.getUserId(), e);
            throw new InternalServerException("Failed to send event to Kafka", e.getMessage());
        }
    }
}

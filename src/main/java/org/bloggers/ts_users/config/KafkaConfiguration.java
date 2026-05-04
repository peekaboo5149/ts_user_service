package org.bloggers.ts_users.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
class KafkaConfiguration {

    @Bean
    ProducerFactory<String, String> producerFactory(KafkaProperties kafkaProperties) {
        var config = kafkaProperties.buildProducerProperties();
        return new DefaultKafkaProducerFactory<>(
                config,
                new StringSerializer(),
                new StringSerializer()
        );
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Profile("local")
    NewTopic userEventsTopic(UserEventsProperties userEventsProperties) {
        return TopicBuilder.name(userEventsProperties.getTopicName())
                .partitions(3)
                .replicas(1)
                .build();
    }
}

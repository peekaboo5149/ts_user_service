package org.bloggers.ts_users.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.events.user-events")
public class UserEventsProperties {

    private String topicName;
}

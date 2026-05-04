package org.bloggers.ts_users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ConfigurationPropertiesScan("org.bloggers.ts_users.config")
@SpringBootApplication
public class TsUserServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(TsUserServiceApplication.class, args);
    }

}

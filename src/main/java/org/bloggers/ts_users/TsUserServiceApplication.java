package org.bloggers.ts_users;

import org.bloggers.ts_users.config.MandatoryHeadersProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(MandatoryHeadersProperties.class)
@SpringBootApplication
public class TsUserServiceApplication {

    static void main(String[] args) {
        SpringApplication.run(TsUserServiceApplication.class, args);
    }

}

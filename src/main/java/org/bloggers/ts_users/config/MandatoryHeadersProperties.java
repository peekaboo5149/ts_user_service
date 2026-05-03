package org.bloggers.ts_users.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "mandatory.headers")
public class MandatoryHeadersProperties {

    private List<String> required = new ArrayList<>();
    private List<String> routesToSkip = new ArrayList<>();
    private List<String> startsWith = new ArrayList<>();
}

package ru.floda.home.rustshop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "rcon.connection")
public class RconConfig {
    private int timeout = 5000;
    private int maxRetries = 3;
    private int retryDelay = 1000;
}

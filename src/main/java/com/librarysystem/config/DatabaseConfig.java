package com.librarysystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "database")
public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}

package com.larkin.defcode.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "smpp")
@Getter
@Setter
public class SmsConfig {
    private String host;
    private int port;
    private String systemId;
    private String password;
    private String systemType;
    private String sourceAddr;
}

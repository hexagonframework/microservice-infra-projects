package io.github.hexagonframework.microservice.infra.gateway.ratelimit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

/**
 * @author Marcos Barbero
 */
@Data
@ConfigurationProperties(RateLimitProperties.PREFIX)
public class RateLimitProperties {

    public static final String PREFIX = "zuul.ratelimit";

    private Map<String, RateLimitPolicy> policies = new LinkedHashMap<>();
    private boolean enabled;
    private boolean behindProxy;
}
package io.github.hexagonframework.microservice.infra.gateway.errorlimit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Xuegui Yuan
 */
@Data
@ConfigurationProperties(ErrorLimitProperties.PREFIX)
public class ErrorLimitProperties {

    public static final String PREFIX = "zuul.errorlimit";

    private Map<String, ErrorLimitPolicy> policies = new LinkedHashMap<>();
    private boolean enabled;
    private boolean behindProxy;
}
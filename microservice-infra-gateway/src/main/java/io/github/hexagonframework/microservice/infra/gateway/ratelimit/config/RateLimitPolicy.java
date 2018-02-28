package io.github.hexagonframework.microservice.infra.gateway.ratelimit.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A policy is used to define rate limit constraints within ErrorLimiter implementations
 *
 * @author Marcos Barbero
 * @author Michal Šváb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitPolicy {
    private Long refreshInterval = 60L;
    private Long limit;
    private List<Type> type = new ArrayList<>();

    public enum Type {
        ORIGIN, USER, URL, DEVICE
    }

}

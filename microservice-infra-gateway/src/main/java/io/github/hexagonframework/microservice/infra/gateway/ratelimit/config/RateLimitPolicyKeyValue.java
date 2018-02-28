package io.github.hexagonframework.microservice.infra.gateway.ratelimit.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author Xuegui Yuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitPolicyKeyValue {
    @NonNull
    private String key;

    @NonNull
    private RateLimitPolicy rateLimitPolicy;
}

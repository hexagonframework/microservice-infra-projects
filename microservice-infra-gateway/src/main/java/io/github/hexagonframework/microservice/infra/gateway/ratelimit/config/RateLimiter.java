package io.github.hexagonframework.microservice.infra.gateway.ratelimit.config;

/**
 * @author Marcos Barbero
 */
public interface RateLimiter {

    /**
     * @param rateLimitPolicy - Template for which rates should be created in case there's no rate limit associated with the key
     * @param key    - Unique key that identifies a request
     * @return a view of a user's rate request limit
     */
    LimitRate consume(RateLimitPolicy rateLimitPolicy, String key);
}

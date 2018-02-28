package io.github.hexagonframework.microservice.infra.gateway.errorlimit.config;

/**
 * @author Xuegui Yuan
 */
public interface ErrorLimiter {

    /**
     * @param errorLimitPolicy - Template for which rates should be created in case there's no rate limit associated with the key
     * @param key    - Unique key that identifies a request
     * @return a view of a user's rate request limit
     */
    ErrorRate consume(ErrorLimitPolicy errorLimitPolicy, String key);

    ErrorRate query(ErrorLimitPolicy errorLimitPolicy, String key);
}

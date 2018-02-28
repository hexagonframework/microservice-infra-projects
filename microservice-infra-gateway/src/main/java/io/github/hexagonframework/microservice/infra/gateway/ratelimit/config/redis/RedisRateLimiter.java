package io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.redis;

import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.RateLimitPolicy;
import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.LimitRate;
import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.RateLimiter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Marcos Barbero
 */
public class RedisRateLimiter implements RateLimiter {
    private final RedisTemplate template;

    public RedisRateLimiter(final RedisTemplate template) {
        Assert.notNull(template, "RedisTemplate cannot be null");
        this.template = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public LimitRate consume(final RateLimitPolicy rateLimitPolicy, final String key) {
        final Long limit = rateLimitPolicy.getLimit();
        final Long refreshInterval = rateLimitPolicy.getRefreshInterval();
        final Long current = this.template.boundValueOps(key).increment(1L);
        Long expire = this.template.getExpire(key);
        if (expire == null || expire == -1) {
            this.template.expire(key, refreshInterval, SECONDS);
            expire = refreshInterval;
        }
        return new LimitRate(limit, Math.max(-1, limit - current), SECONDS.toMillis(expire));
    }
}

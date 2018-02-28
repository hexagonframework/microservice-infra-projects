package io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.redis;

import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.ErrorLimitPolicy;
import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.ErrorLimiter;
import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.ErrorRate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Xuegui Yuan
 */
public class RedisErrorLimiter implements ErrorLimiter {
    private final RedisTemplate template;

    public RedisErrorLimiter(final RedisTemplate template) {
        Assert.notNull(template, "RedisTemplate cannot be null");
        this.template = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ErrorRate consume(final ErrorLimitPolicy errorLimitPolicy, final String key) {
        final Long limit = errorLimitPolicy.getLimit();
        final Long refreshInterval = errorLimitPolicy.getRefreshInterval();
        final String k = "ERR_" + key;
        final Long current = this.template.boundValueOps(k).increment(1L);
        Long expire = this.template.getExpire(k);
        if (expire == null || expire == -1) {
            this.template.expire(k, refreshInterval, SECONDS);
            expire = refreshInterval;
        }
        return new ErrorRate(limit, Math.max(-1, limit - current), SECONDS.toMillis(expire));
    }

    @Override
    public ErrorRate query(ErrorLimitPolicy errorLimitPolicy, String key) {
        final Long limit = errorLimitPolicy.getLimit();
        final String k = "ERR_" + key;
        Object val = this.template.boundValueOps(k).get();
        if (val == null) {
            return null;
        }
        Long current = Long.parseLong((String) val);
        if (current == null) current = 0L;
        Long expire = this.template.getExpire(k);
        return new ErrorRate(limit, Math.max(-1, limit - current), SECONDS.toMillis(expire));
    }


}

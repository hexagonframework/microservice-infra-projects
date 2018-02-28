package io.github.hexagonframework.microservice.infra.gateway.ratelimit.config;

import io.github.hexagonframework.microservice.infra.gateway.ratelimit.RateLimitFilter;
import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.redis.RedisRateLimiter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Marcos Barbero
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = RateLimitProperties.PREFIX, name = "enabled", havingValue = "true")
public class RateLimitAutoConfiguration {

    @ConditionalOnClass(RedisTemplate.class)
    public static class RedisConfiguration {
        @Bean
        public RateLimiter rateLimiter(RedisTemplate redisTemplate) {
            return new RedisRateLimiter(redisTemplate);
        }
    }

    @Bean
    public RateLimitFilter rateLimiterFilter(RateLimiter rateLimiter,
                                             RateLimitProperties rateLimitProperties,
                                             RouteLocator routeLocator) {
        return new RateLimitFilter(rateLimiter, rateLimitProperties, routeLocator);
    }

}

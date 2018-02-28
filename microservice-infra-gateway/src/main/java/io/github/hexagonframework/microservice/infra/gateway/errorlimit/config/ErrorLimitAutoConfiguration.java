package io.github.hexagonframework.microservice.infra.gateway.errorlimit.config;

import io.github.hexagonframework.microservice.infra.gateway.errorlimit.ErrorLimitPostFilter;
import io.github.hexagonframework.microservice.infra.gateway.errorlimit.ErrorLimitPreFilter;
import io.github.hexagonframework.microservice.infra.gateway.errorlimit.config.redis.RedisErrorLimiter;
import io.github.hexagonframework.microservice.infra.gateway.ratelimit.config.RateLimitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author Xuegui Yuan
 */
@Configuration
@EnableConfigurationProperties(ErrorLimitProperties.class)
@ConditionalOnProperty(prefix = RateLimitProperties.PREFIX, name = "enabled", havingValue = "true")
public class ErrorLimitAutoConfiguration {

    @ConditionalOnClass(RedisTemplate.class)
    public static class RedisConfiguration {
        @Bean
        public StringRedisTemplate redisTemplate(RedisConnectionFactory cf) {
            return new StringRedisTemplate(cf);
        }

        @Bean
        public ErrorLimiter errorLimiter(RedisTemplate redisTemplate) {
            return new RedisErrorLimiter(redisTemplate);
        }
    }

    @Bean
    public ErrorLimitPreFilter errorLimitPreFilter(ErrorLimiter errorLimiter,
                                                   ErrorLimitProperties rateLimitProperties,
                                                   RouteLocator routeLocator) {
        return new ErrorLimitPreFilter(errorLimiter, rateLimitProperties, routeLocator);
    }

    @Bean
    public ErrorLimitPostFilter errorLimitPostFilter(ErrorLimiter errorLimiter,
                                                     ErrorLimitProperties rateLimitProperties,
                                                     RouteLocator routeLocator) {
        return new ErrorLimitPostFilter(errorLimiter, rateLimitProperties, routeLocator);
    }

}

package io.github.hexagonframework.microservice.infra.gateway.ratelimit.config;

import io.github.hexagonframework.microservice.infra.gateway.exception.ErrorCodeException;
import org.springframework.http.HttpStatus;

/**
 * @author Xuegui Yuan
 */
public class RateLimitException extends ErrorCodeException {

    public RateLimitException() {
        super("请求频次超过上限", HttpStatus.TOO_MANY_REQUESTS.value(), "RateLimitException", "410002");
    }
}

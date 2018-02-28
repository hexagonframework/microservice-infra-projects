package io.github.hexagonframework.microservice.infra.gateway.errorlimit.config;

import io.github.hexagonframework.microservice.infra.gateway.exception.ErrorCodeException;
import org.springframework.http.HttpStatus;

/**
 * @author Xuegui Yuan
 */
public class ErrorLimitException extends ErrorCodeException {

    public ErrorLimitException() {
        super("错误次数超过上限", HttpStatus.TOO_MANY_REQUESTS.value(), "ErrorLimitException", "410003");
    }

}

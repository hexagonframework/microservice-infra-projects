package io.github.hexagonframework.microservice.infra.uaa.client;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

/**
 * 自定义InvalidTokenException，覆盖JsonSerialize，用于自定义InvalidTokenException JSON响应.
 *
 * @author Xuegui Yuan
 */
@JsonSerialize(using = CustomInvalidTokenExceptionSerializer.class)
public class CustomInvalidTokenException extends InvalidTokenException {
  public CustomInvalidTokenException(String msg) {
    super(msg);
  }

  public CustomInvalidTokenException(String msg, Throwable t) {
    super(msg, t);
  }
}

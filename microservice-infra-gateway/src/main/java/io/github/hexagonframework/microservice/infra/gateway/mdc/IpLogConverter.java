package io.github.hexagonframework.microservice.infra.gateway.mdc;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.hexagonframework.microservice.infra.gateway.utils.IpUtils;

/**
 * @author Xuegui Yuan
 */
public class IpLogConverter extends ClassicConverter {
  @Override
  public String convert(ILoggingEvent iLoggingEvent) {
    return IpUtils.getLocalIp();
  }
}

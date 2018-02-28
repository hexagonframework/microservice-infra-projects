package io.github.hexagonframework.microservice.infra.gateway.errorlimit.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A policy is used to define rate limit constraints within ErrorLimiter implementations
 *
 * @author Xuegui Yuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLimitPolicy {
    private Long refreshInterval = 60L;
    private Long limit;
    private String errorCode;
    private List<Type> type = new ArrayList<>();

    public enum Type {
        ORIGIN, USER, DEVICE
    }

}

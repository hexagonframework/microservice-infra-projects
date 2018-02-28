package io.github.hexagonframework.microservice.infra.gateway.errorlimit.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author Xuegui Yuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorPolicyKeyValue {
    @NonNull
    private String key;

    @NonNull
    private ErrorLimitPolicy errorLimitPolicy;
}

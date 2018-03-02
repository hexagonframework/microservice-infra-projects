package io.github.hexagonframework.microservice.infra.uaa.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 应用自定义UserInfoTokenService Configuration.
 *
 * @author Xuegui Yuan
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CustomUserInfoTokenServiceConfiguration.class)
public @interface EnableCustomUserInfoTokenService {
}

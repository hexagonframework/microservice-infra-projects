package io.github.hexagonframework.microservice.infra.uaa.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

/**
 * 自定义Oauth2FeignRequestInterceptor Configuration.
 *
 * @author Xuegui Yuan
 */
@Configuration
public class CustomOAuth2FeignRequestInterceptorConfiguration {

  @Bean
  public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ProtectedResourceDetails oauth2RemoteResource,
                                                          OAuth2ClientContext oauth2ClientContext) {
    return new CustomOAuth2FeignRequestInterceptor(oauth2ClientContext, oauth2RemoteResource);
  }

}

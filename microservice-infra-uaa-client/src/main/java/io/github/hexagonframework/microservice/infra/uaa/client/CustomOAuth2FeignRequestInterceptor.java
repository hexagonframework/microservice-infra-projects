package io.github.hexagonframework.microservice.infra.uaa.client;

import feign.RequestTemplate;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

/**
 * 自定义OAuth2FeignRequestInterceptor
 * 当headers已包含Authorization头时不应用拦截.
 *
 * @author Xuegui Yuan
 */
public class CustomOAuth2FeignRequestInterceptor extends OAuth2FeignRequestInterceptor {

  public CustomOAuth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext, OAuth2ProtectedResourceDetails resource) {
    super(oAuth2ClientContext, resource);
  }

  public CustomOAuth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext, OAuth2ProtectedResourceDetails resource, String tokenType, String header) {
    super(oAuth2ClientContext, resource, tokenType, header);
  }

  @Override
  public void apply(RequestTemplate template) {
    if (!template.headers().containsKey(OAuth2FeignRequestInterceptor.AUTHORIZATION)) {
      super.apply(template);
    }
  }
}

package io.github.hexagonframework.microservice.infra.uaa.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 * 自定义UserInfoTokenService Configuration.
 *
 * @author Xuegui Yuan
 */
@Configuration
@EnableConfigurationProperties
public class CustomUserInfoTokenServiceConfiguration {

  @Bean
  public ResourceServerTokenServices tokenServices(ResourceServerProperties sso) {
    return new CustomUserInfoTokenServices(sso.getUserInfoUri(), sso.getClientId());
  }

  @Bean
  @ConditionalOnProperty(value = "endpoints.refresh-server-token.enabled", matchIfMissing = true)
  public RefreshServerTokenEndpoint refreshServerTokenEndpoint(
      OAuth2ClientContext oauth2ClientContext) {
    return new RefreshServerTokenEndpoint(oauth2ClientContext);
  }

  @Bean
  @ConditionalOnBean( {RefreshServerTokenEndpoint.class})
  public RefreshServerTokenMvnEndpoint refreshServerTokenMvnEndpoint(
      RefreshServerTokenEndpoint refreshServerTokenEndpoint) {
    return new RefreshServerTokenMvnEndpoint(refreshServerTokenEndpoint);
  }

  @Bean
  @ConditionalOnProperty(value = "endpoints.server-token.enabled", matchIfMissing = true)
  public ServerTokenEndpoint serverTokenEndpoint(
      OAuth2ClientContext oAuth2ClientContext) {
    return new ServerTokenEndpoint(oAuth2ClientContext);
  }

  @Bean
  @ConditionalOnBean( {ServerTokenEndpoint.class})
  public ServerTokenMvnEndpoint serverTokenMvnEndpoint(
      ServerTokenEndpoint serverTokenEndpoint) {
    return new ServerTokenMvnEndpoint(serverTokenEndpoint);
  }
}

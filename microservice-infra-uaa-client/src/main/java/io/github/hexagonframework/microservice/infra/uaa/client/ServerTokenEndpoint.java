package io.github.hexagonframework.microservice.infra.uaa.client;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * 获取资源服务TOKEN.
 *
 * @author Xuegui Yuan
 */
@ConfigurationProperties("endpoints.refresh-server-token")
public class ServerTokenEndpoint extends AbstractEndpoint<OAuth2AccessToken> {

  private OAuth2ClientContext oAuth2ClientContext;

  public ServerTokenEndpoint(OAuth2ClientContext oAuth2ClientContext) {
    super("server_token", true, true);
    this.oAuth2ClientContext = oAuth2ClientContext;
  }

  @Override
  public OAuth2AccessToken invoke() {
    if (this.oAuth2ClientContext != null) {
      return this.oAuth2ClientContext.getAccessToken();
    }
    return null;
  }
}

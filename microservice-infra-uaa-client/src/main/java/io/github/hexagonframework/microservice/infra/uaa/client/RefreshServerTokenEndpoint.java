package io.github.hexagonframework.microservice.infra.uaa.client;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.OAuth2ClientContext;

/**
 * 刷新资源服务TOKEN.
 *
 * @author Xuegui Yuan
 */
@ConfigurationProperties("endpoints.refresh-server-token")
public class RefreshServerTokenEndpoint extends AbstractEndpoint<Boolean> {

  private OAuth2ClientContext oAuth2ClientContext;

  public RefreshServerTokenEndpoint(OAuth2ClientContext oAuth2ClientContext) {
    super("refresh_server_token", true, true);
    this.oAuth2ClientContext = oAuth2ClientContext;
  }

  @Override
  public Boolean invoke() {
    if (this.oAuth2ClientContext != null) {
      this.oAuth2ClientContext.setAccessToken(null);
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}

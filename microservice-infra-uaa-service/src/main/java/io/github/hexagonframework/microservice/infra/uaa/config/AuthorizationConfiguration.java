package io.github.hexagonframework.microservice.infra.uaa.config;

import io.github.hexagonframework.microservice.infra.uaa.service.UserDetailsServiceMongo;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @author Xuegui Yuan
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationConfiguration extends AuthorizationServerConfigurerAdapter {

  @Autowired(required = false)
  private RedisConnectionFactory redisConnectionFactory;

  @Autowired
  @Qualifier("authenticationManagerBean")
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserDetailsServiceMongo userDetailsService;

  @Autowired
  private Environment env;

  @Autowired(required = false)
  private DataSource dataSource;

  @Bean
  public TokenStore redisTokenStore() {
    return new RedisTokenStore(redisConnectionFactory);
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    oauthServer
        .tokenKeyAccess("permitAll()")
        .checkTokenAccess("isAuthenticated()")
        .allowFormAuthenticationForClients();
  }

  @Bean
  public ClientDetailsService jdbcClientDetailsService() {
    return new JdbcClientDetailsService(dataSource);
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(jdbcClientDetailsService());
    // @formatter:off
//    clients.inMemory()
//        .withClient("app")
//        .secret("jkgj")
//        .authorizedGrantTypes("refresh_token", "password", "login_sms_code")
//        .scopes("ui")
//        .accessTokenValiditySeconds(30 * 24 * 60 * 60)
//        .and()
//        .withClient("service")
//        .secret("root")
//        .authorizedGrantTypes("client_credentials")
//        .scopes("server")
//        .accessTokenValiditySeconds(30 * 24 * 60 * 60);
    // @formatter:on
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints
        .tokenStore(redisTokenStore())
        .authenticationManager(authenticationManager)
        .userDetailsService(userDetailsService);
  }
}

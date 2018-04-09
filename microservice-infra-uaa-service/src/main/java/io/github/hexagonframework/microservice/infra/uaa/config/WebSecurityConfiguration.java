package io.github.hexagonframework.microservice.infra.uaa.config;

import io.github.hexagonframework.microservice.infra.uaa.service.UserDetailsServiceMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Xuegui Yuan
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsServiceMongo userDetailsService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
        .passwordEncoder(new BCryptPasswordEncoder());
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/webjars/**",
        "/images/**",
        "/oauth/uncache_approvals", "/oauth/cache_approvals");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http.csrf().disable();
    // for gateway access
    http.antMatcher("/**").authorizeRequests()
        .anyRequest().permitAll();
    // for management access with basic auth
    http.antMatcher("/actuator/**").authorizeRequests()
        .antMatchers("/actuator/**").authenticated()
        .and().httpBasic().realmName("Actuator API");
    // @formatter:on
  }
}

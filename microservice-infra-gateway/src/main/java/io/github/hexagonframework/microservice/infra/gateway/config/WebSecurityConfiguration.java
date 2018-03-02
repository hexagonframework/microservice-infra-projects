package io.github.hexagonframework.microservice.infra.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Xuegui Yuan
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        // for gateway access
        http.antMatcher("/**").authorizeRequests()
                .anyRequest().permitAll();
        // for management access with basic uaa
        http.antMatcher("/management/**").authorizeRequests()
                .antMatchers("/management/**").authenticated()
                .and().httpBasic().realmName("Management API");
    }
}

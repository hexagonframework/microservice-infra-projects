package io.github.hexagonframework.microservice.infra.uaa.service;

import io.github.hexagonframework.microservice.infra.uaa.domain.model.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * @author Xuegui Yuan
 */
@Data
public class CreateUserCommand {
  private String username;
  private String password;
  private String clientId;
  private List<String> authorities = new ArrayList<>();

  public User user() {
    List<GrantedAuthority> grantedAuthorities;
    if (!authorities.isEmpty()) {
      grantedAuthorities = authorities
          .stream()
          .map(role -> new SimpleGrantedAuthority(role))
          .collect(Collectors.toList());
    } else {
      grantedAuthorities =  Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    User user = new User(username, password);
    user.setAuthorities(grantedAuthorities);
    return user;
  }
}

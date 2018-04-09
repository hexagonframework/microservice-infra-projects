package io.github.hexagonframework.microservice.infra.uaa.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Xuegui Yuan
 */
@Document(collection = "users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class User implements UserDetails {
  @Id
  private long id;
  @NonNull
  private String username;
  @NonNull
  private String password;
  private String clientId;
  private List<GrantedAuthority> authorities = new ArrayList<>();
  private boolean accountNonExpired = true;
  private boolean accountNonLocked = true;
  private boolean credentialsNonExpired = true;
  private boolean enabled = true;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }
}

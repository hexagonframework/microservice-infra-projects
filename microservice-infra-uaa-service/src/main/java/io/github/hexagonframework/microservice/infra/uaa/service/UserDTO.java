package io.github.hexagonframework.microservice.infra.uaa.service;

import io.github.hexagonframework.microservice.infra.uaa.domain.model.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * @author Xuegui Yuan
 */
@Data
public class UserDTO {
  private String username;
  private List<String> authorities;

  public static UserDTO from(User user) {
    if (user == null) {
      return null;
    }
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername(user.getUsername());
    if (user.getAuthorities()!= null && !user.getAuthorities().isEmpty()) {
      userDTO.setAuthorities(user.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList()));
    }
    return userDTO;
  }
}

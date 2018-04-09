package io.github.hexagonframework.microservice.infra.uaa.service;

import java.util.List;
import lombok.Data;

/**
 * @author Xuegui Yuan
 */
@Data
public class GrantAuthoritiesCommand {
  private String username;
  private List<String> authorities;
}

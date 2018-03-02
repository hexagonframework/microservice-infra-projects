package io.github.hexagonframework.microservice.infra.uaa.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户信息.
 *
 * @author Xuegui Yuan
 */
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal {
  private String uid;
  private String mobile;
}

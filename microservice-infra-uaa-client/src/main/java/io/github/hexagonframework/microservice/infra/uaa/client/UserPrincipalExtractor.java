package io.github.hexagonframework.microservice.infra.uaa.client;

import java.util.Map;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

/**
 * 用户信息抽取器.
 *
 * @author Xuegui Yuan
 */
public class UserPrincipalExtractor implements PrincipalExtractor {

  @Override
  public Object extractPrincipal(Map<String, Object> map) {
    String uid = map.containsKey("uid") ? (String) map.get("uid") : "NA";
    String mobile = map.containsKey("mobile") ? (String) map.get("mobile") : "NA";
    return new UserPrincipal(uid, mobile);
  }

}

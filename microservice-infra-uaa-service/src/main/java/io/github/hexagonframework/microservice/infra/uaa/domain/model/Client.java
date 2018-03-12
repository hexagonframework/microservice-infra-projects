package io.github.hexagonframework.microservice.infra.uaa.domain.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Xuegui Yuan
 */
@Document(collection = "clients")
public class Client {

  @Id
  private String clientId;

  private String secret;

  private List<String> authorizedGrantTypes;

  private List<String> scopes;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public List<String> getAuthorizedGrantTypes() {
    return authorizedGrantTypes;
  }

  public void setAuthorizedGrantTypes(List<String> authorizedGrantTypes) {
    this.authorizedGrantTypes = authorizedGrantTypes;
  }

  public List<String> getScopes() {
    return scopes;
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

  @Override
  public String toString() {
    return "Client{" +
        "clientId='" + clientId + '\'' +
        ", secret='" + secret + '\'' +
        ", authorizedGrantTypes=" + authorizedGrantTypes +
        ", scopes=" + scopes +
        '}';
  }
}

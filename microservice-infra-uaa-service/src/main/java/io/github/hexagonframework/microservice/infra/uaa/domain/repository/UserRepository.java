package io.github.hexagonframework.microservice.infra.uaa.domain.repository;

import io.github.hexagonframework.microservice.infra.uaa.domain.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

  public User findUserByUsernameEquals(String username);
  
}

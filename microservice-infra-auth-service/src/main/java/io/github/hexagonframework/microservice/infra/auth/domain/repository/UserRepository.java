package io.github.hexagonframework.microservice.infra.auth.domain.repository;

import io.github.hexagonframework.microservice.infra.auth.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

}

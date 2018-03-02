package io.github.hexagonframework.microservice.infra.uaa.repository;

import io.github.hexagonframework.microservice.infra.uaa.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

}

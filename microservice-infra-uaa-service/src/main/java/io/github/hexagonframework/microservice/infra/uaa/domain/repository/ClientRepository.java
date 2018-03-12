package io.github.hexagonframework.microservice.infra.uaa.domain.repository;

import io.github.hexagonframework.microservice.infra.uaa.domain.model.Client;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Xuegui Yuan
 */
public interface ClientRepository extends CrudRepository<Client, String> {
}

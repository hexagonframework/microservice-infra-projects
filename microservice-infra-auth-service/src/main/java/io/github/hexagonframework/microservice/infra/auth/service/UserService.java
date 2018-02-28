package io.github.hexagonframework.microservice.infra.auth.service;

import io.github.hexagonframework.microservice.infra.auth.domain.User;

public interface UserService {

	void create(User user);

}

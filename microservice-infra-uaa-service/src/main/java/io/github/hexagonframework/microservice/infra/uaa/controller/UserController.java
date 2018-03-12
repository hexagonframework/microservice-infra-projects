package io.github.hexagonframework.microservice.infra.uaa.controller;

import io.github.hexagonframework.microservice.infra.uaa.domain.model.User;
import io.github.hexagonframework.microservice.infra.uaa.service.UserDetailsServiceMongo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserDetailsServiceMongo userService;

	@RequestMapping(value = "/me", method = RequestMethod.GET)
	public Map<String, Object> getUser(OAuth2Authentication authentication) {
		Map<String, Object> userInfo = new HashMap<>(6);
		userInfo.put("clientId", authentication.getOAuth2Request().getClientId());
		userInfo.put("isClientOnly", authentication.isClientOnly());
		userInfo.put("name", authentication.getName());
		if (! authentication.isClientOnly()) {
			User user = (User) authentication.getDetails();
			userInfo.put("userId", user.getId());
			userInfo.put("username", user.getUsername());
		}
		userInfo.put("authorities", authentication.getAuthorities());
		return userInfo;
	}

	@PreAuthorize("#oauth2.hasScope('server')")
	@RequestMapping(method = RequestMethod.POST)
	public void createUser(OAuth2Authentication authentication, @Valid @RequestBody User user) {
		user.setClientId(authentication.getOAuth2Request().getClientId());
		userService.create(user);
	}
}

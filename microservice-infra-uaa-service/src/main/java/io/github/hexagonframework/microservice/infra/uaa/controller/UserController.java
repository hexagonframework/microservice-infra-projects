package io.github.hexagonframework.microservice.infra.uaa.controller;

import io.github.hexagonframework.microservice.infra.uaa.domain.model.User;
import io.github.hexagonframework.microservice.infra.uaa.service.UserDetailsServiceMongo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.StringUtils;
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
		if (! authentication.isClientOnly()) {
			User user = (User) authentication.getUserAuthentication().getPrincipal();
			Map<String, Object> userInfoDetail = new HashMap<>(2);
			userInfoDetail.put("id", user.getId());
			userInfoDetail.put("username", user.getUsername());
			userInfo.put("user", userInfoDetail);
		}
		userInfo.put("authorities", authentication.getAuthorities());
		return userInfo;
	}

	@PreAuthorize("#oauth2.hasScope('server')")
	@RequestMapping(method = RequestMethod.POST)
	public void createUser(OAuth2Authentication authentication, @Valid @RequestBody User user) {
		if (StringUtils.isEmpty(user.getClientId())) {
			user.setClientId(authentication.getOAuth2Request().getClientId());
		}
		userService.create(user);
	}
}

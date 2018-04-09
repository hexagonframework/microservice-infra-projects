package io.github.hexagonframework.microservice.infra.uaa.controller;

import io.github.hexagonframework.microservice.infra.uaa.domain.model.User;
import io.github.hexagonframework.microservice.infra.uaa.service.CreateUserCommand;
import io.github.hexagonframework.microservice.infra.uaa.service.GrantAuthoritiesCommand;
import io.github.hexagonframework.microservice.infra.uaa.service.UserDTO;
import io.github.hexagonframework.microservice.infra.uaa.service.UserDetailsServiceMongo;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	@GetMapping("/me")
	public Map<String, Object> getUser(OAuth2Authentication authentication) {
		Map<String, Object> userInfo = new HashMap<>(6);
		userInfo.put("clientId", authentication.getOAuth2Request().getClientId());
		userInfo.put("scope", authentication.getOAuth2Request().getScope());
		userInfo.put("isClientOnly", authentication.isClientOnly());
		if (authentication.isClientOnly()) {
			userInfo.put("name", authentication.getName());
		}else {
			User user = (User) authentication.getUserAuthentication().getPrincipal();
			userInfo.put("id", user.getId());
		}
		userInfo.put("authorities", authentication.getAuthorities());
		return userInfo;
	}

	@PreAuthorize("#oauth2.hasScope('server')")
	@PostMapping()
	public void createUser(OAuth2Authentication authentication, @Valid @RequestBody CreateUserCommand user) {
		if (StringUtils.isEmpty(user.getClientId())) {
			user.setClientId(authentication.getOAuth2Request().getClientId());
		}
		userService.create(user);
	}

	@PreAuthorize("hasRole('admin')")
	@GetMapping("/{username}")
	public UserDTO getUser(OAuth2Authentication authentication, @PathVariable String username) {
		return userService.getUserByUsername(username);
	}

	@PreAuthorize("hasRole('admin')")
	@PutMapping("/{username}/authorities")
	public void grantAuthorities(OAuth2Authentication authentication,
								  @PathVariable String username,
									@Valid @RequestBody GrantAuthoritiesCommand grantAuthoritiesCommand) {
		grantAuthoritiesCommand.setUsername(username);
		userService.grantAuthorities(grantAuthoritiesCommand);
	}
}

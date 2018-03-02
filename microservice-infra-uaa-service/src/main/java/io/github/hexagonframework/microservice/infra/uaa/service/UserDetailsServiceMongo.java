package io.github.hexagonframework.microservice.infra.uaa.service;

import io.github.hexagonframework.microservice.infra.uaa.domain.User;
import io.github.hexagonframework.microservice.infra.uaa.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserDetailsServiceMongo implements UserDetailsService {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${security.user.name}")
	private String inMemUserName;

	@Value("${security.user.password}")
	private String inMemUserPassword;

	@Autowired
	private UserRepository repository;

	private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// In mem user for management api
		if (inMemUserName.equals(username)) {
			List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ACTUATOR"));
			return new org.springframework.security.core.userdetails.User(username, passwordEncoder.encode(inMemUserPassword), authorities);
		}

		User user = repository.findOne(username);

		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		return user;
	}

	public void create(User user) {

		User existing = repository.findOne(user.getUsername());
		Assert.isNull(existing, "user already exists: " + user.getUsername());

		String hash = passwordEncoder.encode(user.getPassword());
		user.setPassword(hash);

		repository.save(user);

		log.info("new user has been created: {}", user.getUsername());
	}
}

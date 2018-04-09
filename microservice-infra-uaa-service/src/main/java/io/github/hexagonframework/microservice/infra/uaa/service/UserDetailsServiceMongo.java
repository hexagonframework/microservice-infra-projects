package io.github.hexagonframework.microservice.infra.uaa.service;

import io.github.hexagonframework.microservice.infra.uaa.domain.model.User;
import io.github.hexagonframework.microservice.infra.uaa.domain.repository.UserRepository;
import io.github.hexagonframework.microservice.infra.uaa.domain.service.SequenceService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

	private static final String USER_ID_SEQ_KEY = "user_id";

	@Autowired
	private SequenceService userIdSequenceService;

	private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// In mem user for management api
		if (inMemUserName.equals(username)) {
			List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ACTUATOR"));
			return new org.springframework.security.core.userdetails.User(username, passwordEncoder.encode(inMemUserPassword), authorities);
		}

		User user = repository.findUserByUsernameEquals(username);

		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		return user;
	}

	public void create(CreateUserCommand createUserCommand) {

		User existing = repository.findUserByUsernameEquals(createUserCommand.getUsername());
		Assert.isNull(existing, "user already exists: " + createUserCommand.getUsername());

		String hash = passwordEncoder.encode(createUserCommand.getPassword());
		createUserCommand.setPassword(hash);

		User user = createUserCommand.user();
		user.setId(userIdSequenceService.getNextSequenceId(USER_ID_SEQ_KEY));

		repository.save(user);

		log.info("new user has been created: {}", user.getUsername());
	}

	public UserDTO getUserByUsername(String username) {
		User user = repository.findUserByUsernameEquals(username);
		return UserDTO.from(user);
	}

	public void grantAuthorities(GrantAuthoritiesCommand grantAuthoritiesCommand) {
		User existing = repository.findUserByUsernameEquals(grantAuthoritiesCommand.getUsername());
		Assert.notNull(existing, "user not exists: " + grantAuthoritiesCommand.getUsername());
		if (grantAuthoritiesCommand.getAuthorities() != null
				&& !grantAuthoritiesCommand.getAuthorities().isEmpty()) {
			existing.setAuthorities(grantAuthoritiesCommand.getAuthorities().stream()
			.map(a -> new SimpleGrantedAuthority(a)).collect(Collectors.toList()));
		} else {
			existing.setAuthorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
		}
		repository.save(existing);
	}
}

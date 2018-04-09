package io.github.hexagonframework.microservice.infra.uaa;

import io.github.hexagonframework.microservice.infra.uaa.domain.service.SequenceService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
//@EnableDiscoveryClient
public class UaaApplication {

	@Bean
	SequenceService userIdSequenceService(MongoOperations mongoOperations) {
		return new SequenceService(mongoOperations);
	}

	public static void main(String[] args) {
		SpringApplication.run(UaaApplication.class, args);
	}

}

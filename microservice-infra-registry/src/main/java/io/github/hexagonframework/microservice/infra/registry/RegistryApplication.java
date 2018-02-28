package io.github.hexagonframework.microservice.infra.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Xuegui Yuan
 */
@SpringBootApplication
@EnableEurekaServer
public class RegistryApplication {
  public static void main(String[] args) {
    SpringApplication.run(RegistryApplication.class, args);
  }
}

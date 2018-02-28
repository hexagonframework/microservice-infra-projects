package io.github.hexagonframework.microservice.infra.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@RefreshScope
//@EnableAtlas
public class GatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

//	@Bean
//	AtlasTagProvider atlasCommonTags(@Value("${spring.application.name}") String appName) {
//		return () -> Collections.singletonMap("app", appName);
//	}
//
//	@Bean
//	public CommandLineRunner registerExtMetrics(Registry registry) {
//		return new CommandLineRunner() {
//			@Override
//			public void run(String... strings) throws Exception {
//				Jmx.registerStandardMXBeans(registry);
//				Spectator.globalRegistry().add(registry);
//				GcLogger gc = new GcLogger();
//				gc.start(null);
//			}
//		};
//	}
}

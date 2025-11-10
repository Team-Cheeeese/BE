package com.cheeeese;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableRedisRepositories(basePackages = "com.cheeeese.auth.infrastructure.persistence")
@EnableScheduling
public class CheeeeseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheeeeseApplication.class, args);
	}

}

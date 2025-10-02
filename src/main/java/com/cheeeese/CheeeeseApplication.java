package com.cheeeese;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CheeeeseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheeeeseApplication.class, args);
	}

}

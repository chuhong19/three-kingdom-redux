package com.example.three_kingdom_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ThreeKingdomBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThreeKingdomBackendApplication.class, args);
	}

}

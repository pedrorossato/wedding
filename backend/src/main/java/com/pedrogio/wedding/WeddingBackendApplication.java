package com.pedrogio.wedding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WeddingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeddingBackendApplication.class, args);
	}

}

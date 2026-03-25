package com.example.SkipQ_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.skipq.backend")
@AutoConfigurationPackage(basePackages = "com.skipq.backend")
@EnableJpaRepositories(basePackages = "com.skipq.backend.repository")
public class SkipQBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkipQBackendApplication.class, args);
	}

}

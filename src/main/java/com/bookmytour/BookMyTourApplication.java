package com.bookmytour;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.bookmytour.repository")
@EntityScan(basePackages = "com.bookmytour.entity" )
public class BookMyTourApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookMyTourApplication.class, args);
	}

}

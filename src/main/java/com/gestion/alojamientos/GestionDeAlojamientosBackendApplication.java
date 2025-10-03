package com.gestion.alojamientos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GestionDeAlojamientosBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionDeAlojamientosBackendApplication.class, args);
	}

}
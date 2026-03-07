package it.uniroma1.ingestion;

import it.uniroma1.ingestion.normalization_helpers.*;
import it.uniroma1.ingestion.rest_normalization.*;
import it.uniroma1.ingestion.telemetry_normalization.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IngestionApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngestionApplication.class, args);
	}

}

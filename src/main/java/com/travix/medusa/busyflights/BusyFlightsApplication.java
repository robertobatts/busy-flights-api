package com.travix.medusa.busyflights;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
public class BusyFlightsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusyFlightsApplication.class, args);
	}
}

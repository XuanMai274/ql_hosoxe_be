package com.bidv.asset.vehicle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BidvVehicleProfileManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(BidvVehicleProfileManagementApplication.class, args);
	}
}

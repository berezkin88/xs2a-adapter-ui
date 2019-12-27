package de.adorsys.xs2a.adapter.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"de.adorsys.xs2a.adapter.remote.api"})
@SpringBootApplication
public class Xs2aAdapterUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(Xs2aAdapterUiApplication.class, args);
	}

}

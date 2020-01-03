package de.adorsys.xs2a.adapter.ui;

import de.adorsys.xs2a.adapter.ui.configuration.FeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableFeignClients(basePackages = {"de.adorsys.xs2a.adapter.remote.api"}, defaultConfiguration = FeignConfiguration.class)
@SpringBootApplication
public class Xs2aAdapterUiApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(Xs2aAdapterUiApplication.class, args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/thank-you").setViewName("thank-you");
	}
}

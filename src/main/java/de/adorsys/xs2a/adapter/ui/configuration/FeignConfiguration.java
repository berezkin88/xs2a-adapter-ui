package de.adorsys.xs2a.adapter.ui.configuration;

import feign.Contract;
import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class FeignConfiguration {

    // resolving issue with Feign LocalDate serialization https://github.com/spring-cloud/spring-cloud-openfeign/issues/104
    @Bean
    public Contract feignContract() {
        return new SpringMvcContract();
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Map<String, Collection<String>> queries = new HashMap<>(requestTemplate.queries());

            if (queries.containsKey("bookingStatus")) {
                queries.put("bookingStatus", queries.get("bookingStatus").stream().map(String::toLowerCase).collect(Collectors.toList()));
                requestTemplate.queries(null);
                requestTemplate.queries(queries);
            }
        };
    }
}

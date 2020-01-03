package de.adorsys.xs2a.adapter.ui.configuration;

import de.adorsys.xs2a.adapter.model.BookingStatusTO;
import feign.Contract;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Collections;

@Configuration
public class FeignConfiguration {

    @Bean
    public Contract feignContract() {
        return new SpringMvcContract(Collections.emptyList(), new CustomConversionService());
    }

    public static class CustomConversionService extends DefaultConversionService {

        public CustomConversionService() {

            addConverter(new Converter<BookingStatusTO, String>() {
                @Override
                public String convert(BookingStatusTO source) {
                    return source.toString();
                }
            });
        }
    }
}

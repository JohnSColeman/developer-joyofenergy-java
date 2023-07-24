package uk.tw.energy.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.vavr.control.Validation;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addCustomValidationSerializer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addSerializer(Validation.class, new ValidationSerializer());
            builder.modules(module);
        };
    }
}
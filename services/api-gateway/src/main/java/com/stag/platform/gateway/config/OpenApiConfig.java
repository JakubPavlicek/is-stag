package com.stag.platform.gateway.config;

import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/// Minimal necessary SpringDoc configuration, when springdoc.api-docs.enabled is set to false
/// @see <a href="https://springdoc.org/faq.html#_what_is_a_proper_way_to_set_up_swagger_ui_to_use_provided_spec_yml">SpringDoc FAQ</a>
@Configuration
public class OpenApiConfig {

    @Bean
    @Primary
    SpringDocConfiguration springDocConfiguration() {
        return new SpringDocConfiguration();
    }

    @Bean
    SpringDocConfigProperties springDocConfigProperties() {
        return new SpringDocConfigProperties();
    }

    @Bean
    ObjectMapperProvider objectMapperProvider(SpringDocConfigProperties springDocConfigProperties) {
        return new ObjectMapperProvider(springDocConfigProperties);
    }


}

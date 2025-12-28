package com.stag.platform.gateway.config;

import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/// **Minimal SpringDoc Configuration for Pre-built OpenAPI Specs**
///
/// Configures the minimal necessary SpringDoc beans when API documentation generation
/// is disabled (`springdoc.api-docs.enabled=false`). This setup is used when the gateway
/// serves pre-built OpenAPI YAML specifications instead of generating them at runtime.
///
/// @see <a href="https://springdoc.org/faq.html#_what_is_a_proper_way_to_set_up_swagger_ui_to_use_provided_spec_yml">SpringDoc FAQ</a>
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Configuration
public class OpenApiConfig {

    /// Creates the core SpringDoc configuration bean.
    ///
    /// This bean is marked as `@Primary` to override the default autoconfiguration
    /// when `springdoc.api-docs.enabled` is set to false.
    ///
    /// @return SpringDocConfiguration instance
    @Bean
    @Primary
    SpringDocConfiguration springDocConfiguration() {
        return new SpringDocConfiguration();
    }

    /// Creates the SpringDoc properties configuration bean.
    ///
    /// Holds configuration properties for SpringDoc components even when
    /// automatic API documentation generation is disabled.
    ///
    /// @return SpringDocConfigProperties instance
    @Bean
    SpringDocConfigProperties springDocConfigProperties() {
        return new SpringDocConfigProperties();
    }

    /// Creates the ObjectMapper provider for OpenAPI serialization.
    ///
    /// Swagger UI uses this provider to serialize and deserialize
    /// OpenAPI specification files (YAML/JSON).
    ///
    /// @param springDocConfigProperties SpringDoc configuration properties
    /// @return ObjectMapperProvider instance configured with SpringDoc properties
    @Bean
    ObjectMapperProvider objectMapperProvider(SpringDocConfigProperties springDocConfigProperties) {
        return new ObjectMapperProvider(springDocConfigProperties);
    }

}

package com.awad.tmdb.config;

import com.awad.tmdb.constant.AppPropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMVCConfig implements WebMvcConfigurer {
    private final AppPropertiesConfig properties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(properties.getCors().getPathPattern())
                .allowedOrigins(properties.getCors().getAllowedOrigins())
                .allowedMethods(properties.getCors().getAllowedMethods())
                .allowedHeaders(properties.getCors().getAllowedHeaders())
                .allowCredentials(properties.getCors().isAllowCredentials())
                .maxAge(properties.getCors().getMaxAge());
    }
}

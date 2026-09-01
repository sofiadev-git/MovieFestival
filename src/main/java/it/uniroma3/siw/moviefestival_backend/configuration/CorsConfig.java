package it.uniroma3.siw.moviefestival_backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/rest/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET"); // perché react gestisce solo la lettura, il resto è thymeleaf
    }
}

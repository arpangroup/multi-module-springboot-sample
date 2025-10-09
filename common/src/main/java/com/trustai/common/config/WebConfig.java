package com.trustai.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configurePathMatch(PathMatchConfigurer configurer) {
                configurer.setUseTrailingSlashMatch(true); // Optional but helpful
            }

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // only for API endpoints
                        //.allowedOrigins("*") // Change in production
//                        .allowedOriginPatterns("*") // // allow all origins
//                        .allowedOrigins("https://trustai.co.in")  // ✅ your frontend domain
                        .allowedOrigins("http://localhost:3000", "http://localhost:5173", "https://trustai.co.in", "https://admin.trustai.co.in")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry
                        .addResourceHandler("/css/**", "/js/**")
                        .addResourceLocations("classpath:/static/css/", "classpath:/static/js/");

            }

            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                // Respond with HTTP 204 NO_CONTENT for favicon requests (avoid error)
                registry.addViewController("/favicon.ico").setStatusCode(HttpStatus.NO_CONTENT);
            }
        };
    }
}

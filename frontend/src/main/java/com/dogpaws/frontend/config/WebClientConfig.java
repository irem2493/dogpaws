package com.dogpaws.frontend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@Slf4j
public class WebClientConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {

        String projectPath = System.getProperty("user.dir");

        String absolutePath = projectPath + File.separator + "frontend" + File.separator + "uploads" + File.separator;

        log.info("File access path configured: file:///{}", absolutePath);
        System.out.println(absolutePath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + absolutePath);
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080")
                .build();
    }
}
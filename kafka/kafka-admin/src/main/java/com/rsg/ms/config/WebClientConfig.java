package com.rsg.ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

//So we can use this webclient everytime instead of creating a new instance
@Configuration
public class WebClientConfig {


    @Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }
}

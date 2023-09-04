package com.hamoon.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Bean
    @LoadBalanced
    // implements client side load balancing for api call between inventory-service and order-service
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

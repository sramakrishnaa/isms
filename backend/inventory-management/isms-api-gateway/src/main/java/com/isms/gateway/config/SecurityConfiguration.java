package com.isms.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity security) {
        return security.csrf(ServerHttpSecurity.CsrfSpec::disable).cors(cors -> {
        }).authorizeExchange(exchange -> exchange.pathMatchers("/actuator/**").permitAll().pathMatchers(HttpMethod.OPTIONS, "/**").permitAll().anyExchange().authenticated()).oauth2ResourceServer(oauth2 -> oauth2.jwt()).build();
    }
}	
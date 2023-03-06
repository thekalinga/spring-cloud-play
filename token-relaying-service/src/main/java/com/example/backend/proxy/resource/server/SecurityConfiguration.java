package com.example.backend.proxy.resource.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

  @Bean
  SecurityWebFilterChain security(ServerHttpSecurity http) {
    return http
        .authorizeExchange()
          .anyExchange().authenticated()
        .and()
          .oauth2ResourceServer().jwt().and()
        .and()
          .csrf().disable()
          .formLogin().disable()
        .build();
  }

  @Bean
  WebClient proxyWebClient() {
    final var bearerExchangeFilterFunction = new ServerBearerExchangeFilterFunction();
    return WebClient.builder().filter(bearerExchangeFilterFunction).build();
  }
}

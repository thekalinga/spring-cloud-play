package com.example.hybrid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
  @Bean
  SecurityWebFilterChain security(ServerHttpSecurity http) throws Exception {
    return http.authorizeExchange()
          .anyExchange().authenticated()
        .and()
          .oauth2Client()
        .and()
          .oauth2ResourceServer().jwt().and()
        .and()
        .build();
  }

  @Bean
  ReactiveOAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> oAuth2AccessTokenResponseClient() {
    return new WebClientReactiveClientCredentialsTokenResponseClient();
  }
}

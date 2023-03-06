package com.example.standalone;

import io.netty.handler.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

  @Bean
  ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager(ReactiveClientRegistrationRepository clientRegistrationRepository, ReactiveOAuth2AuthorizedClientService authorisedClientService) {
    return new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository, authorisedClientService);
  }

  @Bean
  ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
    return new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
  }

  @Bean
  ClientHttpConnector clientHttpConnector() {
    // for enabling debugging
    final var httpClient = HttpClient.create().wiretap(HttpClient.class.getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
    return new ReactorClientHttpConnector(httpClient);
  }

  @Bean
  WebClient webClientWithAuthzClientFilter(ServerOAuth2AuthorizedClientExchangeFilterFunction oAuth2AuthorizedClientExchangeFilterFunction, ClientHttpConnector clientHttpConnector) {
    return WebClient.builder()
        .clientConnector(clientHttpConnector)
        .filter(oAuth2AuthorizedClientExchangeFilterFunction)
        .build();
  }

  @Bean
  WebClient webClient(ClientHttpConnector clientHttpConnector) {
    return WebClient.builder()
        .clientConnector(clientHttpConnector)
        .build();
  }

//  @Bean
//  ReactiveOAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> oAuth2AccessTokenResponseClient() {
//    return new WebClientReactiveClientCredentialsTokenResponseClient();
//  }
}

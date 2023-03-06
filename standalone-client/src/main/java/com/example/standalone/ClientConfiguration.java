package com.example.standalone;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.serverWebExchange;

@Log4j2
@Configuration(proxyBeanMethods = false)
public class ClientConfiguration {

  @Bean
  ApplicationRunner doOnStart(WebClient webClient, WebClient webClientWithAuthzClientFilter, ReactiveOAuth2AuthorizedClientManager reactiveOAuth2AuthorizedClientManager) {
    final var authzRequest = OAuth2AuthorizeRequest.withClientRegistrationId("standalone-client").principal("standalone-client").build();
    final var authorizedClient$ = reactiveOAuth2AuthorizedClientManager.authorize(authzRequest);

    return args -> {
      authorizedClient$
          .flatMap(authorizedClient -> {
            Mono<String> bySettingAuthzHeader = webClient.method(GET)
                .uri("http://resource-service.localtest.me:55555/standalone")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + requireNonNull(authorizedClient).getAccessToken().getTokenValue())
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(val -> log.debug("Received: {}", val));

            Mono<String> usingAttributes = webClientWithAuthzClientFilter.method(GET)
                .uri("http://resource-service.localtest.me:55555/standalone")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(val -> log.debug("Received: {}", val));

            return bySettingAuthzHeader.then(usingAttributes);
          })
          .block();
    };
  }

}

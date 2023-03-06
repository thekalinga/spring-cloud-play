package com.example.hybrid.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpMethod.GET;

@RestController
@RequestMapping("/")
public class HybridController {

  private final WebClient webClient;

  public HybridController(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  @GetMapping
  public Mono<String> hello(@RegisteredOAuth2AuthorizedClient("hybrid-resource-server-oauth-client-service") OAuth2AuthorizedClient authorizedClient) {
    return webClient.method(GET)
        .uri("http://resource-service.localtest.me:55555/hybrid")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + requireNonNull(authorizedClient).getAccessToken().getTokenValue())
        .retrieve()
        .bodyToMono(String.class)
        .map(val -> "[via hybrid resource server cum oauth client]: " + val);
  }

}

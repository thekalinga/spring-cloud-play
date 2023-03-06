package com.example.frontend.controller.resource;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.serverWebExchange;

@Log4j2
@RestController
@RequestMapping("/resource-service")
public class ResourceServerRequestProxyingController {
  private final WebClient tokenRelayingWebClient;

  public ResourceServerRequestProxyingController(WebClient.Builder tokenRelayingWebClientBuilder) {
    this.tokenRelayingWebClient = tokenRelayingWebClientBuilder.build();
  }

  @GetMapping
  public Mono<String> index(ServerWebExchange serverWebExchange, @RegisteredOAuth2AuthorizedClient("frontend-client") OAuth2AuthorizedClient authorizedClient) {
    return tokenRelayingWebClient.method(GET)
        .uri("http://resource-service")
        .attributes(serverWebExchange(serverWebExchange))
        .attributes(oauth2AuthorizedClient(authorizedClient))
        .retrieve()
        .bodyToMono(String.class)
        .map(this::addHomeLink);
  }

  @GetMapping("read")
  public Mono<String> read(ServerWebExchange serverWebExchange, @RegisteredOAuth2AuthorizedClient("frontend-client") OAuth2AuthorizedClient authorizedClient) {
    return tokenRelayingWebClient.method(GET)
        .uri("http://resource-service/read")
        .attributes(serverWebExchange(serverWebExchange))
        .attributes(oauth2AuthorizedClient(authorizedClient))
        .retrieve()
        .bodyToMono(String.class)
        .map(this::addHomeLink);
  }

  @GetMapping("write")
  public Mono<String> write(ServerWebExchange serverWebExchange, @RegisteredOAuth2AuthorizedClient("frontend-client") OAuth2AuthorizedClient authorizedClient) {
    return tokenRelayingWebClient.method(POST)
        .uri("http://resource-service/write")
        .body(Mono.just("Modified by frontend"), String.class)
        .attributes(serverWebExchange(serverWebExchange))
        .attributes(oauth2AuthorizedClient(authorizedClient))
        .retrieve()
        .bodyToMono(Void.class)
        .thenReturn("Write successful. Now <a href=\"read\">read</a> written value")
        .map(this::addHomeLink);
  }

  @GetMapping("inaccessible")
  public Mono<String> inaccessible(ServerWebExchange serverWebExchange, @RegisteredOAuth2AuthorizedClient("frontend-client") OAuth2AuthorizedClient authorizedClient) {
    return tokenRelayingWebClient.method(GET)
        .uri("http://resource-service/inaccessible")
        .attributes(serverWebExchange(serverWebExchange))
        .attributes(oauth2AuthorizedClient(authorizedClient))
        .retrieve()
        .bodyToMono(String.class)
        .map(this::addHomeLink);
  }

  @GetMapping("hybrid_client_endpoint_direct")
  public Mono<String> hybridRouteIllegalAccess(ServerWebExchange serverWebExchange, @RegisteredOAuth2AuthorizedClient("frontend-client") OAuth2AuthorizedClient authorizedClient) {
    return tokenRelayingWebClient.method(GET)
        .uri("http://resource-service/hybrid")
        .attributes(serverWebExchange(serverWebExchange))
        .attributes(oauth2AuthorizedClient(authorizedClient))
        .retrieve()
        .bodyToMono(String.class)
        .map(this::addHomeLink);
  }

  @GetMapping("hybrid_client_endpoint_via_hybrid_service")
  public Mono<String> viaHybridRoute(ServerWebExchange serverWebExchange, @RegisteredOAuth2AuthorizedClient("frontend-client") OAuth2AuthorizedClient authorizedClient) {
    return tokenRelayingWebClient.method(GET)
        .uri("http://hybrid-resource-server-oauth-client-service")
        .attributes(serverWebExchange(serverWebExchange))
        .attributes(oauth2AuthorizedClient(authorizedClient))
        .retrieve()
        .bodyToMono(String.class)
        .map(this::addHomeLink);
  }

  @GetMapping("token_relay")
  public Mono<String> viaTokenRelay(ServerWebExchange serverWebExchange, @RegisteredOAuth2AuthorizedClient("frontend-client") OAuth2AuthorizedClient authorizedClient) {
    return tokenRelayingWebClient.method(GET)
        .uri("http://token-relaying-service")
        .attributes(serverWebExchange(serverWebExchange))
        .attributes(oauth2AuthorizedClient(authorizedClient))
        .retrieve()
        .bodyToMono(String.class)
        .map(this::addHomeLink);
  }

  private String addHomeLink(String input) {
    return input + "<br><br><p>Go <a href=\"../\">home</a></p>";
  }
}

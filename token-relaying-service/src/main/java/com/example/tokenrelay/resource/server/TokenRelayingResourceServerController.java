package com.example.tokenrelay.resource.server;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;

@RestController
@RequestMapping("/")
public class TokenRelayingResourceServerController {

  private final WebClient proxyWebClient;

  public TokenRelayingResourceServerController(WebClient proxyWebClient) {
    this.proxyWebClient = proxyWebClient;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('SCOPE_resource.token_relay')")
  public Mono<String> read() {
    return proxyWebClient.method(GET)
        .uri("http://resource-service.localtest.me:55555/read")
        .retrieve()
        .bodyToMono(String.class)
        .map(val -> "[via proxied resource server]: " + val);
  }

}

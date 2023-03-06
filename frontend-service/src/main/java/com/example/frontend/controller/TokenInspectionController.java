package com.example.frontend.controller;

import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.example.frontend.Util.getAccessToken;
import static com.example.frontend.Util.getIdToken;
import static com.example.frontend.Util.getRefreshToken;

@RestController
@RequestMapping("/inspect")
public class TokenInspectionController {

  private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

  public TokenInspectionController(
      ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
    this.authorizedClientRepository = authorizedClientRepository;
  }

  @GetMapping("/access_token")
  public Mono<String> accessToken(ServerWebExchange webExchange) {
    return getAccessToken(authorizedClientRepository, webExchange).map(this::wrapInMarkup);
  }

  @GetMapping("/refresh_token")
  public Mono<String> refreshToken(ServerWebExchange webExchange) {
    return getRefreshToken(authorizedClientRepository, webExchange).map(this::wrapInMarkup);
  }

  @GetMapping("/id_token")
  public Mono<String> idToken() {
    return getIdToken().map(this::wrapInMarkup);
  }

  private String wrapInMarkup(String input) {
    return "<textarea readonly style=\"width: 800px; height: 200px;\">" + input + "</textarea><br><br><p>Go <a href=\"../\">home</a></p>";
  }

}

package com.example.frontend;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

public class Util {
  public static Mono<String> getIdToken() {
    return getAuthentication()
        .map(Authentication::getPrincipal)
        .cast(OidcUser.class)
        .map(user -> user.getIdToken().getTokenValue());
  }

  public static Mono<String> getAccessToken(
      ServerOAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository, ServerWebExchange webExchange) {
    return getOAuth2AuthorizedClient(oAuth2AuthorizedClientRepository, webExchange)
        .map(authorizedClient -> requireNonNull(authorizedClient.getAccessToken()).getTokenValue());
  }

  public static Mono<String> getRefreshToken(
      ServerOAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository, ServerWebExchange webExchange) {
    return getOAuth2AuthorizedClient(oAuth2AuthorizedClientRepository, webExchange)
      .map(authorizedClient -> requireNonNull(authorizedClient.getRefreshToken()).getTokenValue());
  }

  public static Mono<OAuth2AuthorizedClient> getOAuth2AuthorizedClient(
      ServerOAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
      ServerWebExchange webExchange) {
    return getAuthentication().flatMap(authentication -> {
      return oAuth2AuthorizedClientRepository.loadAuthorizedClient("frontend-client", authentication, webExchange);
    });
  }

  public static Mono<OAuth2AuthenticationToken> getAuthentication() {
    return ReactiveSecurityContextHolder.getContext() // Refer to `ReactorContextWebFilter`
        .map(securityContext -> (OAuth2AuthenticationToken) securityContext.getAuthentication());
  }
}

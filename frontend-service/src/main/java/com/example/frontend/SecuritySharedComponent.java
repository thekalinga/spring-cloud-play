package com.example.frontend;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.web.filter.reactive.ServerWebExchangeContextFilter.EXCHANGE_CONTEXT_ATTRIBUTE;

@Component
public class SecuritySharedComponent {

  private final ReactiveOAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final ServerSecurityContextRepository serverSecurityContextRepository;

  public SecuritySharedComponent(ReactiveOAuth2AuthorizedClientService oAuth2AuthorizedClientService,
      ServerSecurityContextRepository serverSecurityContextRepository) {
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    this.serverSecurityContextRepository = serverSecurityContextRepository;
  }

  public Mono<Void> removeAuthorisedClientRemoveAuthenticationAndTriggerAuthFlow(String clientRegistrationId, String principalName) {
    final var serverWebExchange$ = Mono.deferContextual(contextView -> {
      final ServerWebExchange exchange = contextView.getOrDefault(EXCHANGE_CONTEXT_ATTRIBUTE, null);
      if (exchange != null) {
        return Mono.just(exchange);
      } else {
        return Mono.error(new IllegalStateException("This handler can only be invoked in flows with a ServerWebExchange"));
      }
    });

    return oAuth2AuthorizedClientService.removeAuthorizedClient(clientRegistrationId, principalName) // TODO: more needs to be done in this area as we would want to allow users to specify if any given flow is idempotent (or) not, if yes, we would want to ideally trigger post auth-server redirect
        .thenEmpty(serverWebExchange$.flatMap(serverWebExchange -> serverSecurityContextRepository.save(serverWebExchange, null))
            .thenEmpty(Mono.error(new AccessDeniedException("Either existing access token is not valid (or) Could not retrieve a new access token from refresh token. Requires re-authorisation"))));
  }

}

package com.example.frontend.config;

import com.example.frontend.SecuritySharedComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.RemoveAuthorizedClientReactiveOAuth2AuthorizationFailureHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.Map;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.SECURITY_CONTEXT_SERVER_WEB_EXCHANGE;
import static org.springframework.security.oauth2.client.RemoveAuthorizedClientReactiveOAuth2AuthorizationFailureHandler.OAuth2AuthorizedClientRemover;
import static org.springframework.web.filter.reactive.ServerWebExchangeContextFilter.EXCHANGE_CONTEXT_ATTRIBUTE;

@Configuration(proxyBeanMethods = false)
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

  @Bean
  SecurityWebFilterChain security(ServerHttpSecurity http, ServerSecurityContextRepository serverSecurityContextRepository) {
    final var authenticationEntryPoint = new RedirectServerAuthenticationEntryPoint("/oauth2/authorization/frontend-client");
    final var accessDeniedHandler = new RedirectAwareAccessDeniedHandler(authenticationEntryPoint);

    final var serverWebExchangeContextFilter = new ServerWebExchangeContextFilter();

    return http
        .authorizeExchange()
          .pathMatchers("/assets/**").permitAll()
          .pathMatchers("/favicon.ico").permitAll()
          .anyExchange().authenticated()
        .and()
          .oauth2Login()
            .securityContextRepository(serverSecurityContextRepository)
        .and()
          .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        .and()
          .formLogin().disable()
          .httpBasic().disable()
        .addFilterAfter(serverWebExchangeContextFilter, SECURITY_CONTEXT_SERVER_WEB_EXCHANGE) // make security context available to `RedirectAwareAccessDeniedHandler`
        .build();
  }

  @Bean
  ServerOAuth2AuthorizedClientExchangeFilterFunction bearerTokenRelayExchangeFilterFunction(
      ReactiveClientRegistrationRepository clientRegistrationRepository,
      ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
      SecuritySharedComponent securitySharedComponent) {
    final var bearerTokenRelayExchangeFilterFunction = new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository);
    final var authorizedClientRemover = new SessionClearingRedirectionTriggeringOAuth2AuthorizedClientRemover(securitySharedComponent);
    final var authorizationFailureHandler = new RemoveAuthorizedClientReactiveOAuth2AuthorizationFailureHandler(authorizedClientRemover);
    bearerTokenRelayExchangeFilterFunction.setAuthorizationFailureHandler(authorizationFailureHandler);
    return bearerTokenRelayExchangeFilterFunction;
  }

  static class RedirectAwareAccessDeniedHandler implements ServerAccessDeniedHandler {
    private final ServerAuthenticationEntryPoint authenticationEntryPoint;

    public RedirectAwareAccessDeniedHandler(ServerAuthenticationEntryPoint authenticationEntryPoint) {
      this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
      return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap((response) -> {
        if (ex.getCause() instanceof ClientAuthorizationException cause) {
          return authenticationEntryPoint.commence(exchange, new OAuth2AuthenticationException(cause.getError()));
        } else {
          response.setStatusCode(HttpStatus.FORBIDDEN);
          response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
          DataBufferFactory dataBufferFactory = response.bufferFactory();
          DataBuffer buffer = dataBufferFactory.wrap(ex.getMessage().getBytes(Charset.defaultCharset()));
          return response.writeWith(Mono.just(buffer)).doOnError((error) -> DataBufferUtils.release(buffer));
        }
      });
    }
  }

  static class SessionClearingRedirectionTriggeringOAuth2AuthorizedClientRemover implements OAuth2AuthorizedClientRemover {
    private final SecuritySharedComponent securitySharedComponent;

    public SessionClearingRedirectionTriggeringOAuth2AuthorizedClientRemover(
        SecuritySharedComponent securitySharedComponent) {
      this.securitySharedComponent = securitySharedComponent;
    }

    @Override
    public Mono<Void> removeAuthorizedClient(String clientRegistrationId, Authentication principal, Map<String, Object> attributes) {
      return securitySharedComponent.removeAuthorisedClientRemoveAuthenticationAndTriggerAuthFlow(clientRegistrationId, principal.getName());
    }
  }

}

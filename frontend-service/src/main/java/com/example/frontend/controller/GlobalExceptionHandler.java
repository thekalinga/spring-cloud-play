package com.example.frontend.controller;

import com.example.frontend.SecuritySharedComponent;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

  private final SecuritySharedComponent securitySharedComponent;

  public GlobalExceptionHandler(SecuritySharedComponent securitySharedComponent) {
    this.securitySharedComponent = securitySharedComponent;
  }

  @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class})
  Mono<Void> authenticationExceptionRethrower(Exception e) {
    // we need to ensure, the catch all exception handler (handleException method below) dont stop AuthenticationException from propagating
    // rethow this exception so that ExceptionTranslationFilter which triggers authentication flow
    return Mono.error(e);
  }

  @ExceptionHandler
  Mono<ResponseEntity<String>> handleWebClientException(WebClientException e) {
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("ERROR:<br><br>" + e.getMessage()));
  }

  @ExceptionHandler(ClientAuthorizationException.class)
  Mono<Void> handleClientAuthorizationException(Authentication principal) {
    return securitySharedComponent.removeAuthorisedClientRemoveAuthenticationAndTriggerAuthFlow("frontend-client", principal.getName());
  }

  @ExceptionHandler
  Mono<ResponseEntity<String>> handleAllUnhandledExceptions(Exception e) {
    log.error("Error occurred", e);
    return Mono.just(ResponseEntity.internalServerError().body("ERROR:<br><br>" + e.getMessage()));
  }
}

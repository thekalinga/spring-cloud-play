package com.example.frontend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler
  ResponseEntity<String> authenticationExceptionRethrower(AuthenticationException e) {
    // we need to ensure, the catch all exception handler (handleException method below) dont stop AuthenticationException from propagating
    // rethow this exception so that ExceptionTranslationFilter which triggers authentication flow
    throw e;
  }

  @ExceptionHandler
  ResponseEntity<String> handleAllUnhandledExceptions(Exception e) {
    log.error("Error occurred", e);
    return ResponseEntity.internalServerError().body("ERROR:<br><br>" + e.getMessage());
  }
}

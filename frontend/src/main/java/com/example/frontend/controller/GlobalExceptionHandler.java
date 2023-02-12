package com.example.frontend.controller;

import jakarta.security.auth.message.AuthException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler
  ResponseEntity<String> handleException(AuthenticationException e) {
    // we need to ensure catch all exception handler dont stop AuthenticationException doesnt propagate
    throw e;
  }

  @ExceptionHandler
  ResponseEntity<String> handleException(Exception e) {
    log.error("Error occurred", e);
    return ResponseEntity.internalServerError().body("ERROR:<br><br>" + e.getMessage());
  }
}

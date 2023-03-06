package com.example.frontend.controller.resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface ResourceServerClient {
  @PostExchange("/write")
  void write(@RequestBody String message);

  @GetExchange("/client_credentials_only")
  String clientCredentialsEndpoint();
}

package com.example.backend.client.credentials.controller;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpMethod.GET;

@RestController
@RequestMapping("/")
public class BackendClientCredentialsController {

  private final RestTemplate restTemplate;

  public BackendClientCredentialsController(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  @GetMapping
  public String hello(@RegisteredOAuth2AuthorizedClient("backend-client-credentials-client") OAuth2AuthorizedClient authorizedClient) {
    var headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + requireNonNull(authorizedClient).getAccessToken().getTokenValue());
    HttpEntity<String> requestEntity = new HttpEntity<>(headers);
    final var responseEntity =
        restTemplate.exchange("http://backend-resource-server.localtest.me:8081/client_credentials_only", GET,
            requestEntity, String.class);
    return responseEntity.getBody();
  }

}

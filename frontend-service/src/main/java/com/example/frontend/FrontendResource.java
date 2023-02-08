package com.example.frontend;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static com.example.frontend.Util.getAccessToken;
import static com.example.frontend.Util.getIdToken;
import static com.example.frontend.Util.getRefreshToken;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/")
public class FrontendResource {

  private final OAuth2AuthorizedClientRepository authorizedClientRepository;

  public FrontendResource(OAuth2AuthorizedClientRepository authorizedClientRepository) {
    this.authorizedClientRepository = authorizedClientRepository;
  }

  @GetMapping
  public String hello() {
    return "hello from frontend";
  }

  @GetMapping("/id_token")
  public String idToken() {
    return getIdToken();
  }

  @GetMapping("/access_token")
  public String accessToken(HttpServletRequest request) {
    return getAccessToken(authorizedClientRepository, request);
  }

  @GetMapping("/refresh_token")
  public String refreshToken(HttpServletRequest request) {
    return getRefreshToken(authorizedClientRepository, request);
  }

}

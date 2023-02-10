package com.example.frontend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.frontend.Util.getAccessToken;
import static com.example.frontend.Util.getIdToken;
import static com.example.frontend.Util.getRefreshToken;

@RestController
@RequestMapping("/inspect")
public class TokenInspectionController {

  private final OAuth2AuthorizedClientRepository authorizedClientRepository;

  public TokenInspectionController(OAuth2AuthorizedClientRepository authorizedClientRepository) {
    this.authorizedClientRepository = authorizedClientRepository;
  }

  @GetMapping("/access_token")
  public String accessToken(HttpServletRequest request) {
    return getAccessToken(authorizedClientRepository, request);
  }

  @GetMapping("/refresh_token")
  public String refreshToken(HttpServletRequest request) {
    return getRefreshToken(authorizedClientRepository, request);
  }

  @GetMapping("/id_token")
  public String idToken() {
    return getIdToken();
  }

}

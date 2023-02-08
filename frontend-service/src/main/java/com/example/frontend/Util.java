package com.example.frontend;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Util {
  static String getIdToken() {
    return ((OidcUser) (getAuthentication()).getPrincipal()).getIdToken().getTokenValue();
  }

  static String getAccessToken(OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository, HttpServletRequest request) {
    final OAuth2AuthorizedClient auth2AuthorizedClient = getoAuth2AuthorizedClient(oAuth2AuthorizedClientRepository, request);
    return auth2AuthorizedClient.getAccessToken().getTokenValue();
  }

  static String getRefreshToken(OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository, HttpServletRequest request) {
    final OAuth2AuthorizedClient auth2AuthorizedClient = getoAuth2AuthorizedClient(oAuth2AuthorizedClientRepository, request);
    return requireNonNull(auth2AuthorizedClient.getRefreshToken()).getTokenValue();
  }

  private static OAuth2AuthorizedClient getoAuth2AuthorizedClient(
      OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
      HttpServletRequest request) {
    return oAuth2AuthorizedClientRepository.loadAuthorizedClient("frontend-client", getAuthentication(), request);
  }

  static OAuth2AuthenticationToken getAuthentication() {
    return (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
  }
}

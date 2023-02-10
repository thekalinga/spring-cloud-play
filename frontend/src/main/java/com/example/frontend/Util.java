package com.example.frontend;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import static java.util.Objects.requireNonNull;

public class Util {
  public static String getIdToken() {
    return ((OidcUser) (getAuthentication()).getPrincipal()).getIdToken().getTokenValue();
  }

  public static String getAccessToken(
      OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository, HttpServletRequest request) {
    final OAuth2AuthorizedClient auth2AuthorizedClient = getOAuth2AuthorizedClient(oAuth2AuthorizedClientRepository, request);
    return auth2AuthorizedClient.getAccessToken().getTokenValue();
  }

  public static String getRefreshToken(
      OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository, HttpServletRequest request) {
    final OAuth2AuthorizedClient auth2AuthorizedClient = getOAuth2AuthorizedClient(oAuth2AuthorizedClientRepository, request);
    return requireNonNull(auth2AuthorizedClient.getRefreshToken()).getTokenValue();
  }

  public static OAuth2AuthorizedClient getOAuth2AuthorizedClient(
      OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository,
      HttpServletRequest request) {
    return oAuth2AuthorizedClientRepository.loadAuthorizedClient("frontend-client", getAuthentication(), request);
  }

  public static OAuth2AuthenticationToken getAuthentication() {
    return (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
  }
}

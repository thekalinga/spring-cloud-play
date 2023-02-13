package com.example.frontend.config;

import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Clock;
import java.time.Instant;

import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_TOKEN;

@Component
public class OAuthTokenRefresher {

  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager;

  public OAuthTokenRefresher(OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
      ClientRegistrationRepository clientRegistrationRepository) {
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    final var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService);
    var accessTokenResponseClient = new DefaultClientCredentialsTokenResponseClient();
    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder
        .builder()
        .refreshToken()
        .clientCredentials(configurer -> configurer.accessTokenResponseClient(accessTokenResponseClient))
        .build();
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
    this.authorizedClientManager = authorizedClientManager;
  }

  public OAuth2AuthorizedClient retrieveToken(OAuth2AuthorizeRequest authorizeRequest) {
    final var currentOAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(authorizeRequest.getClientRegistrationId(), authorizeRequest.getPrincipal().getName());

    final var accessToken = currentOAuth2AuthorizedClient.getAccessToken();

    // if access token is already expired
    if (accessToken.getExpiresAt() != null && accessToken.getExpiresAt().isAfter(Instant.now(Clock.systemUTC()))) {
      return currentOAuth2AuthorizedClient;
    }

    final var refreshToken = currentOAuth2AuthorizedClient.getRefreshToken();
    if (refreshToken != null && refreshToken.getExpiresAt() != null && refreshToken.getExpiresAt().isBefore(Instant.now(Clock.systemUTC()))) {
      clearContextAndLaunchAuthFlow(authorizeRequest);
    }

    OAuth2AuthorizedClient updatedOAuth2AuthorizedClient = null;
    try {
      updatedOAuth2AuthorizedClient = authorizedClientManager.authorize(authorizeRequest);
    } catch (OAuth2AuthorizationException e) {
      clearContextAndLaunchAuthFlow(authorizeRequest);
    }
    oAuth2AuthorizedClientService.saveAuthorizedClient(updatedOAuth2AuthorizedClient, authorizeRequest.getPrincipal());
    return updatedOAuth2AuthorizedClient;
  }

  private void clearContextAndLaunchAuthFlow(OAuth2AuthorizeRequest authorizeRequest) {
    oAuth2AuthorizedClientService.removeAuthorizedClient(authorizeRequest.getClientRegistrationId(), authorizeRequest.getPrincipal().getName());

    var servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    servletRequestAttributes.getRequest().getSession().invalidate();

    throw new OAuth2AuthenticationException(new OAuth2Error(INVALID_TOKEN), "Session expired, please login");
  }

}

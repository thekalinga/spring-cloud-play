package com.example.frontend.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.example.frontend.Util.getAuthentication;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class AccessTokenRelayingInterceptor implements ClientHttpRequestInterceptor {
  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final OAuthTokenRefresher oauthTokenRefresher;

  public AccessTokenRelayingInterceptor(OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
      OAuthTokenRefresher oauthTokenRefresher) {
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    this.oauthTokenRefresher = oauthTokenRefresher;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    final var authorisedClient = getAuthorisedClient();
    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(authorisedClient.getClientRegistration().getRegistrationId())
        .principal(getAuthentication())
        .build();

    final var authorizedClient = oauthTokenRefresher.retrieveToken(authorizeRequest);
    final var accessTokenValue = authorizedClient.getAccessToken().getTokenValue();
    request.getHeaders().add(AUTHORIZATION, "Bearer " + accessTokenValue);

    return execution.execute(request, body);
  }

  private OAuth2AuthorizedClient getAuthorisedClient() {
    final var oAuth2AuthenticationToken = getAuthentication();
    final var clientId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
//    final var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return oAuth2AuthorizedClientService.loadAuthorizedClient(clientId, oAuth2AuthenticationToken.getName());
  }
}

package com.example.frontend.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

import static com.example.frontend.Util.getAuthentication;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class AccessTokenRelayingInterceptor implements ClientHttpRequestInterceptor {
  private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

  public AccessTokenRelayingInterceptor(OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
    this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
      ClientHttpRequestExecution execution) throws IOException {
    final var accessToken = getAuthorisedClient().getAccessToken().getTokenValue();
    request.getHeaders().add(AUTHORIZATION, "Bearer " + accessToken);
    return execution.execute(request, body);
  }

  private OAuth2AuthorizedClient getAuthorisedClient() {
    final var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    final var oAuth2AuthenticationToken = getAuthentication();
    final var clientId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
    return oAuth2AuthorizedClientRepository.loadAuthorizedClient(clientId, oAuth2AuthenticationToken, requireNonNull(requestAttributes).getRequest());
  }
}

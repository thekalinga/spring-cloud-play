package com.example.authorization.server;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static org.springframework.security.oauth2.server.authorization.OAuth2TokenType.ACCESS_TOKEN;

@Log4j2
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

  @Component
  static class UserInfoResponseCustomizer implements Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {
    private final UserDetailsService userDetailsService;

    public UserInfoResponseCustomizer(UserDetailsService userDetailsService) {
      this.userDetailsService = userDetailsService;
    }

    @Override
    public OidcUserInfo apply(OidcUserInfoAuthenticationContext oidcUserInfoAuthenticationContext) {
      final var authToken = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication());
      final var resourceOwnerUsername = authToken.getName();
      final var builder = OidcUserInfo.builder().subject(resourceOwnerUsername);
      if (authToken.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("SCOPE_email"))) {
        final var userWithEmail = userDetailsService.loadUserByUsername(resourceOwnerUsername);
//        builder.email(userWithEmail.getEmail());
        builder.email("whatever@gmail.com");
      }
      return builder.build();
    }
  }

  @Bean
  @Order(1)
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, UserInfoResponseCustomizer userInfoResponseCustomizer) throws Exception {
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
        .oidc(customizer -> customizer.userInfoEndpoint(oidcUserInfoEndpointConfigurer -> oidcUserInfoEndpointConfigurer.userInfoMapper(userInfoResponseCustomizer)));	// Enable OpenID Connect 1.0

    http
        // Redirect to the login page when not authenticated from the
        // authorization endpoint
        .exceptionHandling((exceptions) -> exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
        // Accept access tokens for User Info and/or Client Registration
        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

    // enable debugging
    // copied from OAuth2TokenEndpointConfigurer#createDefaultAuthenticationConverters
    final var accessTokenAuthenticationRequestConverters =
        List.of(new DebuggingOAuth2AuthorizationCodeAuthenticationConverter(),
            new DebuggingOAuth2RefreshTokenAuthenticationConverter(),
            new DebuggingOAuth2ClientCredentialsAuthenticationConverter());
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
        .tokenEndpoint(oAuth2TokenEndpointConfigurer -> oAuth2TokenEndpointConfigurer
            .accessTokenRequestConverters(consumer -> consumer.addAll(0, accessTokenAuthenticationRequestConverters)) // add at the beginning
            .accessTokenResponseHandler(new DebuggingAuthenticationSuccessHandler())
            .errorResponseHandler(new DebuggingAuthenticationFailureHandler())
        );

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
      throws Exception {
    http
        .authorizeHttpRequests((authorize) -> authorize
            .anyRequest().authenticated()
        )
        // Form login handles the redirect to the login page from the
        // authorization server filter chain
        .formLogin(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails userDetails = User.builder()
        .username("u")
        .password("{noop}p")
        .roles("USER")
        .build();

    UserDetails user2Details = User.builder()
        .username("u2")
        .password("{noop}p")
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(userDetails, user2Details);
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient standaloneClient = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("standalone-client")
        .clientSecret("{noop}standalone-client-secret")
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope("resource.standalone_client")
        .build();

    RegisteredClient frontendClient = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("frontend-client")
        .clientSecret("{noop}frontend-client-secret")
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .redirectUri("http://frontend.localtest.me:8080/login/oauth2/code/frontend-client")
        .redirectUri("http://frontend.localtest.me:8080/authorized")
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope(OidcScopes.EMAIL)
        .scope("resource.read")
        .scope("resource.write")
        .scope("resource.token_relay") // for token relaying resource server
        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
        .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(2)).refreshTokenTimeToLive(Duration.ofMinutes(30)).reuseRefreshTokens(false).build())
        .build();

    RegisteredClient hybridResourceServerCumOAuthClientClient = RegisteredClient.withId(UUID.randomUUID().toString())
        .clientId("hybrid-resource-server-oauth-client-service")
        .clientSecret("{noop}hybrid-resource-server-oauth-client-service-secret")
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .scope(OidcScopes.OPENID)
        .scope(OidcScopes.PROFILE)
        .scope("resource.hybrid")
//        .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofSeconds(30)).refreshTokenTimeToLive(Duration.ofMinutes(2)).build())
        .build();

    return new InMemoryRegisteredClientRepository(standaloneClient, frontendClient, hybridResourceServerCumOAuthClientClient);
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    KeyPair keyPair = generateRsaKey();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    RSAKey rsaKey = new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(UUID.randomUUID().toString())
        .build();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return new ImmutableJWKSet<>(jwkSet);
  }

  private static KeyPair generateRsaKey() {
    KeyPair keyPair;
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      keyPair = keyPairGenerator.generateKeyPair();
    }
    catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    return keyPair;
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().build();
  }

  @Log4j2
  static class DebuggingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter =
        new OAuth2AccessTokenResponseHttpMessageConverter();
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
      // copied from OAuth2TokenEndpointFilter#sendAccessTokenResponse
      OAuth2AccessTokenAuthenticationToken accessTokenAuthentication =
          (OAuth2AccessTokenAuthenticationToken) authentication;

      OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
      OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
      Map<String, Object> additionalParameters = accessTokenAuthentication.getAdditionalParameters();

      OAuth2AccessTokenResponse.Builder builder =
          OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
              .tokenType(accessToken.getTokenType())
              .scopes(accessToken.getScopes());
      if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
        builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
      }
      if (refreshToken != null) {
        builder.refreshToken(refreshToken.getTokenValue());
      }
      if (!CollectionUtils.isEmpty(additionalParameters)) {
        builder.additionalParameters(additionalParameters);
      }
      OAuth2AccessTokenResponse accessTokenResponse = builder.build();
      ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
      this.accessTokenHttpResponseConverter.write(accessTokenResponse, null, httpResponse);

      log.debug("Sending access token response {}", toString(accessTokenResponse));
    }

    private String toString(OAuth2AccessTokenResponse accessTokenResponse) {
      final var toStringBuilder = new StringBuilder();

      toStringBuilder.append("\n\n");

      final var accessToken = accessTokenResponse.getAccessToken();

      toStringBuilder.append("\nAccess token:\n");
      toStringBuilder
          .append("\ntokenType\n").append(accessToken.getTokenType())
          .append("\ntokenValue\n").append(accessToken.getTokenValue())
          .append("\nscopes\n").append(accessToken.getScopes())
          .append("\nissuedAt\n").append(accessToken.getIssuedAt())
          .append("\nexpiresAt\n").append(accessToken.getExpiresAt());

      if (accessTokenResponse.getRefreshToken() != null) {
        final var refreshToken = accessTokenResponse.getRefreshToken();

        toStringBuilder.append("\n\nRefresh token:\n");
        toStringBuilder
            .append("\ngetTokenValue\n").append(refreshToken.getTokenValue())
            .append("\ngetIssuedAt\n").append(refreshToken.getIssuedAt())
            .append("\ngetExpiresAt\n").append(refreshToken.getExpiresAt());
      }

      if (accessTokenResponse.getAdditionalParameters() != null) {
        toStringBuilder.append("\n\nAdditional Parameters:\n");

        toStringBuilder.append(accessTokenResponse.getAdditionalParameters());
      }
      toStringBuilder.append("\n\n");
      return toStringBuilder.toString();
    }
  }

  @Log4j2
  static class DebuggingAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final HttpMessageConverter<OAuth2Error> errorHttpResponseConverter =
        new OAuth2ErrorHttpMessageConverter();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {
      // copied from OAuth2TokenEndpointFilter#sendErrorResponse

      OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
      ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
      httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
      this.errorHttpResponseConverter.write(error, null, httpResponse);

      log.error("Sending authentication failure error {}", error, exception);
    }
  }

  @Log4j2
  static class DebuggingOAuth2AuthorizationCodeAuthenticationConverter implements AuthenticationConverter {
    private final OAuth2AuthorizationCodeAuthenticationConverter delegate = new OAuth2AuthorizationCodeAuthenticationConverter();
    @Override
    public Authentication convert(HttpServletRequest request) {
      final var converted = delegate.convert(request);
      if (converted != null) {
        log.debug("Received access token request for authorization code flow: {}", toString(
            (OAuth2AuthorizationCodeAuthenticationToken) converted));
      }
      return converted;
    }

    private String toString(OAuth2AuthorizationCodeAuthenticationToken authenticationToken) {
      final var toStringBuilder = new StringBuilder();

      toStringBuilder.append("\n\n");

      toStringBuilder.append("\nReceived authorisation code:\n");
      toStringBuilder.append(authenticationToken.getCode());

      toStringBuilder.append("\nOAuth Client name:\n");
      toStringBuilder.append(authenticationToken.getName());

      toStringBuilder.append("\n\n");
      return toStringBuilder.toString();
    }
  }

  @Log4j2
  static class DebuggingOAuth2RefreshTokenAuthenticationConverter implements AuthenticationConverter {
    private final OAuth2RefreshTokenAuthenticationConverter delegate = new OAuth2RefreshTokenAuthenticationConverter();
    @Override
    public Authentication convert(HttpServletRequest request) {
      final var converted = delegate.convert(request);
      if (converted != null) {
        log.debug("Received access token request for refresh token flow: {}", toString(
            (OAuth2RefreshTokenAuthenticationToken) converted));
      }
      return converted;
    }

    private String toString(OAuth2RefreshTokenAuthenticationToken authenticationToken) {
      final var toStringBuilder = new StringBuilder();

      toStringBuilder.append("\n\n");

      toStringBuilder.append("\nReceived refresh token:\n");
      toStringBuilder.append(authenticationToken.getRefreshToken());

      toStringBuilder.append("\nRequested scopes:\n");
      toStringBuilder.append(authenticationToken.getScopes());

      toStringBuilder.append("\nOAuth Client name:\n");
      toStringBuilder.append(authenticationToken.getName());

      toStringBuilder.append("\n\n");
      return toStringBuilder.toString();
    }
  }

  @Log4j2
  static class DebuggingOAuth2ClientCredentialsAuthenticationConverter implements AuthenticationConverter {
    private final OAuth2ClientCredentialsAuthenticationConverter delegate = new OAuth2ClientCredentialsAuthenticationConverter();
    @Override
    public Authentication convert(HttpServletRequest request) {
      final var converted = delegate.convert(request);
      if (converted != null) {
        log.debug("Received access token request for client credentials flow: {}", converted);
      }
      return converted;
    }

    private String toString(OAuth2ClientAuthenticationToken authenticationToken) {
      final var toStringBuilder = new StringBuilder();

      toStringBuilder.append("\n\n");

      toStringBuilder.append("\nClient credentials request:\n");
      toStringBuilder.append(authenticationToken.getName());

      toStringBuilder.append("\nClient credentials:\n");
      toStringBuilder.append(authenticationToken.getCredentials());

      toStringBuilder.append("\n\n");
      return toStringBuilder.toString();
    }
  }

}

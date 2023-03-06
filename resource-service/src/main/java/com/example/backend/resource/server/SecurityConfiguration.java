package com.example.backend.resource.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

  @Bean
  SecurityWebFilterChain security(ServerHttpSecurity http) {
    return http
        .authorizeExchange()
          .anyExchange().authenticated()
        .and()
          .oauth2ResourceServer().jwt().and()
        .and()
          .csrf().disable()
          .formLogin().disable()
        .build();
  }

  @Bean
  ReactiveJwtAuthenticationConverter customJwtAuthenticationConverter() {
    final var authenticationConverter = new ReactiveJwtAuthenticationConverter();
    authenticationConverter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
    return authenticationConverter;
  }

  static class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Flux<GrantedAuthority>> {
    private final ReactiveJwtGrantedAuthoritiesConverterAdapter delegate = new ReactiveJwtGrantedAuthoritiesConverterAdapter(new JwtGrantedAuthoritiesConverter());

    @Override
    public Flux<GrantedAuthority> convert(Jwt source) {
      final var grantedAuthorities = delegate.convert(source);
      if (grantedAuthorities == null) {
        return null;
      }
      return grantedAuthorities.map(grantedAuthority -> {
        final var replacedGrantName = grantedAuthority.getAuthority().replaceAll("\\.", "_").toUpperCase();
        return new SimpleGrantedAuthority(replacedGrantName);
      });
    }
  }
}

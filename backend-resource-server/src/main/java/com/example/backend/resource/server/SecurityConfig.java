package com.example.backend.resource.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
    return security
        .oauth2ResourceServer().jwt().and().and()
        .formLogin().disable()
        .build();
  }

  // TODO: Replace JwtGrantedAuthoritiesConverter of resource server with one where scopes in authentication are named RESOURCE_READ instead of SCOPE_resource.read
//  @Bean
//  Converter<Jwt, Collection<GrantedAuthority>> scopesToGrantedAuthoritiesConverter() {
//    return jwt -> {
//      return jwt.getClaim()
//    };
//  }
}

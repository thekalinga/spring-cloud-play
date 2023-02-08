package com.example.frontend;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.requireNonNull;

@Configuration(proxyBeanMethods = false)
public class MiscConfiguration {
  @Bean
  RestTemplate tokenRelayingResttemplate(RestTemplateBuilder builder, AccessTokenRelayingInterceptor accessTokenRelayingInterceptor) {
    return builder.additionalInterceptors(accessTokenRelayingInterceptor).build();
  }
}

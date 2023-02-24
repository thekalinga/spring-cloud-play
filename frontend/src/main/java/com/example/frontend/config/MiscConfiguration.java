package com.example.frontend.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.requireNonNull;

@Configuration(proxyBeanMethods = false)
public class MiscConfiguration {
  @Bean
  RestTemplate tokenRelayingRestTemplate(RestTemplateBuilder builder,
      AccessTokenRelayingInterceptor accessTokenRelayingInterceptor,
      LoadBalancerInterceptor loadBalancerInterceptor) {
    return builder.additionalInterceptors(loadBalancerInterceptor, accessTokenRelayingInterceptor).build();
  }
}

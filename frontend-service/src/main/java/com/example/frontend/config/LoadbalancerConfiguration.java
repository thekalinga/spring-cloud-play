package com.example.frontend.config;

import io.netty.handler.logging.LogLevel;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.util.List;

import static com.example.frontend.config.LoadbalancerConfiguration.HYBRID_SERVICE_ID;
import static com.example.frontend.config.LoadbalancerConfiguration.TOKEN_RELAYING_SERVICE_ID;
import static com.example.frontend.config.LoadbalancerConfiguration.RESOURCE_SERVICE_ID;

@Configuration(proxyBeanMethods = false)
@LoadBalancerClients({
    @LoadBalancerClient(name = RESOURCE_SERVICE_ID, configuration = LoadbalancerConfiguration.ResourceServerLoadBalancerClientConfiguration.class),
    @LoadBalancerClient(name = HYBRID_SERVICE_ID, configuration = LoadbalancerConfiguration.ClientCredentialsServerLoadBalancerClientConfiguration.class),
    @LoadBalancerClient(name = TOKEN_RELAYING_SERVICE_ID, configuration = LoadbalancerConfiguration.ProxyingResourceServerLoadBalancerClientConfiguration.class)
})
public class LoadbalancerConfiguration {
  public static final String RESOURCE_SERVICE_ID = "resource-service";
  public static final String HYBRID_SERVICE_ID = "hybrid-resource-server-oauth-client-service";
  public static final String TOKEN_RELAYING_SERVICE_ID = "token-relaying-service";

  @Bean
  @LoadBalanced
  WebClient.Builder tokenRelayingWebClient(ServerOAuth2AuthorizedClientExchangeFilterFunction bearerTokenRelayExchangeFilterFunction) {
    final var httpClient = HttpClient.create()
        .wiretap(HttpClient.class.getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
    final var clientConnector = new ReactorClientHttpConnector(httpClient);

    return WebClient.builder()
        .clientConnector(clientConnector)
        .filter(bearerTokenRelayExchangeFilterFunction);
  }

  @Configuration(proxyBeanMethods = false)
  static class ResourceServerLoadBalancerClientConfiguration {
    @Bean
    ServiceInstanceListSupplier resourceServerInstanceSupplier() {
      return new ServiceInstanceListSupplier() {
        @Override
        public String getServiceId() {
          return RESOURCE_SERVICE_ID;
        }

        @Override
        public Flux<List<ServiceInstance>> get() {
          final var instance1 = new DefaultServiceInstance("instance1", RESOURCE_SERVICE_ID, "resource-service.localtest.me", 55555, false);
//          final var instance2 = new DefaultServiceInstance("instance2", BACKEND_RESOURCE_SERVICE_ID, "resource-service.localtest.me", 55556, false);
//          List<ServiceInstance> instances = List.of(instance1, instance2);
          List<ServiceInstance> instances = List.of(instance1);
          return Flux.just(instances);
        }
      };
    }
  }

  @Configuration(proxyBeanMethods = false)
  static class ClientCredentialsServerLoadBalancerClientConfiguration {
    @Bean
    ServiceInstanceListSupplier clientCredentialsServerInstanceSupplier() {
      return new ServiceInstanceListSupplier() {
        @Override
        public String getServiceId() {
          return HYBRID_SERVICE_ID;
        }

        @Override
        public Flux<List<ServiceInstance>> get() {
          final var instance1 = new DefaultServiceInstance("instance1", RESOURCE_SERVICE_ID, "client-credentials.localtest.me", 44444, false);
          List<ServiceInstance> instances = List.of(instance1);
          return Flux.just(instances);
        }
      };
    }
  }

  @Configuration(proxyBeanMethods = false)
  static class ProxyingResourceServerLoadBalancerClientConfiguration {
    @Bean
    ServiceInstanceListSupplier proxyingResourceServerInstanceSupplier() {
      return new ServiceInstanceListSupplier() {
        @Override
        public String getServiceId() {
          return TOKEN_RELAYING_SERVICE_ID;
        }

        @Override
        public Flux<List<ServiceInstance>> get() {
          final var instance1 = new DefaultServiceInstance("instance1", TOKEN_RELAYING_SERVICE_ID, "proxying-resource-server.localtest.me", 33333, false);
          List<ServiceInstance> instances = List.of(instance1);
          return Flux.just(instances);
        }
      };
    }
  }

}


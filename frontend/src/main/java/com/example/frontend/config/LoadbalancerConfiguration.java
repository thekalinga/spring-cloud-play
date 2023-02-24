package com.example.frontend.config;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class LoadbalancerConfiguration {

  public static final String BACKEND_RESOURCE_SERVICE_ID = "backend-resource-server";

  @Bean
  ServiceInstanceListSupplier resourceServerInstanceSupplier() {
    return new ServiceInstanceListSupplier() {
      @Override
      public String getServiceId() {
        return BACKEND_RESOURCE_SERVICE_ID;
      }

      @Override
      public Flux<List<ServiceInstance>> get() {
        final var instance1 = new DefaultServiceInstance("instance1", BACKEND_RESOURCE_SERVICE_ID,
            "backend-resource-server.localtest.me", 55555, false);
        final var instance2 = new DefaultServiceInstance("instance2", BACKEND_RESOURCE_SERVICE_ID,
            "backend-resource-server.localtest.me", 55556, false);
        List<ServiceInstance> instances = List.of(instance1, instance2);
        return Flux.just(instances);
      }
    };
  }
}

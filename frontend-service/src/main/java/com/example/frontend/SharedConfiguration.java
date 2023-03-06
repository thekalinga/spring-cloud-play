package com.example.frontend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;

@Configuration
public class SharedConfiguration {

  @Bean
  ServerSecurityContextRepository serverSecurityContextRepository() {
    return new WebSessionServerSecurityContextRepository();
  }

}

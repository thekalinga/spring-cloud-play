package com.example.backend.client.credentials;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = false)
public class BackendClientCredentialsApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(BackendClientCredentialsApplication.class)
        .bannerMode(Banner.Mode.OFF)
        .properties("spring.output.ansi.enabled=always")
        .run(args);
  }

}

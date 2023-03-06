package com.example.authorization.server;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class AuthorizationServerApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(AuthorizationServerApplication.class)
        .bannerMode(Banner.Mode.OFF)
        .properties("spring.output.ansi.enabled=always")
        .run(args);
  }

}

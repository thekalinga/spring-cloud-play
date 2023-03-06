package com.example.frontend;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;


@SpringBootApplication
@EnableReactiveMethodSecurity
public class FrontendApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(FrontendApplication.class)
        .bannerMode(Banner.Mode.OFF)
        .properties("spring.output.ansi.enabled=always")
        .run(args);
  }

}

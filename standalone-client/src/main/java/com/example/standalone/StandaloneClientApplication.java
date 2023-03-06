package com.example.standalone;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
public class StandaloneClientApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(StandaloneClientApplication.class)
        .bannerMode(Banner.Mode.OFF)
        .web(WebApplicationType.NONE)
        .properties("spring.output.ansi.enabled=always")
        .run(args);
  }

}

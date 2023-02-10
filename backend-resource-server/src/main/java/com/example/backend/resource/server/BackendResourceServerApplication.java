package com.example.backend.resource.server;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = false)
public class BackendResourceServerApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(BackendResourceServerApplication.class)
        .bannerMode(Banner.Mode.OFF)
        .properties("spring.output.ansi.enabled=always")
        .run(args);
  }

}

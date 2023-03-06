package com.example.backend.proxy.resource.server;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ProxyResourceServerApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(ProxyResourceServerApplication.class)
        .bannerMode(Banner.Mode.OFF)
        .properties("spring.output.ansi.enabled=always")
        .run(args);
  }

}

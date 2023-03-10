package com.example.tokenrelay.resource.server;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class TokenRelayingResourceServerApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(TokenRelayingResourceServerApplication.class)
        .bannerMode(Banner.Mode.OFF)
        .properties("spring.output.ansi.enabled=always")
        .run(args);
  }

}

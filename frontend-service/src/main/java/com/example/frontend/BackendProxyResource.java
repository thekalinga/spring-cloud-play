package com.example.frontend;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static com.example.frontend.Util.getAccessToken;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/backend")
public class BackendProxyResource {
  private final RestTemplate tokenRelayingResttemplate;

  public BackendProxyResource(RestTemplate tokenRelayingResttemplate) {
    this.tokenRelayingResttemplate = tokenRelayingResttemplate;
  }

  @GetMapping
  public String hello(HttpServletRequest request) {
    return tokenRelayingResttemplate.getForObject("http://backend.localtest.me:8081/", String.class);
  }

  @GetMapping("accessible")
  public String accessible(HttpServletRequest request) {
    return tokenRelayingResttemplate.getForObject("http://backend.localtest.me:8081/accessible", String.class);
  }

  @GetMapping("inaccessible")
  public String inaccessible(HttpServletRequest request) {
    return tokenRelayingResttemplate.getForObject("http://backend.localtest.me:8081/inaccessible", String.class);
  }

}

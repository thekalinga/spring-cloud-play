package com.example.frontend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RestController
@RequestMapping("/backend")
public class BackendProxyController {
  private final RestTemplate tokenRelayingRestTemplate;

  public BackendProxyController(RestTemplate tokenRelayingRestTemplate) {
    this.tokenRelayingRestTemplate = tokenRelayingRestTemplate;
  }

  @GetMapping
  public String hello() {
    return tokenRelayingRestTemplate.getForObject("http://backend-resource-server", String.class);
  }

  @GetMapping("read")
  public String read() {
    return tokenRelayingRestTemplate.getForObject("http://backend-resource-server/read", String.class);
  }

  @GetMapping("write")
  public String write() {
    tokenRelayingRestTemplate.postForObject("http://backend-resource-server/write", "Modified by frontend", Void.class);
    return "Write successful. Now <a href=\"read\">read</a> written value";
  }

  @GetMapping("inaccessible")
  public String inaccessible() {
    return tokenRelayingRestTemplate.getForObject("http://backend-resource-server/inaccessible", String.class);
  }

  @GetMapping("client_only_direct")
  public String clientOnly() {
    return tokenRelayingRestTemplate.getForObject("http://backend-resource-server/client_credentials_only", String.class);
  }

  @GetMapping("client_only_via_client_service")
  public String clientOnlyViaClientService() {
    return tokenRelayingRestTemplate.getForObject("http://backed-client-credentials.localtest.me:8082", String.class);
  }

}

package com.example.frontend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/backend")
public class BackendProxyController {
  private final RestTemplate tokenRelayingResttemplate;

  public BackendProxyController(RestTemplate tokenRelayingResttemplate) {
    this.tokenRelayingResttemplate = tokenRelayingResttemplate;
  }

  @GetMapping
  public String hello() {
    return tokenRelayingResttemplate.getForObject("http://backend-resource-server.localtest.me:8081", String.class);
  }

  @GetMapping("read")
  public String read() {
    return tokenRelayingResttemplate.getForObject("http://backend-resource-server.localtest.me:8081/read", String.class);
  }

  @GetMapping("write")
  public void write() {
    tokenRelayingResttemplate.postForObject("http://backend-resource-server.localtest.me:8081/write", "Modified by frontend", Void.class);
  }

  @GetMapping("inaccessible")
  public String inaccessible() {
    return tokenRelayingResttemplate.getForObject("http://backend-resource-server.localtest.me:8081/inaccessible", String.class);
  }

  @GetMapping("client_only_direct")
  public String clientOnly() {
    return tokenRelayingResttemplate.getForObject("http://backend-resource-server.localtest.me:8081/client_credentials_only", String.class);
  }

  @GetMapping("client_only_via_client_service")
  public String clientOnlyViaClientService() {
    return tokenRelayingResttemplate.getForObject("http://backed-client-credentials.localtest.me:8082", String.class);
  }

}

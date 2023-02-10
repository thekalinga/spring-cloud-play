package com.example.backend.resource.server;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BackendResourceSeverController {

  @GetMapping
  public String hello() {
    return "Backend resource server: Hello!";
  }

  String value = "Resource server default";

  @Secured("SCOPE_resource.read")
  @GetMapping("read")
  public String read() {
    return "Backend resource server: Value = " + value;
  }

  @Secured("SCOPE_resource.write")
  @PostMapping("write")
  public void write(@RequestBody String value) {
    this.value = value;
  }

  @Secured("SCOPE_resource.client_credentials_only")
  @GetMapping("client_credentials_only")
  public String clientCredentialsOnly() {
    return "Backend resource server: Hello client credentials proxy!";
  }

  @Secured("SCOPE_backend.inaccessible")
  @GetMapping("inaccessible")
  public String inaccessible() {
    return "Backend resource server: YOU SHOULD NEVER SEE THIS.";
  }

}

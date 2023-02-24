package com.example.backend.resource.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BackendResourceSeverController {

  private final String instanceId;
  private String value = "Resource server default";

  public BackendResourceSeverController(@Value("${instanceid:}") String instanceId) {
    this.instanceId = instanceId;
  }

  @GetMapping
  public String hello() {
    return wrapWithInstanceId("Backend resource server: Hello!");
  }

  @Secured("SCOPE_resource.read")
  @GetMapping("read")
  public String read() {
    return wrapWithInstanceId("Backend resource server: Value = " + value);
  }

  @Secured("SCOPE_resource.write")
  @PostMapping("write")
  public void write(@RequestBody String value) {
    this.value = value;
  }

  @Secured("SCOPE_resource.client_credentials_only")
  @GetMapping("client_credentials_only")
  public String clientCredentialsOnly() {
    return wrapWithInstanceId("Backend resource server: Hello client credentials proxy!");
  }

  @Secured("SCOPE_backend.inaccessible")
  @GetMapping("inaccessible")
  public String inaccessible() {
    return wrapWithInstanceId("Backend resource server: YOU SHOULD NEVER SEE THIS.");
  }

  private String wrapWithInstanceId(String input) {
    return StringUtils.hasText(instanceId) ? "[" + instanceId + "]: " + input : input;
  }
}

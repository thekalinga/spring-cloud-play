package com.example.backend.resource.server;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@RequestMapping("/")
public class ResourceSeverController {

  private final String instanceId;
  private String value = "Resource server default";

  public ResourceSeverController(@Value("${instanceid:}") String instanceId) {
    this.instanceId = instanceId;
  }

  @GetMapping
  public Mono<String> hello() {
    return wrapWithInstanceId("Resource server: Hello!");
  }

  @GetMapping("read")
  @PreAuthorize("hasAuthority('SCOPE_RESOURCE_READ')")
  public Mono<String> read(Authentication authentication) {
    log.debug(authentication);
    return wrapWithInstanceId("Resource server: Value = " + value);
  }

  @PostMapping("write")
  @PreAuthorize("hasAuthority('SCOPE_RESOURCE_WRITE')")
  public Mono<Void> write(@RequestBody String value) {
    this.value = value;
    return Mono.empty();
  }

  @GetMapping("inaccessible")
  @PreAuthorize("hasAuthority('SCOPE_RESOURCE_INACCESSIBLE')")
  public Mono<String> inaccessible() {
    return wrapWithInstanceId("Resource server: YOU SHOULD NEVER SEE THIS.");
  }

  @GetMapping("hybrid")
  @PreAuthorize("hasAuthority('SCOPE_RESOURCE_HYBRID')")
  public Mono<String> hybridClientEndpoint() {
    return wrapWithInstanceId("Resource server: Hello hybrid!");
  }

  @GetMapping("standalone")
  @PreAuthorize("hasAuthority('SCOPE_RESOURCE_STANDALONE_CLIENT')")
  public Mono<String> standaloneClientEndpoint() {
    return wrapWithInstanceId("Resource server: Hello standalone client!");
  }

  private Mono<String> wrapWithInstanceId(String input) {
    return Mono.just(StringUtils.hasText(instanceId) ? "[" + instanceId + "]: " + input : input);
  }
}

package com.example.resourceservice;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BackendResource {

  @GetMapping
  public String hello() {
    return "hello from backend";
  }

  @Secured("SCOPE_backend.read")
  @GetMapping("accessible")
  public String accessible() {
    return "should be accessible";
  }

  @Secured("SCOPE_backend.inaccessible")
  @GetMapping("inaccessible")
  public String inaccessible() {
    return "should be inaccessible";
  }

}

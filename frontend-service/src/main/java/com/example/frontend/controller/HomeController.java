package com.example.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/")
public class HomeController {

  @GetMapping
  public Rendering index(WebSession session, ServerWebExchange exchange) {
    return Rendering.view("index.html")
        .modelAttribute("session_id", session.getId())
        .build();
  }

}

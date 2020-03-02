package com.blockchain.webapp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringController {

  @RequestMapping("/")
  public String index() {
    return "Greetings from Spring Boot";
  }

  @RequestMapping("/health")
  public String health() {
    return "{health: good}\n";
  }

}

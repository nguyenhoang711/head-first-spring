package com.github.nguyenhoang711.head_first_spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StartupLogger {

  @Value("${host}")
  private String host;

  @Value("${port}")
  private String port;

  private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  @EventListener(ApplicationReadyEvent.class)
  public void logSwaggerUrl() {
    if (host == null || host.isEmpty()) {
      host = "localhost";
    }
    if (port == null || port.isEmpty()) {
      port = "8080";
    }
    String swaggerUrl = "http://" + host + port + "/api/v1/swagger-ui/index.html";
    logger.info("Swagger UI is available at: {}", swaggerUrl);
  }
}

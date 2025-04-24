package com.github.nguyenhoang711.head_first_spring.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import com.github.nguyenhoang711.head_first_spring.dto.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(jakarta.servlet.http.HttpServletRequest request,
      jakarta.servlet.http.HttpServletResponse response,
      AuthenticationException authException) throws IOException, jakarta.servlet.ServletException {
    response.setContentType("application/json");
    response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(
        new ErrorResponse(401, "Unauthorized", authException.getMessage()));
    response.getWriter().write(json);
  }
}

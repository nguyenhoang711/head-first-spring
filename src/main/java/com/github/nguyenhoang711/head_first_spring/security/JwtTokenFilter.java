package com.github.nguyenhoang711.head_first_spring.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.nguyenhoang711.head_first_spring.entity.User;
import com.github.nguyenhoang711.head_first_spring.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String requestURI = request.getRequestURI();

    if (requestURI.startsWith("/api/v1/auth/") || requestURI.startsWith("/swagger-ui/")
        || requestURI.startsWith("/v3/api-docs/")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = resolveToken(request);
    System.out.println("Token: " + token);

    try {
      if (token != null && jwtTokenProvider.validateToken(token)) {
        String username = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username);
        List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);

        if (user != null) {
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              username, null, authorities);
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } else {
        System.out.println("Invalid jưt token");
      }
    } catch (Exception e) {
      System.out.println("Error processing token: " + e.getMessage());
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
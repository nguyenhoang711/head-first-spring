package com.github.nguyenhoang711.head_first_spring.security;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = resolveToken(request);
    log.info("Token: {}", token);

    try {
      if (token != null && jwtTokenProvider.validateToken(token)) {
        String username = jwtTokenProvider.getUsername(token);
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username);
        List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
        // get all authorities in token

        if (user != null) {
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              username, null, authorities);
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } else {
        log.info("Invalid token or token is not exist");
      }
    } catch (Exception e) {
      log.error("Error processing token: {}", e.getMessage());
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    // add Bearer before token
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
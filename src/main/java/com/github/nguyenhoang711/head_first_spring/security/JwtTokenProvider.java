package com.github.nguyenhoang711.head_first_spring.security;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.github.nguyenhoang711.head_first_spring.entity.Role;
import com.github.nguyenhoang711.head_first_spring.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

  @Value("${jwtSecret}")
  private String secretKey;

  @Value("${jwtExpirationMs}")
  private long validityInMilliseconds;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String createToken(User user) {
    Claims claims = Jwts.claims();
    claims.put("id", user.getId());
    claims.put("username", user.getUsername());

    List<String> roles = user.getRoles().stream()
        .map(Role::getName)
        .collect(Collectors.toList());
    claims.put("roles", roles);

    Set<String> permissions = new HashSet<>();
    user.getRoles().forEach(role -> role.getPermissions().forEach(permission -> permissions.add(permission.getName())));
    claims.put("permissions", permissions);

    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public String getUsername(String token) {
    Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    String username = claims.get("username", String.class); // Lấy username từ claims
    return username;
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public List<GrantedAuthority> getAuthorities(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();

    List<GrantedAuthority> authorities = new ArrayList<>();

    // Thêm roles vào authorities
    @SuppressWarnings("unchecked")
    List<String> roles = (List<String>) claims.get("roles");
    if (roles != null) {
      roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
    }

    // Thêm permissions vào authorities
    @SuppressWarnings("unchecked")
    List<String> permissions = (List<String>) claims.get("permissions");
    if (permissions != null) {
      permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
    }

    return authorities;
  }
}
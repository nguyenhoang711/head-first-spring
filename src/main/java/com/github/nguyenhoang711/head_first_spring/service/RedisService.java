package com.github.nguyenhoang711.head_first_spring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nguyenhoang711.head_first_spring.dto.request.RegisterDto;
import com.github.nguyenhoang711.head_first_spring.dto.response.UserResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Getter
@Setter
public class RedisService {
    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;

    public RedisService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveToken(String username, String token, long ttlSeconds) {
        try {
            log.info("Bắt đầu lưu token cho user: {}, host: {}", username,
                    redisTemplate.getConnectionFactory().getConnection().getClientName());
            String key = "token:" + username;
            redisTemplate.opsForValue().set(key, token, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Lỗi khi lưu token cho user: {}", username, e);
            throw new RuntimeException("Không thể kết nối Redis", e);
        }
    }

    // check token exist and validate in redis
    public boolean isTokenValid(String username, String token) {
        String key = "token:" + username;
        String storedToken = (String) redisTemplate.opsForValue().get(key);
        return storedToken != null && storedToken.equals(token);
    }

    // save user info in redis
    public void saveUserInfo(String username, UserResponse userResponse) {
        String key = "user:" + username;
        try{
            String json = objectMapper.writeValueAsString(userResponse);
            redisTemplate.opsForValue().set(key, json, 3600, TimeUnit.SECONDS); //TTL 1h
        } catch (Exception e) {
            log.error("Error saving user info for: {}", username, e);
        }
    }

    // get user info from Redis
    public UserResponse getUserInfo(String username){
        String key = "user:" + username;
        String json = (String) redisTemplate.opsForValue().get(key);

        if(json != null){
            try{
                UserResponse userResponse = objectMapper.readValue(json, UserResponse.class);
                return userResponse;
            } catch (Exception e) {
                log.error("error when parsing user info for: {}", username, e);
            }
        }
        return null;
    }

    public void deleteToken(String username){
        String key = "token:" + username;
        redisTemplate.delete(key);
    }


    public void deleteUserInfo(String username) {
        String key = "user:" + username;
        redisTemplate.delete(key);
        log.info("Deleted user info for user: {}, key: {}", username, key);
    }

    public void blacklistToken(String token, long ttlSeconds) {
        try {
            String key = "blacklist:token:" + token;
            redisTemplate.opsForValue().set(key, "blacklisted", ttlSeconds, TimeUnit.SECONDS);
            log.info("Blacklisted token: {}", token);
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", token, e);
            throw new RuntimeException("Cannot connect to Redis", e);
        }
    }

    // Kiểm tra token có trong blacklist hay không
    public boolean isTokenBlacklisted(String token) {
        String key = "blacklist:token:" + token;
        Boolean exists = redisTemplate.hasKey(key);
        log.info("Checked if token is blacklisted: {}, result: {}", token, exists != null && exists);
        return exists != null && exists;
    }

    public void savePendingRegistration(RegisterDto registerDto, long ttlSeconds) {
        String username = registerDto.getUsername();
        String key = "pending:register:" + username;
        try {
            String json = objectMapper.writeValueAsString(registerDto);
            redisTemplate.opsForValue().set(key, json, ttlSeconds, TimeUnit.SECONDS);
            log.info("Saved pending registration for: {}, key: {}", username, key);
        } catch (Exception e) {
            log.error("Error saving pending registration for: {}", username, e);
            throw new RuntimeException("Không thể lưu thông tin đăng ký", e);
        }
    }

    public RegisterDto getPendingRegistration(String username) {
        String key = "pending:register:" + username;
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                RegisterDto authDto = objectMapper.readValue(json, RegisterDto.class);
                log.info("Retrieved pending registration for: {}, key: {}", username, key);
                return authDto;
            } catch (Exception e) {
                log.error("Error parsing pending registration for: {}", username, e);
            }
        }
        return null;
    }

    public void deletePendingRegistration(String username) {
        String key = "pending:register:" + username;
        redisTemplate.delete(key);
        log.info("Deleted pending registration for: {}, key: {}", username, key);
    }
}

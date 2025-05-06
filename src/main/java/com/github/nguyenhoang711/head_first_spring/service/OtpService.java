package com.github.nguyenhoang711.head_first_spring.service;

import com.github.nguyenhoang711.head_first_spring.constant.OtpType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OtpService {
    @Autowired
    private RedisService redisService;

    public void saveOtp(String username, String otp, long ttlSeconds, OtpType otpType) {
        try {
            String key = String.format("otp:%s:%s", otpType.name().toLowerCase(), username);
            redisService.getRedisTemplate().opsForValue().set(key, otp, ttlSeconds, TimeUnit.SECONDS);
            log.info("Saved OTP for user: {}, type: {}, key: {}", username, otpType, key);
        } catch (Exception e) {
            log.error("Error saving OTP for user: {}, type: {}", username, otpType, e);
            throw new RuntimeException("Cannot save OTP", e);
        }
    }

    public void deleteOtp(String username) {
        String key = "otp:" + username;
        redisService.getRedisTemplate().delete(key);
        log.info("Deleted OTP for user: {}, key: {}", username, key);
    }

    public boolean validateOtp(String username, String otp, OtpType otpType) {
        String key = String.format("otp:%s:%s", otpType.name().toLowerCase(), username);
        String storedOtp = (String) redisService.getRedisTemplate().opsForValue().get(key);
        boolean isValid = storedOtp != null && storedOtp.equals(otp);
        if(isValid){
            redisService.getRedisTemplate().delete(key);
            log.info("Verified OTP for user: {}, type: {} key: {}", username, otpType.getName(), key);
        } else {
            log.warn("Invalid OTP for user: {}, type: {}, key: {}", username, otpType.getName(), key);
            return false;
        }
        return true;
    }
}

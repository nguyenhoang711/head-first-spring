package com.github.nguyenhoang711.head_first_spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.nguyenhoang711.head_first_spring.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsernameAndDeletedAtIsNull(String username);

  Optional<User> findByIdAndDeletedAtIsNull(Long id);

  List<User> findAllByDeletedAtIsNull();
}
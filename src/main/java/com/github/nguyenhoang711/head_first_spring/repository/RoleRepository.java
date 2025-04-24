package com.github.nguyenhoang711.head_first_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.nguyenhoang711.head_first_spring.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Role findByName(String name);
}

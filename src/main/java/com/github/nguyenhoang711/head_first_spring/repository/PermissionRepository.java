package com.github.nguyenhoang711.head_first_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.nguyenhoang711.head_first_spring.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
  Permission findByName(String name);
}

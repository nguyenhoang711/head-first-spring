package com.github.nguyenhoang711.head_first_spring.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Common {
  @Column(name = "created_at")
  public LocalDateTime createdAt;

  @Column(name = "updated_at")
  public LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  public LocalDateTime deletedAt;
}
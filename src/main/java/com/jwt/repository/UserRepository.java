package com.jwt.repository;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jwt.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @NotNull Optional<User> findById(@NotNull Long id);
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

}
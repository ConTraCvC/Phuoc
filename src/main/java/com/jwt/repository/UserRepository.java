package com.jwt.repository;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jwt.models.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @EntityGraph("joined")
  @NotNull Optional<User> findById(@NotNull Long id);

  @EntityGraph("roleJoin")
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  @EntityGraph("roleJoin")
  User findByEmail(String email);


  @Modifying(clearAutomatically = true)
  @Query("update User c set c.password = :password where c.email = :email")
  void changePassword(@Param("password") String password, @Param("email") String email);

  @Modifying(clearAutomatically = true)
  @Query("delete User c where c.id = :id")
  void deleteByUserId(@Param("id") @NotNull Long id );
}
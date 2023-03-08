package com.jwt.repository;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;
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

  @EntityGraph("roleJoin")
  Optional<User> findByEmail(String email);

  @Modifying(clearAutomatically = true)
  @Query("update User c set c.password = :password where c.email = :email")
  void changePassword(@Param("password") String password, @Param("email") String email);

  @EntityGraph("roleJoin")
  @Query("select u from User u")
  List<User> findAllUser();

}
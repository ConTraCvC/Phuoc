package com.jwt.repository;

import com.jwt.models.PasswordResetToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  @EntityGraph("PassJoin")
  PasswordResetToken findByToken(String token);

  void deleteByToken(String token);

  @Transactional
  @Modifying(clearAutomatically = true)
  // delete all except the newest one group by user_id.
  @Query(value = "delete from password_reset_token where user_id and id not in (select * from (select max(id) as id from password_reset_token group by user_id) as t2)", nativeQuery = true)
  void deleteAll();
}

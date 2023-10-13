package com.jwt.repository;

import com.jwt.models.PasswordResetToken;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "tokenJoin")
  @Cacheable("token")
  PasswordResetToken findByToken(String token);

  Optional<PasswordResetToken> findByUserId(Long id);

//  @Transactional
//  @Modifying(clearAutomatically = true)
//  // delete all except the newest one group by user_id.
//  @Query(value = "delete from password_reset_token where user_id and id not in (select * from (select max(id) as id from password_reset_token group by user_id) as t2)", nativeQuery = true)
//  void deleteAll();

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(value = "update dev1.password_reset_token set token=:token, expiration_time=:expiration_time where user_id=:user_id", nativeQuery = true)
  void updateToken(@Param("token") String token, @Param("expiration_time") Date date, @Param("user_id") Long id);
}

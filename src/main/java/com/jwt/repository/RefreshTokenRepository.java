package com.jwt.repository;

import com.jwt.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  @Transactional
  @Modifying(clearAutomatically = true)
  // delete all except the newest one group by user_id.
  @Query(value = "delete from refresh_token where user_id and id not in (select * from (select max(id) as id from refresh_token group by user_id) as t1)", nativeQuery = true)
  void deleteAll();
}

package com.jwt.repository;

import com.jwt.models.RefreshToken;
import com.jwt.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  @EntityGraph("joined")
  Optional<RefreshToken> findByToken(String token);

  @Modifying(clearAutomatically = true)
  // delete all except the newest one group by user_id.
  @Query(value = "delete from refresh_token where user_id and id not in (select * from (select max(id) as id from refresh_token group by user_id) as t1)", nativeQuery = true)
  void deleteAllRf();

  @Modifying(clearAutomatically = true)
  @Query(value = "update refresh_token set token=:token where user_id=:user_id", nativeQuery = true)
  void updateRefreshToken(@Param("token")String token, @Param("user_id") Long id);

  @Modifying(clearAutomatically = true)
  @Query("delete refreshToken c where c.user = :user")
  void deleteByTokenId(@Param("user") User id);
}

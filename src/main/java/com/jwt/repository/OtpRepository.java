package com.jwt.repository;

import com.jwt.models.Otp;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

  @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "otpJoin")
  @Cacheable("otp")
  Otp findByOtp(int otp);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete Otp o where o.otp=:otp")
  void deleteByOtp(@Param("otp") int otp);

  @Transactional
  @Modifying(clearAutomatically = true)
  // delete all except the newest one group by user_id.
  @Query(value = "delete from otp where user_id and id not in (select * from (select max(id) as id from otp group by user_id) as t2)", nativeQuery = true)
  void deleteAllOtp();

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("update Otp o set o.realTime=:realTime")
  void setExpiredTime(@Param("realTime") Date realTime);
}

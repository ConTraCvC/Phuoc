package com.jwt.repository;

import com.jwt.models.Otp;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

  @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "otpJoin")
  @Cacheable("otp")
  Otp findByOtp(int otp);

  @Transactional
  @Modifying(clearAutomatically = true)
  // delete all except the newest one group by user_id.
  @Query(value = "delete from dev1.otp where (user_id,id) not in (select user_id, max(id) from dev1.otp group by user_id);", nativeQuery = true)
  void deleteAllOtp();

}
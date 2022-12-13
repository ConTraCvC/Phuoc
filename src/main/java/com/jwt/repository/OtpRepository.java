package com.jwt.repository;

import com.jwt.payload.request.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

  Otp findByOtp(int otp);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete from Otp c where c.otp = :otp")
  void deleteBy(@Param("otp") int otp);
}

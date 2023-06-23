package com.jwt.repository;

import com.jwt.models.CustomerPort;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortsRepository extends JpaRepository<CustomerPort, Long> {

  @Query(value = "select port from customer_port", nativeQuery = true)
  int[] findAllBy();

  Optional<CustomerPort> findByPort(int port);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("delete from CustomerPort where port=:port")
  void deletePort(int port);
}

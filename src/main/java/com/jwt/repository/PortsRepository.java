package com.jwt.repository;

import com.jwt.models.CustomerPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PortsRepository extends JpaRepository<CustomerPort, Long> {

  @Query(value = "select ports from customer_port", nativeQuery = true)
  int findAllBy();
}

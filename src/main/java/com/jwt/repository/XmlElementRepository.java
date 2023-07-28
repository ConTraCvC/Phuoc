package com.jwt.repository;

import com.jwt.models.XmlEle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface XmlElementRepository extends JpaRepository<XmlEle, Long> {

  @Query("from XmlEle x where x.data_length=:data_length and x.data_type=:data_type")
  XmlEle findAllByLengthAndType(@Param("data_length") int data_length, @Param("data_type") String data_type);

  Optional<XmlEle> findByField(String field);
}

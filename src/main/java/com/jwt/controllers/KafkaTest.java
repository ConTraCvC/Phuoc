package com.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class KafkaTest {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @PostMapping("/kafka")
  public ResponseEntity<?> sendTransfer() {
    Long value = 1L;
    kafkaTemplate.send("phuoc", value.toString());
    return ResponseEntity.ok("Transfer complete: " + value);
  }
}

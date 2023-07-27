package com.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class KafkaTest {

  private final KafkaTemplate<String, String> kafkaTemplate;

  @PostMapping("/kafka")
  public ResponseEntity<?> sendTransfer() {
    StringBuilder sb = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dell\\Desktop\\PR001.dat"))) {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    String str = sb.toString();
    kafkaTemplate.send("string", str);
    System.out.println("Transfer complete: " + str);
    return ResponseEntity.ok("Transfer complete: " + str);
  }

  @KafkaListener(
          topics = "xml",
          groupId = "groupId",
          containerFactory = "kafkaListenerContainerFactory"
  )
  public void testPrams(String data) {
    System.out.println(data);
  }
}

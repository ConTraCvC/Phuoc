package com.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class KafkaTest {

  private final KafkaTemplate<String, String> kafkaTemplate;

  private static final String TRANSACTION = "Transaction";
  private static final String COMMON = "Common";
  private static final String INDIVIDUAL = "Individual";

  @PostMapping("/kafka")
  public ResponseEntity<?> sendTransfer() {
    ArrayList<String> list = new ArrayList<String>();
    try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dell\\Desktop\\PR001.dat"))) {
      String line;
      while ((line = br.readLine()) != null) {
        list.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    StringBuilder sb = new StringBuilder();
    Iterator<String> iterator = list.iterator();
    while (iterator.hasNext()) {
      sb.append(iterator.next());
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

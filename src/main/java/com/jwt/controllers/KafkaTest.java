package com.jwt.controllers;

import com.jwt.models.FinalObjectLength;
import com.jwt.models.User;
import com.jwt.models.XmlEle;
import com.jwt.repository.XmlElementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class KafkaTest {

  private final KafkaTemplate<String, String> kafkaTemplate;

  private static final String common = "Common";
  private static final String individual = "Individual";
  private String result;
  private final PadService padService;

  private final XmlElementRepository repository;
  private final int[] array = new int[]{FinalObjectLength.coNo_SIZE, FinalObjectLength.msgDscd_SIZE, FinalObjectLength.reqRsqDscd_SIZE,
          FinalObjectLength.msgNo_SIZE, FinalObjectLength.tmsDt_SIZE, FinalObjectLength.tmsTm_SIZE, FinalObjectLength.rspCd_SIZE, FinalObjectLength.idCd_SIZE,
          FinalObjectLength.dataCount_SIZE, FinalObjectLength.etcAr_SIZE, FinalObjectLength.wdracNo_SIZE, FinalObjectLength.actPwno_SIZE,
          FinalObjectLength.rercdMrk_SIZE, FinalObjectLength.wdrAm_SIZE, FinalObjectLength.remaining_Balance_boolean_SIZE, FinalObjectLength.remaining_Balance_SIZE,
          FinalObjectLength.tobkDscd_SIZE, FinalObjectLength.itDscd_SIZE, FinalObjectLength.curCd_SIZE, FinalObjectLength.inCdAccGb_SIZE, FinalObjectLength.rcvbk1Cd_SIZE, FinalObjectLength.rcvbk2Cd_SIZE,
          FinalObjectLength.rcvbkNm_SIZE, FinalObjectLength.rmk_SIZE, FinalObjectLength.rcvacNo_SIZE, FinalObjectLength.fee_SIZE, FinalObjectLength.rcvacDppeNm_SIZE, FinalObjectLength.rspDppeNm_SIZE,
          FinalObjectLength.status_SIZE, FinalObjectLength.trnSrno_SIZE, FinalObjectLength.trnDt_SIZE, FinalObjectLength.depCoNo_SIZE, FinalObjectLength.feeProOcc_SIZE,
          FinalObjectLength.feeInclYn_SIZE, FinalObjectLength.testSpace_SIZE, FinalObjectLength.manyRate_SIZE};


  @PostMapping("/kafka")
  public ResponseEntity<?> sendTransfer(@RequestBody String source) {
    StringBuilder sb = new StringBuilder();
//    try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dell\\Desktop\\PR001.dat"))) {
//      String line;
//      while ((line = br.readLine()) != null) {
//        sb.append(line);
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
    sb.append(source);
    kafkaTemplate.send("string", sb.toString());
    System.out.println("Transfer complete: " + sb);
    return ResponseEntity.ok("Transfer complete: " + result);
  }

  private Document convertStringToDocument(String xmlStr) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc;
    try {
      doc = builder.parse(new InputSource(new StringReader(xmlStr)));
    } catch (IOException | SAXException e) {
      throw new RuntimeException(e);
    }
    return doc;
  }

  @KafkaListener(
          topics = "xml",
          groupId = "groupId",
          containerFactory = "kafkaListenerContainerFactory"
  )
  public void testPrams(String data) throws ParserConfigurationException {
    XmlEle xml_ele = new XmlEle();
    String n = "N";
    Map<Object, Object> contents = new HashMap<>();
    StringBuilder sb = new StringBuilder();
    Document xml = convertStringToDocument(data);

    Node user = xml.getFirstChild();
    NodeList childs = user.getChildNodes();
    Node child;

    for (int i=0; i<childs.getLength(); ++i) {
      child = childs.item(i);
      if (common.equals(child.getNodeName())) {
        Node parentNode = xml.getElementsByTagName(child.getNodeName()).item(0);
        NodeList commonNodes = parentNode.getChildNodes();
        Node commonNode;
        for (int i2=0; i2<commonNodes.getLength(); ++i2) {
          commonNode = commonNodes.item(i2);
          contents.put(commonNode.getNodeName(), commonNode.getTextContent());
        }
      } else if (individual.equals(child.getNodeName())) {
        Node parentNode = xml.getElementsByTagName(child.getNodeName()).item(0);
        NodeList individualNodes = parentNode.getChildNodes();
        Node individualNode;
        for (int i2=0; i2<individualNodes.getLength(); ++i2) {
          individualNode = individualNodes.item(i2);
          contents.put(individualNode.getNodeName(), individualNode.getTextContent());
          if (individualNode.getNodeName().equalsIgnoreCase("wdrAm")) {
            if (repository.findByField("wdrAm").isPresent()) {
              System.out.println("exists");
            } else {
              xml_ele.setData_type("N");
              xml_ele.setField("wdrAm");
              for (int i3 : array) {
                if (i3 == FinalObjectLength.wdrAm_SIZE) {
                  xml_ele.setData_length(FinalObjectLength.wdracNo_SIZE);
                }
              }
              repository.save(xml_ele);
            }
          } else if (individualNode.getNodeName().equalsIgnoreCase("fee")) {
            if (repository.findByField("fee").isPresent()) {
              System.out.println("exists");
            } else {
              xml_ele.setData_type("N");
              xml_ele.setField("fee");
              for (int i3 : array) {
                if (i3 == FinalObjectLength.fee_SIZE) {
                  xml_ele.setData_length(FinalObjectLength.fee_SIZE);
                }
              }
              repository.save(xml_ele);
            }
          } else if (!individualNode.getNodeName().equalsIgnoreCase("fee")
                  && !individualNode.getNodeName().equalsIgnoreCase("wdrAm")) {

          }
        }
      }
    }
    for (Object content : contents.keySet()) {
      sb.append(content);
    }
    result = sb.toString();
    System.out.println(sb);
  }
}

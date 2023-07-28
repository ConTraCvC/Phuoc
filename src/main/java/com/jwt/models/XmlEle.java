package com.jwt.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "xml_element", schema = "dev1")
public class XmlEle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String data_type;

  private int data_length;

  @Column(name = "field")
  private String field;
}

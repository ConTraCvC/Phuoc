package com.jwt.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity(name = "refreshToken")
@NamedEntityGraph(name = "joined", includeAllAttributes = true ,attributeNodes = {
        @NamedAttributeNode(value = "user", subgraph = "user")
}, subgraphs = @NamedSubgraph(name = "user", attributeNodes = { @NamedAttributeNode("roles") }))
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  private User user;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private Instant expiryDate;
}

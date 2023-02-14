package com.jwt.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

package com.jwt.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users",
    uniqueConstraints = { 
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "email") 
    })
@NamedEntityGraph(name = "roleJoin", includeAllAttributes = true ,attributeNodes = {
        @NamedAttributeNode(value = "roles", subgraph = "roles")
}, subgraphs = @NamedSubgraph(name = "roles", attributeNodes = { @NamedAttributeNode("roleCode") }))
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;

  @OneToOne
  @JoinColumn(name = "role_id")
  private Role roles;

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  void SignupRequest(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }
}

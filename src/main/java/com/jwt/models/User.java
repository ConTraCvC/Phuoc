package com.jwt.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users",
    uniqueConstraints = { 
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "email") 
    }, schema = "dev1")
@NamedEntityGraph(name = "roleJoin", includeAllAttributes = true ,attributeNodes = {
        @NamedAttributeNode(value = "roles", subgraph = "roles")
}, subgraphs = @NamedSubgraph(name = "roles", attributeNodes = { @NamedAttributeNode("roleCode") }))
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @JsonInclude(JsonInclude.Include.NON_NULL)
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

}
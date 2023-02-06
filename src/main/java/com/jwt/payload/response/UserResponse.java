package com.jwt.payload.response;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;

import com.jwt.models.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
public class UserResponse implements UserDetails {

  @Serial
  private static final long serialVersionUID = 1L;
  private Long id;
  private String username;
  private String email;
  @JsonIgnore
  private String password;

  private Collection<? extends GrantedAuthority> authorities;

  public static UserResponse build(User user) {
    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getRoleCode().name()))
        .collect(Collectors.toList());

    return new UserResponse(
        user.getId(), 
        user.getUsername(), 
        user.getEmail(),
        user.getPassword(), 
        authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {return password;}

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserResponse user = (UserResponse) o;
    return Objects.equals(id, user.id);
  }
}

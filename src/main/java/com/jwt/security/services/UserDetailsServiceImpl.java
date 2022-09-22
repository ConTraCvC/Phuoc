package com.jwt.security.services;

import com.jwt.models.Role;
import com.jwt.models.User;
import com.jwt.payload.response.ErrorMessage;
import com.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    return UserDetailsImpl.build(user);
  }

//  public ErrorMessage setRoleCode(Role roles) {
//    try {
//      userRepository.setRoles(roles.getId());
//    } catch (Exception e) {
//      return new ErrorMessage()
//              .setErrors(Collections.singletonList("Role's not existed"));
//    }
//    return new ErrorMessage().setEdesc("Username not existed");
//  }

}



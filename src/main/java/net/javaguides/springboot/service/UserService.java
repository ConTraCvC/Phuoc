package net.javaguides.springboot.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import net.javaguides.springboot.web.dto.UserRegistrationDto;

public interface UserService extends UserDetailsService{

	void save(UserRegistrationDto registrationDto);
}

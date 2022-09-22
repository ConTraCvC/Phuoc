package com.jwt.repository;

import java.util.Optional;

import com.jwt.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jwt.models.ERole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  Optional<Role> findByRoleCode(ERole roleCode);
}

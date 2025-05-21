package com.my.myproject.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my.myproject.model.user.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Role findByName(String name);

}


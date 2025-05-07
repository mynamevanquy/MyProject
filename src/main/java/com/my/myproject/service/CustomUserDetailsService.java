package com.my.myproject.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.my.myproject.model.User;
import com.my.myproject.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user =userRepository.findByUserName( username).orElseThrow(() -> new UsernameNotFoundException("User not exists by User name"));
		Set<GrantedAuthority> authorities  =
				user
				.getRoles()
				.stream()
				.map((role) -> new SimpleGrantedAuthority(role
						.getName()))
				.collect(Collectors
						.toSet());
		return new org.springframework.security.core.userdetails.User(username, user.getPassWord(),authorities);
	}
}

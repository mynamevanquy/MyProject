package com.my.myproject.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.my.myproject.dto.LoginDto;
import com.my.myproject.jwt.JwtTokenProvider;
import com.my.myproject.service.AuthService;

@Service
public class AuthSeviceImpl implements AuthService{

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
    private JwtTokenProvider jwtTokenProvider;
    
	@Override
	public String login(LoginDto loginDto) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtTokenProvider.generateToken(authentication);
		return token;
	}

}

package com.my.myproject.controller;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; 
import com.my.myproject.dto.LoginDto;
import com.my.myproject.dto.UserDto;
import com.my.myproject.jwt.JWTAuthResponse;
import com.my.myproject.model.Role;
import com.my.myproject.model.User;
import com.my.myproject.repository.RoleRepository;
import com.my.myproject.repository.UserRepository;
import com.my.myproject.service.AuthService;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@Data
@CrossOrigin(origins = "*")
public class AuthController {
	@Autowired
	private AuthService authService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository roleRepository;
	@PostMapping("/login")
	public ResponseEntity<JWTAuthResponse>authenticate(@RequestBody LoginDto loginDto){
		String token =authService.login(loginDto);
		JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
		jwtAuthResponse.setAccessToken(token);
		jwtAuthResponse.setTypeToken("Bearer token");
		return ResponseEntity.ok(jwtAuthResponse);
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> Register(@RequestBody UserDto userdto){
		User user = new User();
		user.setDiaChi(userdto.getDiaChi());
		user.setPassWord(passwordEncoder.encode(userdto.getPassWord()));
		user.setEmail(userdto.getEmail());
		user.setName(userdto.getName());
		user.setSdt(userdto.getSdt());
		user.setUserName(userdto.getUsername());
		Role role = roleRepository.findByName("ROLE_USER");
		user.setRoles(new HashSet<Role>(Arrays.asList(role)));
		if(userRepository.existsByUserName(userdto.getUsername())) {
			return new ResponseEntity<>("Tên tài khoản đã tồn tại!", HttpStatus.BAD_REQUEST);
		}		
		if(userRepository.existsByEmail(userdto.getEmail())) {
			return new ResponseEntity<>("Email đã tồn tại!", HttpStatus.BAD_REQUEST);
		}
		User save= userRepository.save(user);
		if(save==null) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Created fail");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body("Created success");
	}
	

	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> all(){
		List<User> user = userRepository.findAll();
		return new ResponseEntity<Object>(user,HttpStatus.OK);
	}
}

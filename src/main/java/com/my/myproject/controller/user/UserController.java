package com.my.myproject.controller.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.my.myproject.dto.UserDto;
import com.my.myproject.jwt.JwtTokenProvider;
import com.my.myproject.model.Role;
import com.my.myproject.model.User;
import com.my.myproject.repository.RoleRepository;
import com.my.myproject.repository.UserRepository;
import com.my.myproject.util.ApiResponse;



@RestController
@RequestMapping("/api/user/")

public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository roleRepository;

	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> all() {
		List<User> user = userRepository.findAll();
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> userinfor(@PathVariable Long id) {
		Optional<User> user = userRepository.findById(id);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@GetMapping("/me")
	public ResponseEntity<?> getCrrentUser(Authentication authentication) {
		if (authentication != null) {
			org.springframework.security.core.userdetails.User userdetail = (org.springframework.security.core.userdetails.User) authentication
					.getPrincipal();
			Optional<User> user = userRepository.findByUserName(userdetail.getUsername());
			if (user != null) {
				return new ResponseEntity<>(user, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Người dùng không tồn tại", HttpStatus.NOT_FOUND);
			}
		} else {
			return ResponseEntity.status(401).body("Người dùng hiện tại không được xác thực.");
		}

	}

	String uploadDirectory = System.getProperty("/home/user/uploads");
	
	
@PostMapping("/adduser")
public ResponseEntity<?> createUser(@RequestBody UserDto request, @AuthenticationPrincipal UserDetails userDetails) {
    boolean isAdmin = userDetails.getAuthorities()
            .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    
    if (!isAdmin) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Admin only.");
    }

    if (userRepository.existsByUserName(request.getUsername())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists.");
    }
    if (userRepository.existsByEmail(request.getEmail())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists.");
    }

    String roleName = "ROLE_" + request.getRoles().toUpperCase();
    Role userRole = roleRepository.findByName(roleName);
    if (userRole == null) {
        return ResponseEntity.badRequest().body("Không tìm thấy vai trò: " + roleName);
    }

    User user = new User();
    user.setName(request.getName());
    user.setUserName(request.getUsername());
    user.setPassWord(passwordEncoder.encode(request.getPassWord()));
    user.setEmail(request.getEmail());
    user.setSdt(request.getSdt());
    user.setDiaChi(request.getDiaChi());
    user.setImage(request.getImage());

    Set<Role> roles = new HashSet<>();
    roles.add(userRole);
    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok("User created successfully.");
}


	


@PutMapping("/update")
public ResponseEntity<?> updateUser(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody UserDto userDto) {

    try {
        // Lấy username từ token
        String username = userDetails.getUsername();
        String token = jwtTokenProvider.getTokenForUser(username);
        Long userId = Long.valueOf(jwtTokenProvider.getIdJwt(token));

        // Kiểm tra quyền admin
        boolean isAdmin = userDetails.getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Kiểm tra quyền update chính mình hoặc là admin
        if (!isAdmin && !userDto.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Không có quyền chỉnh sửa tài khoản người khác :)");
        }
        System.out.println("userDto.getUsername() = " + userDto.getUsername());

        Optional<User> existingUser = userRepository.findByUserName(userDto.getUsername());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userDto.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists.");
        }

        // Lấy user từ DB
        Optional<User> optionalUser = userRepository.findById(userDto.getId());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        User user = optionalUser.get();

        // Kiểm tra email đã tồn tại cho user khác
        boolean emailExists = userRepository.existsByEmailAndIdNot(
                userDto.getEmail(), userDto.getId());
        if (emailExists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã tồn tại!");
        }

        // Cập nhật thông tin
        user.setName(userDto.getName());
        user.setUserName(userDto.getUsername()); // không đổi username
        user.setPassWord(passwordEncoder.encode(userDto.getPassWord()));
        user.setEmail(userDto.getEmail());
        user.setDiaChi(userDto.getDiaChi());
        user.setSdt(userDto.getSdt());

        // Nếu là admin thì mới được đổi vai trò
        if (isAdmin) {
            Role role = roleRepository.findByName("ROLE_" + userDto.getRoles().toUpperCase());
            if (role == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Không tìm thấy vai trò phù hợp!");
            }
            user.setRoles(new HashSet<>(Arrays.asList(role)));
        }

        userRepository.save(user);
        return ResponseEntity.ok("Cập nhật thông tin người dùng thành công!");

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi cập nhật người dùng.");
    }
}
}
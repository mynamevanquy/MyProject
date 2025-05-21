package com.my.myproject.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my.myproject.dto.PasswordForgotDto;
import com.my.myproject.dto.PasswordForgotDto.PasswordResetDto;
import com.my.myproject.model.user.User;
import com.my.myproject.repository.user.UserRepository;
import com.my.myproject.service.PasswordResetService;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "*")
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    @PostMapping("/forgot")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody PasswordForgotDto passwordForgotDto) {
        Map<String, String> response = new HashMap<>();
        
        User user = userRepository.findByEmail(passwordForgotDto.getEmail());
        if (user == null) {
            response.put("message", "Email không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        passwordResetService.createPasswordResetTokenForUser(user);
        response.put("message", "Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra hộp thư của bạn.");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/validateToken")
    public ResponseEntity<Map<String, String>> validateToken(@RequestParam("token") String token) {
        Map<String, String> response = new HashMap<>();
        
        String result = passwordResetService.validatePasswordResetToken(token);
        if (result.equals("valid")) {
            response.put("message", "Token hợp lệ");
            return ResponseEntity.ok(response);
        } else if (result.equals("expired")) {
            response.put("message", "Token đã hết hạn");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            response.put("message", "Token không hợp lệ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        Map<String, String> response = new HashMap<>();
        
        // Kiểm tra token
        String result = passwordResetService.validatePasswordResetToken(passwordResetDto.getToken());
        if (!result.equals("valid")) {
            response.put("message", "Token không hợp lệ hoặc đã hết hạn");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if (!passwordResetDto.getPassword().equals(passwordResetDto.getConfirmPassword())) {
            response.put("message", "Mật khẩu và xác nhận mật khẩu không khớp");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Thay đổi mật khẩu
        User user = passwordResetService.getUserByPasswordResetToken(passwordResetDto.getToken());
        passwordResetService.changeUserPassword(user, passwordResetDto.getPassword());
        
        response.put("message", "Đặt lại mật khẩu thành công");
        return ResponseEntity.ok(response);
    }
}

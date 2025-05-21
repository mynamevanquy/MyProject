package com.my.myproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.my.myproject.model.user.PasswordResetToken;
import com.my.myproject.model.user.User;
import com.my.myproject.repository.user.PasswordResetTokenRepository;
import com.my.myproject.repository.user.UserRepository;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void createPasswordResetTokenForUser(User user) {
        // Xóa token cũ nếu có
        PasswordResetToken existingToken = tokenRepository.findByUser(user);
        if (existingToken != null) {
            tokenRepository.delete(existingToken);
        }
        
        // Tạo token mới
        PasswordResetToken myToken = new PasswordResetToken(user);
        tokenRepository.save(myToken);
        
        // Gửi email với link reset mật khẩu
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Yêu cầu đặt lại mật khẩu");
        message.setText("Để đặt lại mật khẩu, vui lòng nhấp vào liên kết: "
                + "http://localhost:3000/reset-password?token=" + myToken.getToken());
        mailSender.send(message);
    }
    
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passToken = tokenRepository.findByToken(token);
        
        if (passToken == null) {
            return "invalidToken";
        }
        
        if (passToken.isExpired()) {
            tokenRepository.delete(passToken);
            return "expired";
        }
        
        return "valid";
    }
    
    public User getUserByPasswordResetToken(String token) {
        PasswordResetToken passToken = tokenRepository.findByToken(token);
        return passToken.getUser();
    }
    
    public void changeUserPassword(User user, String newPassword) {
        user.setPassWord(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Xóa token sau khi đã sử dụng
        PasswordResetToken token = tokenRepository.findByUser(user);
        if (token != null) {
            tokenRepository.delete(token);
        }
    }
}

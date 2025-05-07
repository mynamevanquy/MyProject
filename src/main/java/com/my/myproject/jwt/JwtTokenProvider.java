package com.my.myproject.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.my.myproject.model.User;
import com.my.myproject.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
	@Autowired
	private UserRepository userRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
	
	@Value("${app.jwt-secret}")
	private String jwtSecret;
	@Value("${app-jwt-expiration-milliseconds}")
	private long jwtExpirationDate;
	
	
	public String generateToken(Authentication authentication) {
		String username = authentication.getName();
		Date currentDate = new Date();
		 User user = userRepository.getUserIdByUserName(username);
		 String fullName = user.getName();
		 Long userId = user.getId();
	    Date expireDate = new Date(currentDate.getTime()+jwtExpirationDate);
	    String token = Jwts.builder().
	    		setSubject(username).
	    		claim("id", userId).
	    		claim("fullname", fullName).
	            claim("email", userRepository.findByEmail(username)).
	    		setIssuedAt(new Date()).
	    		setExpiration(expireDate).
	    		signWith(key()).compact();
	    		return token;
	    		
	    		

	}

	 public String getTokenForUser(String username) {
	        // Tạo token dựa trên tên người dùng
	        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
	        return generateToken(authentication);
	    }

	 private Key key(){
	        return Keys.hmacShaKeyFor(
	                Decoders.BASE64.decode(jwtSecret)
	        );
	    }
	
	public String getUserName(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(key())
				.build()
				.parseClaimsJws(token)
				.getBody();
		String username = claims.getSubject();
		return username;
	}
	
	public String getIdJwt(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(key())
				.build()
				.parseClaimsJws(token)
				.getBody();
		 Long id = claims.get("id", Long.class);
		return id.toString();
	}
		
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
			.setSigningKey(key())
			.build()
			.parse(token);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		}catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		}catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		}catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}
}

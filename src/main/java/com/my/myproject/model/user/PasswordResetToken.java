package com.my.myproject.model.user;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class PasswordResetToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String token;

	@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	private User user;

	private LocalDateTime expiryDate;

	public PasswordResetToken() {
		this.token = UUID.randomUUID().toString();
		this.expiryDate = LocalDateTime.now().plusHours(24); // Token hết hạn sau 24 giờ
	}

	public PasswordResetToken(User user) {
		this();
		this.user = user;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(this.expiryDate);
	}
}
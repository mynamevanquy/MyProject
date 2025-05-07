//package com.my.myproject.model;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "images")
//public class Image {
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//
//    private String imageName;
//
//    private String imageUrl;
//    
//    private String typeImage;
//    
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    @JsonBackReference
//    private User user;
//
//	public Image(Long id, String imageName, String imageUrl, String typeImage, User user) {
//		this.id = id;
//		this.imageName = imageName;
//		this.imageUrl = imageUrl;
//		this.typeImage = typeImage;
//		this.user = user;
//	}
//
//	public Image() {
//	}
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public String getImageName() {
//		return imageName;
//	}
//
//	public void setImageName(String imageName) {
//		this.imageName = imageName;
//	}
//
//	public String getImageUrl() {
//		return imageUrl;
//	}
//
//	public void setImageUrl(String imageUrl) {
//		this.imageUrl = imageUrl;
//	}
//
//	public String getTypeImage() {
//		return typeImage;
//	}
//
//	public void setTypeImage(String typeImage) {
//		this.typeImage = typeImage;
//	}
//
//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//    
//}

package com.my.myproject.model.user;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name= "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
}

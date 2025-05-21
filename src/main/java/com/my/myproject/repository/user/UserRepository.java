package com.my.myproject.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.my.myproject.model.user.User;


public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String userName);
	Boolean existsByEmail(String email);
//	boolean existsByUserName(String userName);
	Optional<User> findByName(String username);
	User findByEmail(String username);
	User getUserIdByUserName(String username);
	boolean existsByUserNameAndIdNot(String username, Long id);
	boolean existsByEmailAndIdNot(String email,Long id);
	@Query("SELECT u.userName FROM User u WHERE u.id = :id")
	String findUserNameById(@Param("id") Long id);
	boolean existsByUserName(String userName);

	
}

package com.example.crudapi.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.User;

public interface UserRepository extends JpaRepository<User,Long>{

	List<User> findByStatus(char c);

	Optional<User> findByIdAndStatus(Long id, char c);

	User findByEmail(String email);

	User findByPanNo(String panNo);

	static User findByMobileNo(String mobNo) {
		// TODO Auto-generated method stub
		return null;
	}

}

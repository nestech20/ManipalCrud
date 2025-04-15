package com.example.crudapi.demo.service;

import java.util.List;

import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.User;

public interface UserService {
	
	//post
	String addUser(UserDTO userDTO);
	
	
	//get
	List<User> getAllUser();
	
	
	//getbyid
	String getUserById(Long id);
	
	//put
	String updateUser(Long id,UserDTO userDTO);
	
	//delete
	void delete(Long id);
	
	
}

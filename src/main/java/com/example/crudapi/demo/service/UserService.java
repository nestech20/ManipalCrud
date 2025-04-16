package com.example.crudapi.demo.service;

import java.util.List;

import com.example.crudapi.demo.dto.UserDTO;

public interface UserService {
	
	//post
	String addUser(UserDTO userDTO);
	
	
	//get
	List<UserDTO> getAllUser();
	
	
	//getbyid
	String getUserById(Long id);
	
	//put
	String updateUser(Long id,UserDTO userDTO);
	
	//delete
	void delete(Long id);
	
	
}

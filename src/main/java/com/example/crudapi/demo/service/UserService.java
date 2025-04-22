package com.example.crudapi.demo.service;

import java.util.List;

import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.User;
import com.example.crudapi.demo.entity.UserListing;

public interface UserService {
	
	List<User> getAllUsersWithPagination(UserListing userListing);

	
	//post
	String addUser(UserDTO userDTO);
	
	
	//get
	List<UserDTO> getAllUser();
	
	
	//getbyid
	UserDTO getUserById(Long id);
	
	//put
	String updateUser(Long id,UserDTO userDTO);
	
	//delete
	void delete(Long id);
	
	
}

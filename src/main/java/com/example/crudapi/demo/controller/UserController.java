package com.example.crudapi.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.User;
import com.example.crudapi.demo.response.ResponseHandler;
import com.example.crudapi.demo.serviceimp.UserServiceImpl;




@RestController
@RequestMapping("/users")
public class UserController {

	
	
	@Autowired
	private UserServiceImpl userService;

	@PostMapping
	public ResponseHandler addUser(@RequestBody UserDTO userDTO ) {
		
		ResponseHandler handler = new ResponseHandler();
		
		try {
		String data= userService.addUser(userDTO);
			handler.setData(data);
			handler.setMessage("Succes");
			handler.setStatus(true);
			
		} catch (Exception e) {
			handler.setData( null);
			handler.setMessage("fail");
			handler.setStatus(false);
		}
		return handler;
	}
//	
//	
	@GetMapping
	public List<User> getAllUsers() {
		return userService.getAllUser();
	}

	@GetMapping("/{id}")
	public User getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}
	
	
	@DeleteMapping("/{id}")
	public String deleteUser(@PathVariable Long id) {
		userService.delete(id);
		return "user with id " + id+ " has been marked inactive";
	}

	
	@PutMapping("/{id}")
		public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
			try {
			User updateduser = userService.updateUser(id, user);
			return ResponseEntity.ok(updateduser);
			}catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
			}
				
	
		}
	
	
	
	
	
	
	
	
}

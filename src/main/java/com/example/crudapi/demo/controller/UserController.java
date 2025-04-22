package com.example.crudapi.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.UserListing;
import com.example.crudapi.demo.response.ResponseHandler;
import com.example.crudapi.demo.serviceimp.UserServiceImpl;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserServiceImpl userService;

	@PostMapping
	public ResponseHandler addUser(@RequestBody UserDTO userDTO) {

		ResponseHandler handler = new ResponseHandler();

		try {
			String data = userService.addUser(userDTO);
			handler.setData(data);
			handler.setMessage("Succes");
			handler.setStatus(true);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			handler.setData(new ArrayList<>());
			handler.setMessage(e.getMessage());
			handler.setStatus(false);
		} catch (Exception e) {
			e.printStackTrace();
			handler.setData(new ArrayList<>());
			handler.setMessage(e.getMessage());
			handler.setStatus(false);
		}

		return handler;
	}

	@PostMapping("/paginated")
	public ResponseHandler getAllUsersPaginated(@RequestBody UserListing userListing) {
	    ResponseHandler handler = new ResponseHandler();
	    try {
	        List<UserDTO> users = userService.getAllUsersWithPagination(userListing); // Assuming conversion to DTO is handled
	        handler.setData(users);
	        handler.setMessage("Success");
	        handler.setStatus(true);
	    } catch (Exception e) {
	        handler.setData(new ArrayList<>());
	        handler.setMessage("Failed to fetch users: " + e.getMessage());
	        handler.setStatus(false);
	    }
	    return handler;
	}


	@GetMapping("/{id}")
	public ResponseHandler getUserById(@PathVariable Long id) {

		ResponseHandler handler3 = new ResponseHandler();

		try {
			UserDTO xyz3 = userService.getUserById(id);
			handler3.setData(xyz3);
			handler3.setMessage("Succes");
			handler3.setStatus(true);
		} catch (Exception e) {
			handler3.setData(new ArrayList<>());
			handler3.setMessage("fail");
			handler3.setStatus(false);
		}

		return handler3;
	}

	@DeleteMapping("/{id}")
	public String deleteUser(@PathVariable Long id) {
		userService.delete(id);
		return "user with id " + id + " has been marked inactive";
	}

	@PutMapping("/{id}")
	public ResponseHandler updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {

		ResponseHandler handler4 = new ResponseHandler();

		try {
			String xyz4 = userService.updateUser(id, userDTO);
			handler4.setData(xyz4);
			handler4.setMessage("Success");
			handler4.setStatus(true);
		} catch (Exception e) {
			handler4.setData(new ArrayList<>());
			handler4.setMessage("Failed");
			handler4.setStatus(false);
		}

		return handler4;
	}

}

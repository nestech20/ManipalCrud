package com.example.crudapi.demo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// Importing DTO, entity, response handler, and service classes
import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.User;
import com.example.crudapi.demo.entity.UserListing;
import com.example.crudapi.demo.response.ResponseHandler;
import com.example.crudapi.demo.serviceimp.UserServiceImpl;

import jakarta.servlet.ServletException;

@RestController
@RequestMapping("/users") // Defines the base URL path for the user API
public class UserController {

	@Autowired
	private UserServiceImpl userService; // Injecting the service layer for business logic

	// ========================= GET all active users =========================
	@GetMapping
	public ResponseHandler getAllUsers() {

		ResponseHandler handler2 = new ResponseHandler();
		try {
			// Fetches all active users from the service layer
			List<UserDTO> data = userService.getAllUser();

			// Setting response attributes
			handler2.setMessage("Success");
			handler2.setStatus(true);
			handler2.setData(data);
		} catch (Exception e) {
			// Handles errors if any during the operation
			handler2.setData(new ArrayList<>());
			handler2.setMessage("Fail");
			handler2.setStatus(false);
		}

		return handler2;
	}

	// ========================= Add new user =========================
	@PostMapping
	public ResponseHandler addUser(@RequestBody UserDTO userDTO) {

		ResponseHandler handler = new ResponseHandler();

		try {
			// Calls the service layer to add a new user
			String data = userService.addUser(userDTO);
			handler.setMessage("Success");
			handler.setStatus(true);
			handler.setData(data);

		} catch (IllegalArgumentException e) {
			// Handles specific validation errors for invalid input
			e.printStackTrace();
			handler.setData(new ArrayList<>());
			handler.setMessage(e.getMessage());
			handler.setStatus(false);
		} catch (Exception e) {
			// General exception handling
			e.printStackTrace();
			handler.setData(new ArrayList<>());
			handler.setMessage(e.getMessage());
			handler.setStatus(false);
		}

		return handler;
	}

	// ========================= GET paginated users =========================
	@PostMapping("/paginated")
	public ResponseHandler getAllUsersPaginated(@RequestBody UserListing userListing) {
		ResponseHandler handler = new ResponseHandler();

		List<UserDTO> count = userService.getAllUser(); // Get total count of users
		int countUsers = count.size(); // Get the number of users
		try {
			// Fetch users based on pagination and filters from the service layer
			List<User> users = userService.fetchAllProposerByStringBuilder(userListing);

			// Assuming conversion to DTO is handled
			handler.setMessage("Success");
			handler.setStatus(true);
			handler.setData(users);

			// If there are filters applied, return the filtered count
			if (userListing.getUserFilter() != null) {
				userListing.setPageNo(0); // Set page size and page number to 0 for count only
				userListing.setPageSize(0);
				handler.setTotalRecord(userService.fetchAllProposerByStringBuilder(userListing).size());
			} else {
				// If no filters, return the total number of users
				handler.setTotalRecord(countUsers);
			}

		} catch (Exception e) {
			// Handles errors if any during the operation
			handler.setData(new ArrayList<>());
			handler.setMessage("Failed to fetch users: " + e.getMessage());
			handler.setStatus(false);
		}
		return handler;
	}

	// ========================= GET user by ID =========================
	@GetMapping("/{id}")
	public ResponseHandler getUserById(@PathVariable Long id) {

		ResponseHandler handler3 = new ResponseHandler();

		try {
			// Fetch user by ID from the service layer
			UserDTO xyz3 = userService.getUserById(id);
			handler3.setMessage("Success");
			handler3.setStatus(true);
			handler3.setData(xyz3);

		} catch (Exception e) {
			// Handles errors if any during the operation
			handler3.setData(new ArrayList<>());
			handler3.setMessage("Fail");
			handler3.setStatus(false);
		}

		return handler3;
	}

	// ========================= DELETE user =========================
	@DeleteMapping("/{id}")
	public String deleteUser(@PathVariable Long id) {
		// Deletes the user by marking them as inactive (soft delete)
		userService.delete(id);
		return "User with id " + id + " has been marked inactive";
	}

	// ========================= UPDATE user =========================
	@PutMapping("/{id}")
	public ResponseHandler updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {

		ResponseHandler handler4 = new ResponseHandler();

		try {
			// Calls the service layer to update the user
			String xyz4 = userService.updateUser(id, userDTO);
			handler4.setMessage("Success");
			handler4.setStatus(true);
			handler4.setData(xyz4);
		} catch (Exception e) {
			// Handles errors if any during the operation
			handler4.setData(new ArrayList<>());
			handler4.setMessage("Failed");
			handler4.setStatus(false);
		}

		return handler4;
	}

	// ================= Export USER Table in Excel =================
//	@GetMapping("/fileExport")
//	public void exportProposersToExcel(HttpServletResponse response) throws ServletException, IOException {
//
//		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//		response.setHeader("Content-Disposition", "attachment; filename=user_data.xlsx");
//
//		userService.exportProposersToExcel(response);
//
//	}
	
	@GetMapping("/fileExport")
	public ResponseHandler exportProposersToExcel() {
	    ResponseHandler handler = new ResponseHandler();
	    
	    try {
	    	String filepath = userService.exportUsersToExcel();
	        handler.setMessage("File exported successfully.");
	        handler.setStatus(true);
	        handler.setData(filepath);  // No data to return, just the success message
	    } catch (IOException | ServletException e) {
	        // In case of failure, return a failure message
	        handler.setMessage("Failed: " + e.getMessage());
	        handler.setStatus(false);
	    }
	    
	    return handler;
	}

	@PostMapping(value="/fileImport", consumes = "multipart/form-data")
	public ResponseEntity<ResponseHandler> importUsersFromExcel(@RequestParam("file") MultipartFile file) {
	    ResponseHandler handler = new ResponseHandler();

	    try (InputStream inputStream = file.getInputStream()) {
	        userService.importExcelToUser(inputStream);
	        handler.setMessage("Users imported successfully.");
	        handler.setStatus(true);
	        handler.setData(null); // Optional: Could return a count or list summary
	    } catch (IOException e) {
	        handler.setMessage("Import failed: " + e.getMessage());
	        handler.setStatus(false);
	        handler.setData(null);
	    }

	    return ResponseEntity.ok(handler);
	}



}

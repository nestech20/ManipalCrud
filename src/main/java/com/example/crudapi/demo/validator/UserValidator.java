package com.example.crudapi.demo.validator;

import java.time.LocalDateTime;
import java.util.IllegalFormatCodePointException;

import com.example.crudapi.demo.dto.UserDTO;

public class UserValidator {

	public static void validateUserDTO(UserDTO userDTO) {
		
		//FullName
		if (userDTO.getFullName() == null || userDTO.getFullName().trim().isEmpty()) {
			throw new IllegalArgumentException("FullName is required");			
		}
		
		//DOB
		if (userDTO.getDob() == null) {
			throw new IllegalArgumentException("DateOfBirth is required");
		}else if (userDTO.getDob().isAfter(java.time.LocalDate.now())) {
			throw new IllegalArgumentException("DateOfBirth cannot be in the Future Date");			
		}
		
		//Gender
		if (userDTO.getGender() == null) {
			throw new IllegalArgumentException("Gender is required");
			
		}
	   
		//Title
		if (userDTO.getTitle() == null) {
			throw new IllegalArgumentException("Title is required");
		}
		
		//Pan No
		if (userDTO.getPanNo() == null || !userDTO.getPanNo().matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
			throw new IllegalArgumentException("PAN number must be in valid format (ABCDE1234F).");			
		}
		
		//Annual Income
		if (userDTO.getAnnualIncome() == null || userDTO.getAnnualIncome() <= 0) {
			throw new IllegalArgumentException("Annual Income must be greter than 0.");
		}
		
		//Mobile No
		if (userDTO.getMobileNo() == null || !userDTO.getMobileNo().matches("[6-9][0-9]{9}")) {
			throw new IllegalArgumentException("Mobile number must be a valid 10-digit Indian number.");
		}

		//Email
		if (userDTO.getEmail() == null || !userDTO.getEmail().matches("^[A-Za-z0-9+.-]+@[A-Za-z0-9.-]+$")){
			throw new IllegalArgumentException("Email must be Valid.");
		}
		
		//Alternate No
		if (userDTO.getAlternateNo() != null || !userDTO.getAlternateNo().isEmpty()) {
			if (userDTO.getAlternateNo().matches("[6-9][0-9]{9}")) {
				 throw new IllegalArgumentException("Alternate number must be a valid 10-digit Indian number.");
			}
		}
		
		 // Address
	    if (userDTO.getAddress() == null || userDTO.getAddress().trim().isEmpty()) {
	        throw new IllegalArgumentException("Address is required.");
	    }

	    // Pincode
	    if (userDTO.getPincode() == null || !String.valueOf(userDTO.getPincode()).matches("^[1-9][0-9]{5}$")) {
	        throw new IllegalArgumentException("Pincode must be a valid 6-digit number.");
	    }

	    // City
	    if (userDTO.getCity() == null || userDTO.getCity().trim().isEmpty()) {
	        throw new IllegalArgumentException("City is required.");
	    }

	    // State
	    if (userDTO.getState() == null || userDTO.getState().trim().isEmpty()) {
	        throw new IllegalArgumentException("State is required.");
	    }

	    // Status
	    if (userDTO.getStatus() == null || !( userDTO.getStatus() == 'Y' || userDTO.getStatus() == 'N')) {
	        throw new IllegalArgumentException("Status must be valid (A/I/Y/N).");
	    }
	}
}

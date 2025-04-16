package com.example.crudapi.demo.serviceimp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.User;
import com.example.crudapi.demo.enums.Gender;
import com.example.crudapi.demo.enums.Title;
import com.example.crudapi.demo.repository.UserRepository;
import com.example.crudapi.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {
 
	@Autowired
	private UserRepository userRepository;
	
	@Override
    public String addUser(UserDTO userDTO) {
		
		User adddto = new User();
		
		String fullName = userDTO.getFullName();
		if (fullName == null || fullName.trim().isEmpty()) {
			throw new IllegalArgumentException("FullName is required");
		}adddto.setFullName(fullName);		
		
		LocalDate dob = userDTO.getDob();
		if (dob == null) {
			throw new IllegalArgumentException("DateOfBirth is required");
		}else if (dob.isAfter(java.time.LocalDate.now())) {
			throw new IllegalArgumentException("DateOfBirth cannot be in the Future Date");			
		}adddto.setDob(dob);
		
		String city= userDTO.getCity();
		if (city == null || city.trim().isEmpty()) {
	        throw new IllegalArgumentException("City is required.");
	    }adddto.setCity(city);
		
		String add = userDTO.getAddress();
		if (add == null || add.trim().isEmpty()) {
	        throw new IllegalArgumentException("Address is required.");
	    }adddto.setAddress(add);
		
		
		String email = userDTO.getEmail();
		if (email == null || !email.matches("^[A-Za-z0-9+.-]+@[A-Za-z0-9.-]+$")){
			throw new IllegalArgumentException("Email must be Valid.");
		}adddto.setEmail(email);
        
         Gender gender = userDTO.getGender();
        if (gender == null) {
			throw new IllegalArgumentException("Gender is required");	
		}adddto.setGender(gender);
        
        
       	Long annaualIncome = userDTO.getAnnualIncome();
        if (annaualIncome == null || annaualIncome <= 0) {
			throw new IllegalArgumentException("Annual Income must be greter than 0.");
		}adddto.setAnnualIncome(annaualIncome);
        
        String panNo = userDTO.getPanNo();
        if (panNo == null || !panNo.matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
			throw new IllegalArgumentException("PAN number must be in valid format (ABCDE1234F).");			
		}adddto.setPanNo(panNo);
        
        Long pinCode = userDTO.getPincode();
        if (pinCode == null || !String.valueOf(pinCode).matches("^[1-9][0-9]{5}$")) {
	        throw new IllegalArgumentException("Pincode must be a valid 6-digit number.");
	    }adddto.setPincode(pinCode);
        
        Title title = userDTO.getTitle();
        if (title == null) {
			throw new IllegalArgumentException("Title is required");
        }adddto.setTitle(title);
        
        String mobNo = userDTO.getMobileNo();
        if (mobNo == null || !mobNo.matches("[6-9][0-9]{9}")) {
			throw new IllegalArgumentException("Mobile number must be a valid 10-digit Indian number.");
		}adddto.setMobileNo(mobNo);
        
        String altNo = userDTO.getAlternateNo();
        if (altNo != null || !altNo.isEmpty()) {
			if (!altNo.matches("[6-9][0-9]{9}")) {
				 throw new IllegalArgumentException("Alternate number must be a valid 10-digit Indian number.");
			}
		}adddto.setAlternateNo(altNo);
        
        String state = userDTO.getState();
        if (state == null || state.trim().isEmpty()) {
	        throw new IllegalArgumentException("State is required.");
	    }adddto.setState(state);
        
        Character status = userDTO.getStatus();
        if (status == null || !( status == 'Y' || status == 'N')) {
	        throw new IllegalArgumentException("Status must be valid (Y/N).");
	    }adddto.setStatus(status);
        
        adddto.setCreatedDate(userDTO.getCreatedDate());
        adddto.setUpdatedDate(userDTO.getUpdatedDate());
        
        userRepository.save(adddto);
	   return "Added SuccessFully";
   }
   

	@Override
	public List<UserDTO> getAllUser() {
	    List<User> perlist = userRepository.findByStatus('Y');
	    List<UserDTO> adddtUserDTOs = new ArrayList<>();

	    for (User user : perlist) {
	        UserDTO addDto = new UserDTO();

	        addDto.setFullName(user.getFullName());
	        addDto.setDob(user.getDob());
	        addDto.setCity(user.getCity());
	        addDto.setAddress(user.getAddress());
	        addDto.setEmail(user.getEmail());
	        addDto.setGender(user.getGender());
	        addDto.setAnnualIncome(user.getAnnualIncome());
	        addDto.setPanNo(user.getPanNo());
	        addDto.setPincode(user.getPincode());
	        addDto.setTitle(user.getTitle());
	        addDto.setMobileNo(user.getMobileNo());
	        addDto.setAlternateNo(user.getAlternateNo());
	        addDto.setState(user.getState());
	        addDto.setStatus(user.getStatus());
	        addDto.setCreatedDate(user.getCreatedDate());
	        addDto.setUpdatedDate(user.getUpdatedDate());

	        adddtUserDTOs.add(addDto);
	    }

	    return adddtUserDTOs;
	}
   
	@Override
	public String getUserById(Long id) {
	    Optional<User> userOptional = userRepository.findById(id);
	    if (userOptional.isPresent()) {
	        User user = userOptional.get();
	        return "User found: " + user.getFullName() + ", Email: " + user.getEmail();
	    } else {
	        return "User not found with id: " + id;
	    }
	}



   
	@Override
    public String updateUser(@PathVariable Long id,@RequestBody UserDTO userDTO) {
		
         Optional<User> abc = userRepository.findByIdAndStatus(id,'Y');
		
		if(abc.isPresent()) {
			User existUser = abc.get();
			
			String fullName = userDTO.getFullName();
			if (fullName == null || fullName.trim().isEmpty()) {
				throw new IllegalArgumentException("FullName is required");
			}
			 existUser.setFullName(fullName);
			
			LocalDate dob = userDTO.getDob();
			if (dob == null) {
				throw new IllegalArgumentException("DateOfBirth is required");
			}else if (dob.isAfter(java.time.LocalDate.now())) {
				throw new IllegalArgumentException("DateOfBirth cannot be in the Future Date");			
			}
			 existUser.setDob(dob);
			
			String city= userDTO.getCity();
			if (city == null || city.trim().isEmpty()) {
		        throw new IllegalArgumentException("City is required.");
		    }
			 existUser.setCity(city);
			
			String add = userDTO.getAddress();
			if (add == null || add.trim().isEmpty()) {
		        throw new IllegalArgumentException("Address is required.");
		    }
			 existUser.setAddress(add);
			
			
			String email = userDTO.getEmail();
			if (email == null || !email.matches("^[A-Za-z0-9+.-]+@[A-Za-z0-9.-]+$")){
				throw new IllegalArgumentException("Email must be Valid.");
			}
	         existUser.setEmail(email);
	        
	         Gender gender = userDTO.getGender();
	        if (gender == null) {
				throw new IllegalArgumentException("Gender is required");
				
			}
	         existUser.setGender(gender);
	        
	        
	       	 Long annaualIncome = userDTO.getAnnualIncome();
	        if (annaualIncome == null || annaualIncome <= 0) {
				throw new IllegalArgumentException("Annual Income must be greter than 0.");
			}
	         existUser.setAnnualIncome(annaualIncome);
	        
	        String panNo = userDTO.getPanNo();
	        if (panNo == null || !panNo.matches("[A-Z]{5}[0-9]{4}[A-Z]")) {
				throw new IllegalArgumentException("PAN number must be in valid format (ABCDE1234F).");			
			}
	         existUser.setPanNo(panNo);
	        
	        Long pinCode = userDTO.getPincode();
	        if (pinCode == null || !String.valueOf(pinCode).matches("^[1-9][0-9]{5}$")) {
		        throw new IllegalArgumentException("Pincode must be a valid 6-digit number.");
		    }
	         existUser.setPincode(pinCode);
	        
	        Title title = userDTO.getTitle();
	        if (title == null) {
				throw new IllegalArgumentException("Title is required");
	        }        
	         existUser.setTitle(title);
	        
	        String mobNo = userDTO.getMobileNo();
	        if (mobNo == null || !mobNo.matches("[6-9][0-9]{9}")) {
				throw new IllegalArgumentException("Mobile number must be a valid 10-digit Indian number.");
			}        
	         existUser.setMobileNo(mobNo);
	        
	        String altNo = userDTO.getAlternateNo();
	        if (altNo != null || !altNo.isEmpty()) {
				if (!altNo.matches("[6-9][0-9]{9}")) {
					 throw new IllegalArgumentException("Alternate number must be a valid 10-digit Indian number.");
				}
			}
	         existUser.setAlternateNo(altNo);
	        
	        String state = userDTO.getState();
	        if (state == null || state.trim().isEmpty()) {
		        throw new IllegalArgumentException("State is required.");
		    }
	         existUser.setState(state);
	        
	        Character status = userDTO.getStatus();
	        if (status == null || !( status == 'Y' || status == 'N')) {
		        throw new IllegalArgumentException("Status must be valid (Y/N).");
		    }
	         existUser.setStatus(status);
	         
			existUser.setCreatedDate(userDTO.getCreatedDate());
			existUser.setUpdatedDate(userDTO.getUpdatedDate());	
			
			
			userRepository.save(existUser);
			
			
			return "Updated";
		}
		else {
			return "Not Updated";
		}
	
   }
	
	@Override
	public void delete(Long id) {
			Optional<User> optionalUser = userRepository.findById(id);
			if (optionalUser.isPresent()) {
				User user = optionalUser.get();
				user.setStatus('N');
				userRepository.save(user);	
		   }			
		}
}
	
	
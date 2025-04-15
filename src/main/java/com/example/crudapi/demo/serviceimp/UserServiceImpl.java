package com.example.crudapi.demo.serviceimp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.User;
import com.example.crudapi.demo.repository.UserRepository;
import com.example.crudapi.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {
 
	@Autowired
	private UserRepository userRepository;
	
	@Override
    public String addUser(UserDTO userDTO) {
		
		User adddto = new User();
		adddto.setFullName(userDTO.getFullName());
		adddto.setDob(userDTO.getDob());
		adddto.setCity(userDTO.getCity());
		adddto.setAddress(userDTO.getAddress());
        adddto.setEmail(userDTO.getEmail());
        adddto.setGender(userDTO.getGender());
        adddto.setAnnualIncome(userDTO.getAnnualIncome());
        adddto.setPanNo(userDTO.getPanNo());
        adddto.setPincode(userDTO.getPincode());
        adddto.setTitle(userDTO.getTitle());
        adddto.setMobileNo(userDTO.getMobileNo());
        adddto.setAlternateNo(userDTO.getAlternateNo());
        adddto.setState(userDTO.getState());
        adddto.setStatus(userDTO.getStatus());
        adddto.setCreatedDate(userDTO.getCreatedDate());
        adddto.setUpdatedDate(userDTO.getUpdatedDate());
        
        userRepository.save(adddto);
	   return "added";
   }
   
	@Override
    public List<User> getAllUser(){
	   return userRepository.findAll();
   }
   
	@Override
    public User getUserById(Long id) {
	   Optional<User> user = userRepository.findById(id);
	   return user.orElse(null);
	   
   }

   
	@Override
    public User updateUser(@PathVariable Long id,@RequestBody User updateUser) {
		return userRepository.findById(id)
				.map(user -> {
	                user.setFullName(updateUser.getFullName());
	                user.setPanNo(updateUser.getPanNo());
	                user.setEmail(updateUser.getEmail());
	                user.setMobileNo(updateUser.getMobileNo());
	                user.setAlternateNo(updateUser.getAlternateNo());
	                user.setAnnualIncome(updateUser.getAnnualIncome());
	                user.setDob(updateUser.getDob());
	                user.setGender(updateUser.getGender());
	                user.setTitle(updateUser.getTitle());
	                user.setAddress(updateUser.getAddress());
	                user.setPincode(updateUser.getPincode());
	                user.setState(updateUser.getState());
	                user.setCity(updateUser.getCity());
	                user.setStatus(updateUser.getStatus());
	                user.setCreatedDate(updateUser.getCreatedDate());
	                user.setUpdatedDate(updateUser.getUpdatedDate());
	                return userRepository.save(user);
	            })
	            .orElseThrow(() -> new RuntimeException("User not found with id " + id));
	
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


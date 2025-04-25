package com.example.crudapi.demo.service;

import java.util.List;

import com.example.crudapi.demo.dto.UserDTO;
import com.example.crudapi.demo.entity.User;
import com.example.crudapi.demo.entity.UserListing;

public interface UserService {
    
    // POST method to add a new user. Takes a UserDTO as input.
    // Returns a string indicating success or failure.
    String addUser(UserDTO userDTO);
    
    // GET method to retrieve all users. Returns a list of UserDTO objects.
    List<UserDTO> getAllUser();
    
    // GET method to retrieve a user by ID. Returns a UserDTO.
    UserDTO getUserById(Long id);
    
    // PUT method to update an existing user. Takes a user ID and a UserDTO as input.
    // Returns a string indicating success or failure.
    String updateUser(Long id, UserDTO userDTO);
    
    // DELETE method to delete a user by ID. Returns nothing.
    void delete(Long id);
    
    // Method to fetch all proposer users using StringBuilder for dynamic query building.
    // Takes a UserListing object (with filter, pagination, etc.) as input.
    // Returns a list of User entities.
    List<User> fetchAllProposerByStringBuilder(UserListing listing);
}

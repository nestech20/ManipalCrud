package com.example.crudapi.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.crudapi.demo.dto.UserDto;
import com.example.crudapi.demo.entity.User;
import com.example.crudapi.demo.entity.UserListing;

import jakarta.servlet.ServletException;

public interface UserService {

    /**
     * Adds a new user to the system.
     *
     * @param userDTO the data transfer object containing user information
     * @return a success message or relevant error description
     */
    String addUser(UserDto userDTO);

    /**
     * Retrieves all users (typically active ones).
     *
     * @return a list of UserDTOs representing all users
     */
    List<UserDto> getAllUser();

    /**
     * Retrieves a specific user by their unique ID.
     *
     * @param id the unique identifier of the user
     * @return a UserDTO containing user information
     */
    UserDto getUserById(Long id);

    /**
     * Updates an existing user's information.
     *
     * @param id the unique identifier of the user to be updated
     * @param userDTO the data transfer object containing updated user details
     * @return a success message or relevant error description
     */
    String updateUser(Long id, UserDto userDTO);

    /**
     * Performs a soft delete (or actual delete, depending on implementation) of a user by ID.
     *
     * @param id the unique identifier of the user to be deleted
     */
    void delete(Long id);

    /**
     * Retrieves a filtered and paginated list of proposer users using native SQL query
     * constructed with StringBuilder.
     *
     * @param listing the UserListing object containing filter, pagination, and sort criteria
     * @return a list of User entities matching the given criteria
     */
    List<User> fetchAllProposerByStringBuilder(UserListing listing);

    /**
     * Exports the filtered list of proposers to an Excel file and writes it to the response output stream.
     *
     * @param response the HttpServletResponse to write the Excel content to
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input or output error is detected
     */
    String exportUsersToExcel() throws ServletException, IOException;
   
  void importExcelToUser(InputStream file) throws IOException;
    
    
  String batchProcessing(MultipartFile file) throws IOException;
  
   
}
  
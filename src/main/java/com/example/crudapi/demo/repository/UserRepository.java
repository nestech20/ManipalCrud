package com.example.crudapi.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.crudapi.demo.dto.UserDto;
import com.example.crudapi.demo.entity.User;

/**
 * Repository interface for User entity.
 * Provides basic CRUD operations along with custom query methods.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves all users with the specified status.
     *
     * @param c status (e.g., 'A' for Active, 'I' for Inactive)
     * @return list of users with the given status
     */
    List<User> findByStatus(char c);

    /**
     * Retrieves a user by ID and status.
     *
     * @param id user ID
     * @param c status
     * @return an Optional containing the user if found
     */
    Optional<User> findByIdAndStatus(Long id, char c);

    /**
     * Finds a user by their email address.
     *
     * @param email the user's email
     * @return the User entity
     */
    User findByEmail(String email);

    /**
     * Finds a user by their PAN number.
     *
     * @param panNo the PAN number
     * @return the User entity
     */
    User findByPanNo(String panNo);

    /**
     * (Currently unused) Static method stub for mobile number lookup.
     * Note: This method does not work as intended and should be removed or refactored.
     *
     * @param mobNo the user's mobile number
     * @return null (placeholder)
     */
    static User findByMobileNo(String mobNo) {
        return null;
    }
    
    boolean existsByEmail(String email);
    boolean existsByMobileNo(String mobileNo);
    boolean existsByPanNo(String panNo);

    

}

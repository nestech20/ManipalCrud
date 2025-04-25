package com.example.crudapi.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.crudapi.demo.entity.Nominee;

/**
 * Repository interface for performing CRUD operations on Nominee entities.
 * Extends JpaRepository to leverage Spring Data JPA functionality.
 */
public interface NomineeRepository extends JpaRepository<Nominee, Long> {

    /**
     * Retrieves all nominees associated with a given user ID.
     * 
     * @param id the user ID
     * @return list of Nominees
     */
    List<Nominee> findByUserId(Long id);

    /**
     * Retrieves active nominees (e.g., status = 'A') for a specific user.
     * 
     * @param id the user ID
     * @param status nominee status (e.g., 'A' for Active)
     * @return list of active Nominees
     */
    List<Nominee> findByUserIdAndStatus(Long id, Character status);

    /**
     * Retrieves all nominees based on their status.
     * 
     * @param status nominee status (e.g., 'A', 'I')
     * @return list of Nominees by status
     */
    List<Nominee> findByStatus(Character status);
}

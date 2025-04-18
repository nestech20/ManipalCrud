package com.example.crudapi.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crudapi.demo.entity.Nominee;

public interface NomineeRepository extends JpaRepository<Nominee, Long> {

	List<Nominee> findByUserId(Long id);

}

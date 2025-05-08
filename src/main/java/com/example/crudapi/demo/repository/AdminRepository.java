package com.example.crudapi.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crudapi.demo.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long>{

}

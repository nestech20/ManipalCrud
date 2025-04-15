package com.example.crudapi.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crudapi.demo.entity.User;

public interface UserRepository extends JpaRepository<User,Long>{

}

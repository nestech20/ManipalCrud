package com.example.crudapi.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crudapi.demo.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{

}

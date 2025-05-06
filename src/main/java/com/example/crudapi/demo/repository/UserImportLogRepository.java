package com.example.crudapi.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crudapi.demo.entity.UserImportLog;

public interface UserImportLogRepository extends JpaRepository<UserImportLog, Long> {

	List<UserImportLog> findByRowNumber(int i);

}

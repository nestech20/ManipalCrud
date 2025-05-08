package com.example.crudapi.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crudapi.demo.entity.FileQueue;

public interface FileQueueRepository extends JpaRepository<FileQueue, Long> {
	
    // Fetch files with specific status
	List<FileQueue> findByIsProcessed(String isProcessed);

}


package com.example.crudapi.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FileQueue {
    
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long queueId;
    private String filePath;
    private String isProcessed;
    private int rowCount;
    private int rowRead;
    private String status;
    private int lastProcess;

    // Getters and setters
    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(String isProcessed) {
        this.isProcessed = isProcessed;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getRowRead() {
        return rowRead;
    }

    public void setRowRead(int rowRead) {
        this.rowRead = rowRead;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLastProcess() {
        return lastProcess;
    }

    public void setLastProcess(int lastProcess) {
        this.lastProcess = lastProcess;
    }
}

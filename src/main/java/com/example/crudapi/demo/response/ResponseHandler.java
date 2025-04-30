package com.example.crudapi.demo.response;

import java.util.List;

/**
 * ResponseHandler class for standardizing the structure of API responses.
 * Used for wrapping data, status, messages, and pagination information 
 * into a common response format.
 */
public class ResponseHandler {
    
    // Holds the data to be returned in the response
    private Object data;
    
    // Indicates the success/failure status of the request
    private boolean status;
    
    // Message containing additional information about the request (e.g., error or success message)
    private String message;
    
    public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	// Holds the total record count (useful for paginated responses)
    private Integer totalRecord;

    private List<String> errors;
    /**
     * Getter for data.
     * 
     * @return the data object
     */
    public Object getData() {
        return data;
    }

    /**
     * Setter for data.
     * 
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Getter for status.
     * 
     * @return the status of the response (true for success, false for failure)
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Setter for status.
     * 
     * @param status the status of the response (true for success, false for failure)
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Getter for the message.
     * 
     * @return the response message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for message.
     * 
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter for the total record count.
     * 
     * @return the total number of records (useful for pagination)
     */
    public Integer getTotalRecord() {
        return totalRecord;
    }

    /**
     * Setter for the total record count.
     * 
     * @param totalRecord the total record count to set
     */
    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

	public List<String> getErrors() {
		return errors;
	}
    
    
}

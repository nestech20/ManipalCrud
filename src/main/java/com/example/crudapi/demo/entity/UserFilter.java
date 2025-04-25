package com.example.crudapi.demo.entity;

/**
 * Filter class for dynamic querying of User entities.
 * This class is used to encapsulate filtering criteria 
 * such as full name, email, and mobile number.
 */
public class UserFilter {

    /**
     * Filter by full name (partial or exact match).
     */
	private String fullName;

    /**
     * Filter by email address.
     */
	private String email;

    /**
     * Filter by mobile number.
     */
	private String mobileNo;

    // --- Getters and Setters ---

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
}
